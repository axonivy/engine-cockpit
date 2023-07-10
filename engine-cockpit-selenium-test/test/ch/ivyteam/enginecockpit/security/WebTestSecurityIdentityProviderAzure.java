package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestSecurityIdentityProviderAzure {

  private static final String PASSWORD = "securityIdentityProviderForm:group:0:property:2:propertyPassword";
  private static final String CLIENT_ID = "securityIdentityProviderForm:group:0:property:1:propertyString";
  private static final String TENANT_ID = "securityIdentityProviderForm:group:0:property:0:propertyString";
  private final static String NAME = "test-azure";

  @BeforeEach
  void createAzureSystem() {
    login();
    Navigation.toSecuritySystem();
    createSecuritySystem("Azure Active Directory", NAME);
    Navigation.toSecuritySystemProvider(NAME);
  }

  @AfterEach
  void deleteAzureSystem() {
    WebTestSecuritySystem.deleteSecuritySystem(NAME);
  }

  @Test
  void editProperties() {
    stringProperty();
    Selenide.refresh();
    passwordProperty();
    Selenide.refresh();
    keyValueProperty();
  }

  private void stringProperty() {
    var property = $(By.id(TENANT_ID)).shouldBe(visible);
    property.clear();
    property.sendKeys("680be3d4-cc6a-43f3-ac51-286a03074142");
    save();
    property.shouldHave(exactValue("680be3d4-cc6a-43f3-ac51-286a03074142"));
    success();
  }

  private void passwordProperty() {
    var property = $(By.id(PASSWORD)).shouldBe(visible);
    property.clear();
    property.sendKeys("clientSecret");
    save();
    success();
    Selenide.refresh();
    property.shouldHave(attribute("placeholder", "************"));

    save();
    success();
    property.shouldHave(attribute("placeholder", "************"));

    Selenide.refresh();
    property.shouldHave(attribute("placeholder", "************"));
  }

  private void keyValueProperty() {
    var table = new Table(By.id("securityIdentityProviderForm:group:1:property:0:keyValueTable"));
    table.firstColumnShouldBe(CollectionCondition.empty);

    // add
    $(By.id("securityIdentityProviderForm:group:1:newPropertyKeyValue"))
            .shouldBe(visible)
            .click();
    $(By.id("identityProviderKeyValueForm:attributeNameInput"))
            .should(visible)
            .sendKeys("user property");
    $(By.id("identityProviderKeyValueForm:attributeValueInput"))
            .should(visible)
            .sendKeys("azure property");
    $(By.id("identityProviderKeyValueForm:savePropertyKeyAttribute"))
            .should(visible)
            .click();
    success();
    table.columnShouldBe(1, CollectionCondition.exactTexts("user property"));
    table.columnShouldBe(2, CollectionCondition.exactTexts("azure property"));

    // edit
    $(By.id("securityIdentityProviderForm:group:1:property:0:keyValueTable:0:editPropertyBtn"))
            .should(visible)
            .click();
    $(By.id("identityProviderKeyValueForm:attributeNameInput")).should(Condition.disabled);
    var p = $(By.id("identityProviderKeyValueForm:attributeValueInput"))
      .should(visible);
    p.clear();
    p.sendKeys("changed azure property");
    $(By.id("identityProviderKeyValueForm:savePropertyKeyAttribute"))
      .should(visible)
      .click();
    success();
    table.columnShouldBe(1, CollectionCondition.exactTexts("user property"));
    table.columnShouldBe(2, CollectionCondition.exactTexts("changed azure property"));

    // delete
    $(By.id("securityIdentityProviderForm:group:1:property:0:keyValueTable:0:deleteKeyValueBtn"))
      .click();
    success();
    table.firstColumnShouldBe(CollectionCondition.empty);
  }

  @Test
  void azureBrowserInvalidAuth(){
    $(By.id("securityIdentityProviderForm:group:0:property:3:browseDefaultContext")).should(visible)
    .click();
    $(By.id("directoryBrowser:directoryBrowserForm:directoryBrowserMessage")).shouldHave(text("ErrorInvalid UUID string:"));
    $(By.id("directoryBrowser:cancelDirectoryBrowser")).should(visible).click();
  }

  private void createSecuritySystem(String providerName, String securitySystemName) {
    $(By.id("form:createSecuritySystemBtn")).click();
    $(By.id("newSecuritySystemForm:newSecuritySystemNameInput")).sendKeys(securitySystemName);
    var provider = PrimeUi.selectOne(By.id("newSecuritySystemForm:newSecuritySystemProviderSelect"));
    provider.selectItemByLabel(providerName);
    $(By.id("newSecuritySystemForm:saveNewSecuritySystem")).click();
  }

  private void save() {
    $(By.id("securityIdentityProviderForm:group:0:save")).shouldBe(visible).click();
  }

  private void success() {
    $(By.id("securityIdentityProviderForm:securityIdentityProviderSaveSuccess_container")).shouldHave(text("Successfully saved"));
  }
}
