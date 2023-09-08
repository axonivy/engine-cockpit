package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestSecurityIdentityProvider {

  private static final String PASSWORD = "securityIdentityProviderForm:group:0:property:1:propertyPassword";
  private static final String TENANT_ID = "securityIdentityProviderForm:group:0:property:0:propertyString";
  private final static String NAME = "test-security-system";
  private static final String DIRECTORY_BROWSER_FORM = "#directoryBrowser\\:directoryBrowserForm\\:";

  @BeforeEach
  void createSecuritySystem() {
    EngineCockpitUtil.registerDummyIdentityProvider();
    login();
    Navigation.toSecuritySystem();
    createSecuritySystem("Dummy Identity Provider", NAME);
    Navigation.toSecuritySystemProvider(NAME);
  }

  @AfterEach
  void deleteSecuritySystem() {
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
  void keyValuePropertyInvalid() {
    $(By.id("securityIdentityProviderForm:group:1:newPropertyKeyValue")).shouldBe(visible).click();
    $(By.id("propertyKeyValueModal")).shouldBe(visible);
    $(By.id("identityProviderKeyValueForm:attributeNameInput")).shouldBe(empty);
    $(By.id("identityProviderKeyValueForm:attributeValueInput")).shouldBe(empty);

    $(By.id("identityProviderKeyValueForm:savePropertyKeyAttribute")).click();
    $(By.id("identityProviderKeyValueForm:attributeNameMessage")).shouldBe(text("Value is required"));
    $(By.id("identityProviderKeyValueForm:attributeValueMessage")).shouldBe(text("Value is required"));
    $(By.id("identityProviderKeyValueForm:cancelPropertyKeyValue")).click();
  }

  @Test
  void directoryBrowser(){
    $(By.id("securityIdentityProviderForm:group:0:property:2:browseDefaultContext")).should(visible)
    .click();
    $(By.id("directoryBrowser:directoryBrowserForm:tree:0")).should(visible)
    .click();
    $(By.id("directoryBrowser:directoryBrowserForm:tree:0")).shouldHave(text("Group A"));
    By.id("directoryBrowser:directoryBrowserForm:tree:0");
    $(By.className("ui-tree-toggler")).click();

    $(By.id("directoryBrowser:directoryBrowserForm:tree:0_0")).shouldHave(text("Group A.1")).click();
    $(By.id("directoryBrowser:directoryBrowserForm:nodeAttrTable_data")).shouldHave(text("location"));
    $(By.id("directoryBrowser:directoryBrowserForm:nodeAttrTable_data")).shouldHave(text("Zug"));

    $(By.id("directoryBrowser:cancelDirectoryBrowser")).should(visible);
    $(By.id("directoryBrowser:chooseDirectoryName")).should(visible).click();
    $(By.id("securityIdentityProviderForm:group:0:property:2:propertyString")).shouldHave(value("Group A.1"));
  }

  @Test
  void booleanProperty() {
    var bool = $(By.cssSelector(".ui-chkbox-box.ui-widget.ui-corner-all.ui-state-default")).should(visible);
    bool.click();
    PrimeUi.selectBooleanCheckbox(By.id("securityIdentityProviderForm:group:0:property:4:propertyBoolean"))
    .shouldBeChecked(true);
    save();
    success();
  }

  @Test
  void booleanPropertyDefaultValue() {
    $(By.id("securityIdentityProviderForm:group:0:property:5:propertyBoolean")).should(visible);
    PrimeUi.selectBooleanCheckbox(By.id("securityIdentityProviderForm:group:0:property:5:propertyBoolean"))
    .shouldBeChecked(true);
    save();
    success();
  }

  @Test
  void numberProperty() {
    var property = $(By.id("securityIdentityProviderForm:group:0:property:3:propertyNumber_input")).shouldBe(visible);
    property.clear();
    property.sendKeys("123");
    save();
    success();
    property.shouldHave(exactValue("123"));
    property.sendKeys("abc");
    property.shouldHave(exactValue("123"));
  }

  @Test
  void dropdownProperty() {
    var property = $(By.id("securityIdentityProviderForm:group:0:property:6:propertyDropdown"))
            .shouldBe(visible)
            .shouldBe(text("DIRECT"));
    property.click();
    $(By.id("securityIdentityProviderForm:group:0:property:6:propertyDropdown_2")).click();
    property.shouldHave(text("TRAVERSE"));
  }

  @Test
  void browseEscapedNames() {
    $(By.id("securityIdentityProviderForm:group:0:property:2:browseDefaultContext")).should(visible)
    .click();
    $(By.id("directoryBrowser:directoryBrowserDialog")).shouldBe(visible);
      var treeNode = $$(DIRECTORY_BROWSER_FORM + "tree .ui-treenode-label").find(exactText("Group A"));
      treeNode.shouldBe(visible);
      treeNode.parent().$(".ui-tree-toggler").click();
      treeNode.parent().parent().$(".ui-treenode-children").findAll("li")
          .shouldBe(sizeGreaterThan(0));

      treeNode = $$(DIRECTORY_BROWSER_FORM + "tree .ui-treenode-label").find(exactText("Group A.1"));
      treeNode.shouldBe(visible);
      $(By.id("directoryBrowser:cancelDirectoryBrowser")).click();
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
