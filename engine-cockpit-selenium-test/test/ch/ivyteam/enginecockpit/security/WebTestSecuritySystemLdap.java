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

  public static final String LDAP_BROWSER_DIALOG = "#ldapBrowser\\:ldapBrowserDialog";
  public static final String LDAP_BROWSER_FORM = "#ldapBrowser\\:ldapBrowserForm\\:";
  private static final String LDAP_BROWSER_CHOOSE = "#ldapBrowser\\:chooseLdapName";
  private static final String LDAP_BROWSER_CANCEL = "#ldapBrowser\\:cancelLdapBrowser";

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
    openLdapBrowserWithConnError();
    $(URL).clear();
    $(URL).sendKeys("ldap://test-ad.ivyteam.io");
    $(USERNAME).clear();
    $(USERNAME).sendKeys("bla");
    saveConnection();
    openLdapBrowserWithConnError();
    $(USERNAME).clear();
    $(USERNAME).sendKeys("admin@zugtstdomain.wan");
    saveConnection();
    openDefaultLdapBrowser();
    $(LDAP_BROWSER_FORM + "ldapConnectionFailMessage").shouldNotBe(visible);
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
    $(LDAP_BROWSER_FORM + "ldapConnectionFailMessage").shouldNotBe(visible);
    $(LDAP_BROWSER_CANCEL).click();
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
    $$(LDAP_BROWSER_FORM + "tree > ul > li").shouldHave(size(3));
    $(LDAP_BROWSER_FORM + "tree\\:2 .ui-tree-toggler").click();
    $(LDAP_BROWSER_FORM + "tree\\:2 .ui-treenode-children").findAll(".ui-treenode-label")
            .find(text("OU=IvyTeam Test-OU")).click();
    $(LDAP_BROWSER_FORM + "tree\\:2 .ui-treenode-children").findAll(".ui-treenode-label")
            .find(text("OU=IvyTeam Test-OU")).shouldHave(cssClass("ui-state-highlight"));
    $(LDAP_BROWSER_CHOOSE).scrollTo().click();
    $(LDAP_BROWSER_DIALOG).shouldNotBe(visible);
    $(DEFAULT_CONTEXT).shouldBe(exactValue("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
  }

  @Test
  void browseEscapedNames() {
      openDefaultLdapBrowser();
      var treeNode = $$(LDAP_BROWSER_FORM + "tree .ui-treenode-label").find(exactText("OU=issue22622"));
      treeNode.shouldBe(visible);
      treeNode.parent().$(".ui-tree-toggler").click();
      treeNode.parent().parent().$(".ui-treenode-children").findAll("li")
          .shouldBe(sizeGreaterThan(0));

      treeNode = $$(LDAP_BROWSER_FORM + "tree .ui-treenode-label").find(exactText("OU=issue25327"));
      treeNode.shouldBe(visible);
      treeNode.parent().$(".ui-tree-toggler").click();
      treeNode.parent().parent().$(".ui-treenode-children").findAll("li")
          .shouldBe(sizeGreaterThan(0));
  }

  @Test
  void ldapBrowser_initDefaultContext() {
    openDefaultLdapBrowser();
    $$(LDAP_BROWSER_FORM + "tree .ui-treenode-label").find(exactText("OU=IvyTeam Test-OU"))
            .shouldBe(visible, cssClass("ui-state-highlight"));
    Table table = new Table(By.id("ldapBrowser:ldapBrowserForm:nodeAttrTable"));
    table.tableEntry("distinguishedName", 2).shouldBe(exactText("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
  }

  @Test
  void ldapBrowser_chooseImportUsersOfGroup() {
    $(DEFAULT_CONTEXT).shouldBe(exactValue("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
    openImportLdapBrowser();
    $(LDAP_BROWSER_FORM + "tree\\:0").shouldHave(text("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
    $(LDAP_BROWSER_FORM + "tree\\:0 .ui-tree-toggler").click();
    $(LDAP_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode").shouldHave(size(11));
    $(LDAP_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode-label")
            .find(text("CN=role1")).click();
    $(LDAP_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode-label")
            .find(text("CN=role1")).shouldHave(cssClass("ui-state-highlight"));
    $(LDAP_BROWSER_CHOOSE).click();
    $(LDAP_BROWSER_DIALOG).shouldNotBe(visible);
    $(IMPORT_USERS_OF_GROUP).shouldBe(exactValue("CN=role1,OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
  }

  @Test
  void ldapBrowser_initImportUsersOfGroup() {
    $(IMPORT_USERS_OF_GROUP).sendKeys("CN=role1,OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan");
    openImportLdapBrowser();
    $$(LDAP_BROWSER_FORM + "tree .ui-treenode-label").find(exactText("CN=role1"))
            .shouldBe(visible, cssClass("ui-state-highlight"));
    Table table = new Table(By.id("ldapBrowser:ldapBrowserForm:nodeAttrTable"));
    table.tableEntry("distinguishedName", 2)
            .shouldBe(exactText("CN=role1,OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
  }

  @Test
  void ldapBrowser_attributes() {
    $(DEFAULT_CONTEXT).clear();
    openDefaultLdapBrowser();
    Table table = new Table(By.id("ldapBrowser:ldapBrowserForm:nodeAttrTable"));
    table.firstColumnShouldBe(CollectionCondition.empty);
    $(LDAP_BROWSER_FORM + "tree\\:2").click();
    table.valueForEntryShould("distinguishedName", 2, exactText("DC=zugtstdomain,DC=wan"));
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
      openLdapBrowserWithConnError();
      $(URL).clear();
      $(URL).sendKeys("ldap://test-edirectory.ivyteam.io:389");
      $(USERNAME).clear();
      $(USERNAME).sendKeys("bla");
      saveConnection();
      openLdapBrowserWithConnError();
      $(USERNAME).clear();
      $(USERNAME).sendKeys("cn=admin,o=org");
      saveConnection();
      openDefaultLdapBrowser();
      $(LDAP_BROWSER_FORM + "ldapConnectionFailMessage").shouldNotBe(visible);
    }

    @Test
    void ldapBrowser_chooseImportUsersOfGroup() {
      $(DEFAULT_CONTEXT).shouldBe(exactValue("ou=IvyTeam Test-OU,o=zugtstorg"));
      openImportLdapBrowser();
      $(LDAP_BROWSER_FORM + "tree\\:0").shouldHave(text("ou=IvyTeam Test-OU,o=zugtstorg"));
      $(LDAP_BROWSER_FORM + "tree\\:0 .ui-tree-toggler").click();
      $(LDAP_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode").shouldHave(size(10));
      $(LDAP_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode-label")
              .find(text("cn=role1")).click();
      $(LDAP_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode-label")
              .find(text("cn=role1")).shouldHave(cssClass("ui-state-highlight"));
      $(LDAP_BROWSER_CHOOSE).click();
      $(LDAP_BROWSER_DIALOG).shouldNotBe(visible);
      $(IMPORT_USERS_OF_GROUP).shouldBe(exactValue("cn=role1,ou=IvyTeam Test-OU,o=zugtstorg"));
    }

    @Test
    void ldapBrowser_initImportUsersOfGroup() {
      $(IMPORT_USERS_OF_GROUP).sendKeys("cn=role1,ou=IvyTeam Test-OU,o=zugtstorg");
      openImportLdapBrowser();
      $$(LDAP_BROWSER_FORM + "tree .ui-treenode-label").find(exactText("CN=role1"))
              .shouldBe(visible, cssClass("ui-state-highlight"));
      Table table = new Table(By.id("ldapBrowser:ldapBrowserForm:nodeAttrTable"));
      table.tableEntry("cn", 2).shouldBe(exactText("role1"));
    }

    @Test
    void ldapBrowser_chooseDefaultContext() {
      $(DEFAULT_CONTEXT).clear();
      openDefaultLdapBrowser();
      $$(LDAP_BROWSER_FORM + "tree > ul > li").shouldHave(size(1));
      $(LDAP_BROWSER_FORM + "tree\\:0 .ui-tree-toggler").click();
      $(LDAP_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode").shouldHave(size(4));
      $(LDAP_BROWSER_FORM + "tree\\:0_3 .ui-tree-toggler").click();
      $$(LDAP_BROWSER_FORM + "tree\\:0_3 .ui-treenode .ui-treenode-label")
              .find(text("ou=IvyTeam Test-OU")).click();
      $$(LDAP_BROWSER_FORM + "tree\\:0_3 .ui-treenode .ui-treenode-label")
              .find(text("ou=IvyTeam Test-OU")).shouldHave(cssClass("ui-state-highlight"));
      $(LDAP_BROWSER_CHOOSE).scrollTo().click();
      $(LDAP_BROWSER_DIALOG).shouldNotBe(visible);
      $(DEFAULT_CONTEXT).shouldBe(exactValue("ou=IvyTeam Test-OU,o=zugtstorg"));
    }

    @Test
    void ldapBrowser_initDefaultContext() {
      openDefaultLdapBrowser();
      $$(LDAP_BROWSER_FORM + "tree .ui-treenode-label").find(exactText("ou=IvyTeam Test-OU"))
              .shouldBe(visible, cssClass("ui-state-highlight"));
      Table table = new Table(By.id("ldapBrowser:ldapBrowserForm:nodeAttrTable"));
      table.tableEntry("ou", 2).shouldBe(exactText("IvyTeam Test-OU"));
    }

    @Test
    void ldapBrowser_attributes() {
      $(DEFAULT_CONTEXT).clear();
      openDefaultLdapBrowser();
      Table table = new Table(By.id("ldapBrowser:ldapBrowserForm:nodeAttrTable"));
      table.firstColumnShouldBe(CollectionCondition.empty);
      $(LDAP_BROWSER_FORM + "tree\\:0 .ui-tree-toggler").click();
      $(LDAP_BROWSER_FORM + "tree\\:0_3 .ui-tree-selectable").click();
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
    $(LDAP_BROWSER_DIALOG).shouldBe(visible);
  }

  private void openDefaultLdapBrowser() {
    $("#securityLdapBindingForm\\:browseDefaultContext").shouldBe(visible).click();
    $(LDAP_BROWSER_DIALOG).shouldBe(visible);
  }

  private void openLdapBrowserWithConnError() {
    openDefaultLdapBrowser();
    $(LDAP_BROWSER_FORM + "ldapBrowserMessage").shouldBe(visible).shouldNotBe(empty);
    $(LDAP_BROWSER_CHOOSE).shouldBe(disabled);
    $(LDAP_BROWSER_CANCEL).click();
  }
}
