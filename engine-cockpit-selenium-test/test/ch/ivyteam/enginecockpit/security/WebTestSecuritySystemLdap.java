package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestSecuritySystemLdap {

  private static final String SAVE_BINDING_BTN = "securityIdentityProviderForm:group:1:save";
  private static final String SECURITY_SYSTEM_BINDING_GROWL = "#securityIdentityProviderForm\\:securityIdentityProviderSaveSuccess_container";
  private static final String IMPORT_USERS_OF_GROUP = "securityIdentityProviderForm:group:1:property:1:propertyString";
  private static final String DEFAULT_CONTEXT = "securityIdentityProviderForm:group:1:property:0:propertyString";

  private static final String LDAP_ATTRIBUTE_SAVE_BTN = "securityIdentityProviderForm:group:2:save";
  private static final String LDAP_ATTRIBUTE_SAVE_GRWOL = "securityIdentityProviderForm:securityIdentityProviderSaveSuccess_container";
  private static final String LDAP_ATTTRIBUTE_NAME = "securityIdentityProviderForm:group:2:property:1:propertyString";

  private static final String SAVE_CONNECTION_BTN = "securityIdentityProviderForm:group:0:save";
  private static final String CONNECTION_SAVE_GRWOL = "#securityIdentityProviderForm\\:securityIdentityProviderSaveSuccess_container";
  private static final String URL = "securityIdentityProviderForm:group:0:property:0:propertyString";
  private static final String USERNAME = "securityIdentityProviderForm:group:0:property:2:propertyString";
  private static final String USE_SSL = "securityIdentityProviderForm:group:0:property:5:propertyBoolean";
  private static final String ENABLE_INSECURE_SSL = "securityIdentityProviderForm:group:0:property:5:propertyBoolean";

  private static final String LDAP_USER_ATTRIBUTE_MODAL = "propertyKeyValueModal";
  private static final String NEW_LDAP_USER_ATTRIBUTE_BTN = "securityIdentityProviderForm:group:3:newPropertyKeyValue";
  private static final String SAVE_LDAP_USER_ATTRIBUTE = "identityProviderKeyValueForm:savePropertyKeyAttribute";

  public static final String DIRECTORY_BROWSER_DIALOG = "directoryBrowser:directoryBrowserDialog";
  public static final String DIRECTORY_BROWSER_FORM = "#directoryBrowser\\:directoryBrowserForm\\:";
  private static final String DIRECTORY_BROWSER_CHOOSE = "directoryBrowser:chooseDirectoryName";
  private static final String DIRECTORY_BROWSER_CANCEL = "directoryBrowser:cancelDirectoryBrowser";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toSecuritySystemLdap("test-ad");
  }

  @Test
  void securityLdap() {
    $$(".card").shouldHave(size(4));
  }

  @Test
  void connectionInfos() {
    $(By.id(URL)).shouldBe(exactValue("ldap://test-ad.ivyteam.io"));
    $(By.id(USERNAME)).shouldBe(exactValue("admin@zugtstdomain.wan"));
    $(By.id("securityIdentityProviderForm:group:0:property:3:propertyPassword")).shouldBe(exactValue(""));

    $(By.id(URL)).clear();
    $(By.id(URL)).sendKeys("test");
    saveConnection();
    Selenide.refresh();

    $(By.id(URL)).shouldBe(exactValue("test"));
    $(By.id(URL)).clear();
    $(By.id(URL)).sendKeys("ldap://test-ad.ivyteam.io");
    saveConnection();
  }

  @Test
  void ldapAttributes() {
    $(By.id(LDAP_ATTTRIBUTE_NAME)).shouldBe(exactValue(""));
    $(By.id("securityIdentityProviderForm:group:2:property:2:propertyString")).shouldBe(exactValue(""));
    $(By.id("securityIdentityProviderForm:group:2:property:3:propertyString")).shouldBe(exactValue(""));
    $(By.id("securityIdentityProviderForm:group:2:property:4:propertyString")).shouldBe(exactValue(""));
    $(By.id("securityIdentityProviderForm:group:4:property:0:propertyString")).shouldBe(exactValue(""));
    PrimeUi.selectBooleanCheckbox(By.id("securityIdentityProviderForm:group:4:property:1:propertyBoolean"))
            .shouldBeChecked(true);
    $(By.id("securityIdentityProviderForm:group:4:property:2:propertyString")).shouldBe(exactValue(""));
    $(By.id("securityIdentityProviderForm:group:4:property:3:propertyString")).shouldBe(exactValue(""));

    $(By.id(LDAP_ATTTRIBUTE_NAME)).sendKeys("test");
    $(By.id(LDAP_ATTRIBUTE_SAVE_BTN)).click();
    $(By.id(LDAP_ATTRIBUTE_SAVE_GRWOL)).shouldBe(visible);
    Selenide.refresh();

    $(By.id(LDAP_ATTTRIBUTE_NAME)).shouldBe(exactValue("test"));
    $(By.id(LDAP_ATTTRIBUTE_NAME)).clear();
    $(By.id(LDAP_ATTRIBUTE_SAVE_BTN)).click();
    $(By.id(LDAP_ATTRIBUTE_SAVE_GRWOL)).shouldBe(visible);
  }

  @Test
  void binding() {
    $(By.id(DEFAULT_CONTEXT)).shouldBe(exactValue("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
    $(By.id(IMPORT_USERS_OF_GROUP)).shouldBe(exactValue(""));
    $(By.id("securityIdentityProviderForm:group:1:property:2:propertyString")).shouldBe(exactValue(""));

    $(By.id(IMPORT_USERS_OF_GROUP)).sendKeys("test");
    saveBinding();
    Selenide.refresh();

    $(By.id(IMPORT_USERS_OF_GROUP)).shouldBe(exactValue("test"));
    $(By.id(IMPORT_USERS_OF_GROUP)).clear();
    saveBinding();
  }

  @Test
  void ldapUserAttributesNewInvalid() {
    $(By.id(NEW_LDAP_USER_ATTRIBUTE_BTN)).shouldBe(visible).click();
    $(By.id(LDAP_USER_ATTRIBUTE_MODAL)).shouldBe(visible);
    $(By.id("identityProviderKeyValueForm:attributeNameInput")).shouldBe(empty);
    $(By.id("identityProviderKeyValueForm:attributeValueInput")).shouldBe(empty);

    $(By.id(SAVE_LDAP_USER_ATTRIBUTE)).click();
    $(By.id("identityProviderKeyValueForm:attributeNameMessage")).shouldBe(text("Value is required"));
    $(By.id("identityProviderKeyValueForm:attributeValueMessage")).shouldBe(text("Value is required"));
  }

  @Test
  void ldapUserAttributes() {
    Table table = new Table(By.id("securityIdentityProviderForm:group:3:property:0:keyValueTable"));
    assertThat(table.getFirstColumnEntries()).hasSize(2);

    $(By.id(NEW_LDAP_USER_ATTRIBUTE_BTN)).click();
    $(By.id(LDAP_USER_ATTRIBUTE_MODAL)).shouldBe(visible);
    $(By.id("identityProviderKeyValueForm:attributeNameInput")).sendKeys("test");
    $(By.id("identityProviderKeyValueForm:attributeValueInput")).sendKeys("value");
    $(By.id(SAVE_LDAP_USER_ATTRIBUTE)).click();
    assertThat(table.getFirstColumnEntries()).hasSize(3).contains("test");
    table.valueForEntryShould("test", 2, exactText("value"));

    table.clickButtonForEntry("test", "editPropertyBtn");
    $(By.id(LDAP_USER_ATTRIBUTE_MODAL)).shouldBe(visible);
    $(By.id("identityProviderKeyValueForm:attributeValueInput")).clear();
    $(By.id("identityProviderKeyValueForm:attributeValueInput")).sendKeys("newValue");
    $(By.id(SAVE_LDAP_USER_ATTRIBUTE)).click();
    table.valueForEntryShould("test", 2, exactText("newValue"));

    table.clickButtonForEntry("test", "deleteKeyValueBtn");
    assertThat(table.getFirstColumnEntries()).hasSize(2).doesNotContain("test");
  }

  @Test
  void ldapBrowser_wrongConfig() {
    $(By.id(URL)).clear();
    $(By.id(URL)).sendKeys("ldap://test-ad.ivyteam.io2");
    saveConnection();
    try {
      openLdapBrowserWithConnError();
    } finally {
      $(By.id(URL)).clear();
      $(By.id(URL)).sendKeys("ldap://test-ad.ivyteam.io");
      saveConnection();
    }
    $(By.id(USERNAME)).clear();
    $(By.id(USERNAME)).sendKeys("bla");
    saveConnection();
    openLdapBrowserWithConnError();
    $(By.id(USERNAME)).clear();
    $(By.id(USERNAME)).sendKeys("admin@zugtstdomain.wan");
    saveConnection();
    openDefaultLdapBrowser();
    $(DIRECTORY_BROWSER_FORM + "ldapConnectionFailMessage").shouldNotBe(visible);
  }

  @Test
  void ldapBrowser_ssl() {
    $(By.id(URL)).clear();
    $(By.id(URL)).sendKeys("ldaps://test-ad.ivyteam.io:637"); // 637 for self-signed
                                                       // certificate
    $(By.id(USE_SSL)).click();
    saveConnection();
    openLdapBrowserWithConnError();
    $(By.id(ENABLE_INSECURE_SSL)).click();
    saveConnection();
    openDefaultLdapBrowser();
    $(DIRECTORY_BROWSER_FORM + "ldapConnectionFailMessage").shouldNotBe(visible);
    $(By.id(DIRECTORY_BROWSER_CHOOSE)).click();
    $(By.id(URL)).clear();
    $(By.id(URL)).sendKeys("ldap://test-ad.ivyteam.io");
    $(By.id(USE_SSL)).click();
    $(By.id(ENABLE_INSECURE_SSL)).click();
    saveConnection();
  }

  @Test
  void ldapBrowser_chooseDefaultContext() {
    $(By.id(DEFAULT_CONTEXT)).clear();
    openDefaultLdapBrowser();
    $$(DIRECTORY_BROWSER_FORM + "tree > ul > li").shouldHave(size(3));
    $(DIRECTORY_BROWSER_FORM + "tree\\:2 .ui-tree-toggler").click();
    $(DIRECTORY_BROWSER_FORM + "tree\\:2 .ui-treenode-children").findAll(".ui-treenode-label")
            .find(text("OU=IvyTeam Test-OU")).click();
    $(DIRECTORY_BROWSER_FORM + "tree\\:2 .ui-treenode-children").findAll(".ui-treenode-label")
            .find(text("OU=IvyTeam Test-OU")).shouldHave(cssClass("ui-state-highlight"));
    $(By.id(DIRECTORY_BROWSER_CHOOSE)).scrollTo().click();
    $(By.id(DIRECTORY_BROWSER_DIALOG)).shouldNotBe(visible);
    $(By.id(DEFAULT_CONTEXT)).shouldBe(exactValue("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
  }

  @Test
  void browseEscapedNames() {
      openDefaultLdapBrowser();
      var treeNode = $$(DIRECTORY_BROWSER_FORM + "tree .ui-treenode-label").find(exactText("OU=issue22622"));
      treeNode.shouldBe(visible);
      treeNode.parent().$(".ui-tree-toggler").click();
      treeNode.parent().parent().$(".ui-treenode-children").findAll("li")
          .shouldBe(sizeGreaterThan(0));

      treeNode = $$(DIRECTORY_BROWSER_FORM + "tree .ui-treenode-label").find(exactText("OU=issue25327"));
      treeNode.shouldBe(visible);
      treeNode.parent().$(".ui-tree-toggler").click();
      treeNode.parent().parent().$(".ui-treenode-children").findAll("li")
          .shouldBe(sizeGreaterThan(0));
  }

  @Test
  void ldapBrowser_initDefaultContext() {
    openDefaultLdapBrowser();
    $$(DIRECTORY_BROWSER_FORM + "tree .ui-treenode-label").find(exactText("OU=IvyTeam Test-OU"))
            .shouldBe(visible, cssClass("ui-state-highlight"));
    Table table = new Table(By.id("directoryBrowser:directoryBrowserForm:nodeAttrTable"));
    table.tableEntry("distinguishedName", 2).shouldBe(exactText("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
  }

  @Test
  void ldapBrowser_chooseImportUsersOfGroup() {
    $(By.id(DEFAULT_CONTEXT)).shouldBe(exactValue("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
    openImportLdapBrowser();
    $(DIRECTORY_BROWSER_FORM + "tree\\:0").shouldHave(text("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
    $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-tree-toggler").click();
    $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode").shouldHave(size(11));
    $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode-label")
            .find(text("CN=role1")).click();
    $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode-label")
            .find(text("CN=role1")).shouldHave(cssClass("ui-state-highlight"));
    $(By.id(DIRECTORY_BROWSER_CHOOSE)).click();
    $(By.id(DIRECTORY_BROWSER_DIALOG)).shouldNotBe(visible);
    $(By.id(IMPORT_USERS_OF_GROUP)).shouldBe(exactValue("CN=role1,OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
  }

  @Nested
  class LdapBrowserNovell {
    @BeforeEach
    void beforeEach() {
      Navigation.toSecuritySystemLdap("test-nd");
    }

    @Test
    void ldapBrowser_wrongConfig() {
      $(By.id(URL)).clear();
      $(By.id(URL)).sendKeys("ldap://test-edirectory.ivyteam.io2");
      saveConnection();
      try {
        openLdapBrowserWithConnError();
      } finally {
        $(By.id(URL)).clear();
        $(By.id(URL)).sendKeys("ldap://test-edirectory.ivyteam.io:389");
        saveConnection();
      }
      $(By.id(USERNAME)).clear();
      $(By.id(USERNAME)).sendKeys("bla");
      saveConnection();
      openLdapBrowserWithConnError();
      $(By.id(USERNAME)).clear();
      $(By.id(USERNAME)).sendKeys("cn=admin,o=org");
      saveConnection();
      openDefaultLdapBrowser();
      $(DIRECTORY_BROWSER_FORM + "ldapConnectionFailMessage").shouldNotBe(visible);
    }

    @Test
    void ldapBrowser_chooseImportUsersOfGroup() {
      $(By.id(DEFAULT_CONTEXT)).shouldBe(exactValue("ou=IvyTeam Test-OU,o=zugtstorg"));
      openImportLdapBrowser();
      $(DIRECTORY_BROWSER_FORM + "tree\\:0").shouldHave(text("ou=IvyTeam Test-OU,o=zugtstorg"));
      $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-tree-toggler").click();
      $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode").shouldHave(size(10));
      $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode-label")
              .find(text("cn=role1")).click();
      $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode-label")
              .find(text("cn=role1")).shouldHave(cssClass("ui-state-highlight"));
      $(By.id(DIRECTORY_BROWSER_CHOOSE)).click();
      $(By.id(DIRECTORY_BROWSER_DIALOG)).shouldNotBe(visible);
      $(By.id(IMPORT_USERS_OF_GROUP)).shouldBe(exactValue("cn=role1,ou=IvyTeam Test-OU,o=zugtstorg"));
    }

    @Test
    void ldapBrowser_initImportUsersOfGroup() {
      $(By.id(IMPORT_USERS_OF_GROUP)).sendKeys("cn=role1,ou=IvyTeam Test-OU,o=zugtstorg");
      openImportLdapBrowser();
      $$(DIRECTORY_BROWSER_FORM + "tree .ui-treenode-label").find(exactText("CN=role1"))
              .shouldBe(visible, cssClass("ui-state-highlight"));
      Table table = new Table(By.id("directoryBrowser:directoryBrowserForm:nodeAttrTable"));
      table.tableEntry("cn", 2).shouldBe(exactText("role1"));
    }

    @Test
    void ldapBrowser_chooseDefaultContext() {
      $(By.id(DEFAULT_CONTEXT)).clear();
      openDefaultLdapBrowser();
      $$(DIRECTORY_BROWSER_FORM + "tree > ul > li").shouldHave(size(1));
      $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-tree-toggler").click();
      $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode").shouldHave(size(4));
      $(DIRECTORY_BROWSER_FORM + "tree\\:0_3 .ui-tree-toggler").click();
      $$(DIRECTORY_BROWSER_FORM + "tree\\:0_3 .ui-treenode .ui-treenode-label")
              .find(text("ou=IvyTeam Test-OU")).click();
      $$(DIRECTORY_BROWSER_FORM + "tree\\:0_3 .ui-treenode .ui-treenode-label")
              .find(text("ou=IvyTeam Test-OU")).shouldHave(cssClass("ui-state-highlight"));
      $(By.id(DIRECTORY_BROWSER_CHOOSE)).scrollTo().click();
      $(By.id(DIRECTORY_BROWSER_DIALOG)).shouldNotBe(visible);
      $(By.id(DEFAULT_CONTEXT)).shouldBe(exactValue("ou=IvyTeam Test-OU,o=zugtstorg"));
    }

    @Test
    void ldapBrowser_initDefaultContext() {
      openDefaultLdapBrowser();
      $$(DIRECTORY_BROWSER_FORM + "tree .ui-treenode-label").find(exactText("ou=IvyTeam Test-OU"))
              .shouldBe(visible, cssClass("ui-state-highlight"));
      Table table = new Table(By.id("directoryBrowser:directoryBrowserForm:nodeAttrTable"));
      table.tableEntry("ou", 2).shouldBe(exactText("IvyTeam Test-OU"));
    }

    @Test
    void ldapBrowser_attributes() {
      $(By.id(DEFAULT_CONTEXT)).clear();
      openDefaultLdapBrowser();
      Table table = new Table(By.id("directoryBrowser:directoryBrowserForm:nodeAttrTable"));
      table.firstColumnShouldBe(CollectionCondition.empty);
      $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-tree-toggler").click();
      $(DIRECTORY_BROWSER_FORM + "tree\\:0_3 .ui-tree-selectable").click();
      table.valueForEntryShould("o", 2, exactText("zugtstorg"));
    }
  }

  private void saveConnection() {
    $(By.id(SAVE_CONNECTION_BTN)).scrollIntoView("{block: \"center\"}").click();
    $(CONNECTION_SAVE_GRWOL).shouldBe(visible);
    Selenide.executeJavaScript("arguments[0].click();", $(CONNECTION_SAVE_GRWOL + " .ui-growl-icon-close"));
  }

  private void saveBinding() {
    $(By.id(SAVE_BINDING_BTN)).click();
    $(SECURITY_SYSTEM_BINDING_GROWL).shouldBe(visible);
    Selenide.executeJavaScript("arguments[0].click();", $(SECURITY_SYSTEM_BINDING_GROWL + " .ui-growl-icon-close"));
  }

  private void openImportLdapBrowser() {
    $("#securityLdapBindingForm\\:browseImportUserOfGroup").shouldBe(visible).click();
    $(By.id(DIRECTORY_BROWSER_DIALOG)).shouldBe(visible);
  }

  private void openDefaultLdapBrowser() {
    $(By.id("securityIdentityProviderForm:group:1:property:0:browseDefaultContext")).shouldBe(visible).click();
    $(By.id(DIRECTORY_BROWSER_DIALOG)).shouldBe(visible);
  }

  private void openLdapBrowserWithConnError() {
    openDefaultLdapBrowser();
    try {
//      $(DIRECTORY_BROWSER_FORM + "directoryBrowserMessage").shouldBe(visible).shouldNotBe(empty);
//      $(By.id(DIRECTORY_BROWSER_CHOOSE)).shouldBe(disabled);
    } finally {
      $(By.id(DIRECTORY_BROWSER_CANCEL)).click();
    }
  }
}
