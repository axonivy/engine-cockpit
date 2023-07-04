package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.disabled;
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

  private static final String SAVE_BINDING_BTN = "#securityLdapBindingForm\\:saveLdapBindingBtn";
  private static final String SECURITY_SYSTEM_BINDING_GROWL = "#securityLdapBindingForm\\:securityLdapBindingSaveSuccess_container";
  private static final String IMPORT_USERS_OF_GROUP = "#securityLdapBindingForm\\:importUsersOfGroup";
  private static final String DEFAULT_CONTEXT = "#securityLdapBindingForm\\:defaultContext";

  private static final String LDAP_ATTRIBUTE_SAVE_BTN = "#securityLdapAttributesForm\\:saveSecurtiySystemLdapBtn";
  private static final String LDAP_ATTRIBUTE_SAVE_GRWOL = "#securityLdapAttributesForm\\:securityLdapAttributesSaveSuccess_container";
  private static final String LDAP_ATTTRIBUTE_NAME = "#securityLdapAttributesForm\\:ldapName";

  private static final String SAVE_CONNECTION_BTN = "#securityLdapConnectionForm\\:saveLdapConnectionBtn";
  private static final String CONNECTION_SAVE_GRWOL = "#securityLdapConnectionForm\\:securityLdapConnectionSaveSuccess_container";
  private static final String URL = "#securityLdapConnectionForm\\:url";
  private static final String USERNAME = "#securityLdapConnectionForm\\:userName";
  private static final String USE_SSL = "#securityLdapConnectionForm\\:useSsl";
  private static final String ENABLE_INSECURE_SSL = "#securityLdapConnectionForm\\:enableInsecureSsl";

  private static final String LDAP_USER_ATTRIBUTE_MODAL = "#ldapUserAttributeModal";
  private static final String NEW_LDAP_USER_ATTRIBUTE_BTN = "#securityLdapUserAttributesForm\\:newLdapUserAttributeBtn";
  private static final String SAVE_LDAP_USER_ATTRIBUTE = "#ldapUserAttributeForm\\:saveLdapUserAttribute";

  public static final String DIRECTORY_BROWSER_DIALOG = "#directoryBrowser\\:directoryBrowserDialog";
  public static final String DIRECTORY_BROWSER_FORM = "#directoryBrowser\\:directoryBrowserForm\\:";
  private static final String DIRECTORY_BROWSER_CHOOSE = "#directoryBrowser\\:chooseDirectoryName";
  private static final String DIRECTORY_BROWSER_CANCEL = "#directoryBrowser\\:cancelDirectoryBrowser";

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
    $(URL).shouldBe(exactValue("ldap://test-ad.ivyteam.io"));
    $(USERNAME).shouldBe(exactValue("admin@zugtstdomain.wan"));
    $("#securityLdapConnectionForm\\:password").shouldBe(exactValue(""));

    $(URL).clear();
    $(URL).sendKeys("test");
    saveConnection();
    Selenide.refresh();

    $(URL).shouldBe(exactValue("test"));
    $(URL).clear();
    $(URL).sendKeys("ldap://test-ad.ivyteam.io");
    saveConnection();
  }

  @Test
  void ldapAttributes() {
    $(LDAP_ATTTRIBUTE_NAME).shouldBe(exactValue(""));
    $("#securityLdapAttributesForm\\:ldapFullName").shouldBe(exactValue(""));
    $("#securityLdapAttributesForm\\:ldapEmail").shouldBe(exactValue(""));
    $("#securityLdapAttributesForm\\:ldapLanguage").shouldBe(exactValue(""));
    $("#securityLdapAttributesForm\\:ldapUserMemberOfAttribute").shouldBe(exactValue(""));
    PrimeUi.selectBooleanCheckbox(By.id("securityLdapAttributesForm:ldapUserMemberOfLookupAllowed"))
            .shouldBeChecked(true);
    $("#securityLdapAttributesForm\\:ldapGroupMemberOfAttribute").shouldBe(exactValue(""));
    $("#securityLdapAttributesForm\\:ldapGroupMembersAttribute").shouldBe(exactValue(""));

    $(LDAP_ATTTRIBUTE_NAME).sendKeys("test");
    $(LDAP_ATTRIBUTE_SAVE_BTN).click();
    $(LDAP_ATTRIBUTE_SAVE_GRWOL).shouldBe(visible);
    Selenide.refresh();

    $(LDAP_ATTTRIBUTE_NAME).shouldBe(exactValue("test"));
    $(LDAP_ATTTRIBUTE_NAME).clear();
    $(LDAP_ATTRIBUTE_SAVE_BTN).click();
    $(LDAP_ATTRIBUTE_SAVE_GRWOL).shouldBe(visible);
  }

  @Test
  void binding() {
    $(DEFAULT_CONTEXT).shouldBe(exactValue("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
    $(IMPORT_USERS_OF_GROUP).shouldBe(exactValue(""));
    $("#securityLdapBindingForm\\:userFilter").shouldBe(exactValue(""));

    $(IMPORT_USERS_OF_GROUP).sendKeys("test");
    saveBinding();
    Selenide.refresh();

    $(IMPORT_USERS_OF_GROUP).shouldBe(exactValue("test"));
    $(IMPORT_USERS_OF_GROUP).clear();
    saveBinding();
  }

  @Test
  void ldapUserAttributesNewInvalid() {
    $(NEW_LDAP_USER_ATTRIBUTE_BTN).shouldBe(visible).click();
    $(LDAP_USER_ATTRIBUTE_MODAL).shouldBe(visible);
    $("#ldapUserAttributeForm\\:attributeNameMessage").shouldBe(empty);
    $("#ldapUserAttributeForm\\:attributeValueMessage").shouldBe(empty);

    $(SAVE_LDAP_USER_ATTRIBUTE).click();
    $("#ldapUserAttributeForm\\:attributeNameMessage").shouldBe(text("Value is required"));
    $("#ldapUserAttributeForm\\:attributeValueMessage").shouldBe(text("Value is required"));
  }

  @Test
  void ldapUserAttributes() {
    Table table = new Table(By.id("securityLdapUserAttributesForm:ldapPropertiesTable"));
    assertThat(table.getFirstColumnEntries()).hasSize(2);

    $(NEW_LDAP_USER_ATTRIBUTE_BTN).click();
    $(LDAP_USER_ATTRIBUTE_MODAL).shouldBe(visible);
    $("#ldapUserAttributeForm\\:attributeNameInput").sendKeys("test");
    $("#ldapUserAttributeForm\\:attributeValueInput").sendKeys("value");
    $(SAVE_LDAP_USER_ATTRIBUTE).click();
    assertThat(table.getFirstColumnEntries()).hasSize(3).contains("test");
    table.valueForEntryShould("test", 2, exactText("value"));

    table.clickButtonForEntry("test", "editPropertyBtn");
    $(LDAP_USER_ATTRIBUTE_MODAL).shouldBe(visible);
    $("#ldapUserAttributeForm\\:attributeValueInput").clear();
    $("#ldapUserAttributeForm\\:attributeValueInput").sendKeys("newValue");
    $(SAVE_LDAP_USER_ATTRIBUTE).click();
    table.valueForEntryShould("test", 2, exactText("newValue"));

    table.clickButtonForEntry("test", "deleteLdapAttributeBtn");
    assertThat(table.getFirstColumnEntries()).hasSize(2).doesNotContain("test");
  }

  @Test
  void ldapBrowser_wrongConfig() {
    $(URL).clear();
    $(URL).sendKeys("ldap://test-ad.ivyteam.io2");
    saveConnection();
    try {
      openLdapBrowserWithConnError();
    } finally {
      $(URL).clear();
      $(URL).sendKeys("ldap://test-ad.ivyteam.io");
      saveConnection();
    }
    $(USERNAME).clear();
    $(USERNAME).sendKeys("bla");
    saveConnection();
    openLdapBrowserWithConnError();
    $(USERNAME).clear();
    $(USERNAME).sendKeys("admin@zugtstdomain.wan");
    saveConnection();
    openDefaultLdapBrowser();
    $(DIRECTORY_BROWSER_FORM + "ldapConnectionFailMessage").shouldNotBe(visible);
  }

  @Test
  void ldapBrowser_ssl() {
    $(URL).clear();
    $(URL).sendKeys("ldaps://test-ad.ivyteam.io:637"); // 637 for self-signed
                                                       // certificate
    $(USE_SSL).click();
    saveConnection();
    openLdapBrowserWithConnError();
    $(ENABLE_INSECURE_SSL).click();
    saveConnection();
    openDefaultLdapBrowser();
    $(DIRECTORY_BROWSER_FORM + "ldapConnectionFailMessage").shouldNotBe(visible);
    $(DIRECTORY_BROWSER_CANCEL).click();
    $(URL).clear();
    $(URL).sendKeys("ldap://test-ad.ivyteam.io");
    $(USE_SSL).click();
    $(ENABLE_INSECURE_SSL).click();
    saveConnection();
  }

  @Test
  void ldapBrowser_chooseDefaultContext() {
    $(DEFAULT_CONTEXT).clear();
    openDefaultLdapBrowser();
    $$(DIRECTORY_BROWSER_FORM + "tree > ul > li").shouldHave(size(3));
    $(DIRECTORY_BROWSER_FORM + "tree\\:2 .ui-tree-toggler").click();
    $(DIRECTORY_BROWSER_FORM + "tree\\:2 .ui-treenode-children").findAll(".ui-treenode-label")
            .find(text("OU=IvyTeam Test-OU")).click();
    $(DIRECTORY_BROWSER_FORM + "tree\\:2 .ui-treenode-children").findAll(".ui-treenode-label")
            .find(text("OU=IvyTeam Test-OU")).shouldHave(cssClass("ui-state-highlight"));
    $(DIRECTORY_BROWSER_CHOOSE).scrollTo().click();
    $(DIRECTORY_BROWSER_DIALOG).shouldNotBe(visible);
    $(DEFAULT_CONTEXT).shouldBe(exactValue("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
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
    $(DEFAULT_CONTEXT).shouldBe(exactValue("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
    openImportLdapBrowser();
    $(DIRECTORY_BROWSER_FORM + "tree\\:0").shouldHave(text("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
    $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-tree-toggler").click();
    $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode").shouldHave(size(11));
    $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode-label")
            .find(text("CN=role1")).click();
    $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode-label")
            .find(text("CN=role1")).shouldHave(cssClass("ui-state-highlight"));
    $(DIRECTORY_BROWSER_CHOOSE).click();
    $(DIRECTORY_BROWSER_DIALOG).shouldNotBe(visible);
    $(IMPORT_USERS_OF_GROUP).shouldBe(exactValue("CN=role1,OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
  }

  @Nested
  class LdapBrowserNovell {
    @BeforeEach
    void beforeEach() {
      Navigation.toSecuritySystemLdap("test-nd");
    }

    @Test
    void ldapBrowser_wrongConfig() {
      $(URL).clear();
      $(URL).sendKeys("ldap://test-edirectory.ivyteam.io2");
      saveConnection();
      try {
        openLdapBrowserWithConnError();
      } finally {
        $(URL).clear();
        $(URL).sendKeys("ldap://test-edirectory.ivyteam.io:389");
        saveConnection();
      }
      $(USERNAME).clear();
      $(USERNAME).sendKeys("bla");
      saveConnection();
      openLdapBrowserWithConnError();
      $(USERNAME).clear();
      $(USERNAME).sendKeys("cn=admin,o=org");
      saveConnection();
      openDefaultLdapBrowser();
      $(DIRECTORY_BROWSER_FORM + "ldapConnectionFailMessage").shouldNotBe(visible);
    }

    @Test
    void ldapBrowser_chooseImportUsersOfGroup() {
      $(DEFAULT_CONTEXT).shouldBe(exactValue("ou=IvyTeam Test-OU,o=zugtstorg"));
      openImportLdapBrowser();
      $(DIRECTORY_BROWSER_FORM + "tree\\:0").shouldHave(text("ou=IvyTeam Test-OU,o=zugtstorg"));
      $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-tree-toggler").click();
      $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode").shouldHave(size(10));
      $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode-label")
              .find(text("cn=role1")).click();
      $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode-label")
              .find(text("cn=role1")).shouldHave(cssClass("ui-state-highlight"));
      $(DIRECTORY_BROWSER_CHOOSE).click();
      $(DIRECTORY_BROWSER_DIALOG).shouldNotBe(visible);
      $(IMPORT_USERS_OF_GROUP).shouldBe(exactValue("cn=role1,ou=IvyTeam Test-OU,o=zugtstorg"));
    }

    @Test
    void ldapBrowser_initImportUsersOfGroup() {
      $(IMPORT_USERS_OF_GROUP).sendKeys("cn=role1,ou=IvyTeam Test-OU,o=zugtstorg");
      openImportLdapBrowser();
      $$(DIRECTORY_BROWSER_FORM + "tree .ui-treenode-label").find(exactText("CN=role1"))
              .shouldBe(visible, cssClass("ui-state-highlight"));
      Table table = new Table(By.id("directoryBrowser:directoryBrowserForm:nodeAttrTable"));
      table.tableEntry("cn", 2).shouldBe(exactText("role1"));
    }

    @Test
    void ldapBrowser_chooseDefaultContext() {
      $(DEFAULT_CONTEXT).clear();
      openDefaultLdapBrowser();
      $$(DIRECTORY_BROWSER_FORM + "tree > ul > li").shouldHave(size(1));
      $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-tree-toggler").click();
      $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode").shouldHave(size(4));
      $(DIRECTORY_BROWSER_FORM + "tree\\:0_3 .ui-tree-toggler").click();
      $$(DIRECTORY_BROWSER_FORM + "tree\\:0_3 .ui-treenode .ui-treenode-label")
              .find(text("ou=IvyTeam Test-OU")).click();
      $$(DIRECTORY_BROWSER_FORM + "tree\\:0_3 .ui-treenode .ui-treenode-label")
              .find(text("ou=IvyTeam Test-OU")).shouldHave(cssClass("ui-state-highlight"));
      $(DIRECTORY_BROWSER_CHOOSE).scrollTo().click();
      $(DIRECTORY_BROWSER_DIALOG).shouldNotBe(visible);
      $(DEFAULT_CONTEXT).shouldBe(exactValue("ou=IvyTeam Test-OU,o=zugtstorg"));
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
      $(DEFAULT_CONTEXT).clear();
      openDefaultLdapBrowser();
      Table table = new Table(By.id("directoryBrowser:directoryBrowserForm:nodeAttrTable"));
      table.firstColumnShouldBe(CollectionCondition.empty);
      $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-tree-toggler").click();
      $(DIRECTORY_BROWSER_FORM + "tree\\:0_3 .ui-tree-selectable").click();
      table.valueForEntryShould("o", 2, exactText("zugtstorg"));
    }
  }

  private void saveConnection() {
    $(SAVE_CONNECTION_BTN).scrollIntoView("{block: \"center\"}").click();
    $(CONNECTION_SAVE_GRWOL).shouldBe(visible);
    Selenide.executeJavaScript("arguments[0].click();", $(CONNECTION_SAVE_GRWOL + " .ui-growl-icon-close"));
  }

  private void saveBinding() {
    $(SAVE_BINDING_BTN).click();
    $(SECURITY_SYSTEM_BINDING_GROWL).shouldBe(visible);
    Selenide.executeJavaScript("arguments[0].click();", $(SECURITY_SYSTEM_BINDING_GROWL + " .ui-growl-icon-close"));
  }

  private void openImportLdapBrowser() {
    $("#securityLdapBindingForm\\:browseImportUserOfGroup").shouldBe(visible).click();
    $(DIRECTORY_BROWSER_DIALOG).shouldBe(visible);
  }

  private void openDefaultLdapBrowser() {
    $("#securityLdapBindingForm\\:browseDefaultContext").shouldBe(visible).click();
    $(DIRECTORY_BROWSER_DIALOG).shouldBe(visible);
  }

  private void openLdapBrowserWithConnError() {
    openDefaultLdapBrowser();
    try {
      $(DIRECTORY_BROWSER_FORM + "directoryBrowserMessage").shouldBe(visible).shouldNotBe(empty);
      $(DIRECTORY_BROWSER_CHOOSE).shouldBe(disabled);
    } finally {
      $(DIRECTORY_BROWSER_CANCEL).click();
    }
  }
}
