package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest(headless = false)
class WebTestSecurityIdentityProviderAzure {

  private final static String NAME = "test-azure";

  @Test
  void edit() {
    login();
    Navigation.toSecuritySystem();
    createSecuritySystem("Azure Active Directory", NAME);
    Navigation.toSecuritySystemProvider(NAME);
    stringProperty();
    Selenide.refresh();
    passwordProperty();
    Selenide.refresh();
    keyValueProperty();
    WebTestSecuritySystem.deleteSecuritySystem(NAME);
  }

  private void stringProperty() {
    var property = $("#securityIdentityProviderForm\\:group\\:0\\:property\\:0\\:propertyString").shouldBe(visible);
    property.clear();
    property.sendKeys("tenantId");
    save();
    property.shouldHave(exactValue("tenantId"));
    success();
  }

  private void passwordProperty() {
    var property = $("#securityIdentityProviderForm\\:group\\:0\\:property\\:2\\:propertyPassword").shouldBe(visible);
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
    $("#securityIdentityProviderForm\\:group\\:1\\:newPropertyKeyValue")
            .shouldBe(visible)
            .click();
    $("#identityProviderKeyValueForm\\:attributeNameInput")
            .should(visible)
            .sendKeys("user property");
    $("#identityProviderKeyValueForm\\:attributeValueInput")
            .should(visible)
            .sendKeys("azure property");
    $("#identityProviderKeyValueForm\\:savePropertyKeyAttribute")
            .should(visible)
            .click();
    success();
    table.columnShouldBe(1, CollectionCondition.exactTexts("user property"));
    table.columnShouldBe(2, CollectionCondition.exactTexts("azure property"));

    // edit
    $("#securityIdentityProviderForm\\:group\\:1\\:property\\:0\\:keyValueTable\\:0\\:editPropertyBtn")
            .should(visible)
            .click();
    $("#identityProviderKeyValueForm\\:attributeNameInput").should(Condition.disabled);
    var p = $("#identityProviderKeyValueForm\\:attributeValueInput")
      .should(visible);
    p.clear();
    p.sendKeys("changed azure property");
    $("#identityProviderKeyValueForm\\:savePropertyKeyAttribute")
      .should(visible)
      .click();
    success();
    table.columnShouldBe(1, CollectionCondition.exactTexts("user property"));
    table.columnShouldBe(2, CollectionCondition.exactTexts("changed azure property"));

    // delete
    $("#securityIdentityProviderForm\\:group\\:1\\:property\\:0\\:keyValueTable\\:0\\:deleteKeyValueBtn")
      .click();
    success();
    table.firstColumnShouldBe(CollectionCondition.empty);
  }

  private void createSecuritySystem(String providerName, String securitySystemName) {
    $("#form\\:createSecuritySystemBtn").click();
    $("#newSecuritySystemForm\\:newSecuritySystemNameInput").sendKeys(securitySystemName);
    var provider = PrimeUi.selectOne(By.id("newSecuritySystemForm:newSecuritySystemProviderSelect"));
    provider.selectItemByLabel(providerName);
    $("#newSecuritySystemForm\\:saveNewSecuritySystem").click();
  }

  private void save() {
    $("#securityIdentityProviderForm\\:group\\:0\\:save").shouldBe(visible).click();
  }

  private void success() {
    $("#securityIdentityProviderForm\\:securityIdentityProviderSaveSuccess_container").shouldHave(text("Successfully saved"));
  }
}
