package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.exist;
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
public class WebTestSecuritySystemDetail
{
  private static final String DEFAULT_CONTEXT = "#securitySystemBindingForm\\:defaultContext";
  private static final String SAVE_LDAP_ATTRIBUTE = "#ldapAttributeForm\\:saveLdapAttribute";
  private static final String LDAP_ATTRIBUTE_MODAL = "#ldapAttributeModal";
  private static final String NEW_LDAP_ATTRIBUTE_BTN = "#securityLdapAttributesForm\\:newLdapAttributeBtn";
  private static final String SECURITY_SYSTEM_BINDING_GROWL = "#securitySystemBindingForm\\:securitySystemBindingSaveSuccess_container";
  private static final String IMPORT_USERS_OF_GROUP = "#securitySystemBindingForm\\:importUsersOfGroup";
  private static final String SAVE_SECURITY_SYSTEM_BINDING_BTN = "#securitySystemBindingForm\\:saveSecuritySystemBindingBtn";
  private static final String LDAP_SAVE_GRWOL = "#securityLdapForm\\:securitySystemLdapSaveSuccess_container";
  private static final String LDAP_SAVE_BTN = "#securityLdapForm\\:saveSecurtiySystemLdapBtn";
  private static final String LDAP_NAME = "#securityLdapForm\\:ldapName";
  private static final String SAVE_SUCCESS_GROWL = "#securitySystemConfigForm\\:securitySystemConfigSaveSuccess_container";
  private static final String SAVE_SECURITY_SYSTEM_BTN = "#securitySystemConfigForm\\:saveSecuritySystemConfigBtn";
  private static final String SYNC_TIME_MESSAGE = "#securitySystemConfigForm\\:syncTimeMessage";
  private static final String URL = "#securitySystemConfigForm\\:url";
  private static final String USERNAME = "#securitySystemConfigForm\\:userName";
  private static final String SYNC_TIME = "#securitySystemConfigForm\\:syncTime";
  private static final String USE_SSL = "#securitySystemConfigForm\\:useSsl";
  private static final String ENABLE_INSECURE_SSL = "#securitySystemConfigForm\\:enableInsecureSsl";
  public static final String LDAP_BROWSER_DIALOG = "#ldapBrowser\\:ldapBrowserDialog";
  public static final String LDAP_BROWSER_FORM = "#ldapBrowser\\:ldapBrowserForm\\:";
  private static final String LDAP_BROWSER_CHOOSE = "#ldapBrowser\\:chooseLdapName";
  private static final String LDAP_BROWSER_CANCEL = "#ldapBrowser\\:cancelLdapBrowser";

  @BeforeEach
  void beforeEach()
  {
    login();
    Navigation.toSecuritySystemDetail("test-ad");
  }
  
  @Test
  void testSecuritySystemDetail()
  {
    $$(".ui-panel").shouldHave(size(4));
  }

  @Test
  void testConnectionInfos()
  {
    $("#securitySystemConfigForm\\:provider").shouldBe(exactText("Microsoft Active Directory"));
    $(URL).shouldBe(exactValue("ldap://test-ad.ivyteam.io"));
    $("#securitySystemConfigForm\\:userName").shouldBe(exactValue("admin@zugtstdomain.wan"));
    $("#securitySystemConfigForm\\:password").shouldBe(exactValue(""));
    
    $(URL).clear();
    $(URL).sendKeys("test");
    $(SAVE_SECURITY_SYSTEM_BTN).click();
    $(SAVE_SUCCESS_GROWL).shouldBe(visible);
    Selenide.refresh();
    
    $(URL).shouldBe(exactValue("test"));
    $(URL).clear();
    $(URL).sendKeys("ldap://test-ad.ivyteam.io");
    $(SAVE_SECURITY_SYSTEM_BTN).click();
    $(SAVE_SUCCESS_GROWL).shouldBe(visible);
  }
  
  @Test
  void testDirNotDeletableIfUsedByApp()
  {
    $("#securitySystemConfigForm\\:deleteSecuritySystem").shouldNotBe(exist);
  }
  
  @Test
  void testInvalidAndValidSyncTimes()
  {
    $(SYNC_TIME).shouldBe(exactValue(""));
    $(SYNC_TIME).shouldBe(attribute("placeholder", "00:00"));
    $(SYNC_TIME_MESSAGE).shouldNotBe(visible);
    
    saveInvalidSyncTimeAndAssert("32:23");
    saveInvalidSyncTimeAndAssert("12:95");
    
    $(SYNC_TIME).clear();
    $(SYNC_TIME).sendKeys("16:47");
    $(SAVE_SECURITY_SYSTEM_BTN).click();
    $(SYNC_TIME_MESSAGE).shouldNotBe(visible);
    $(SAVE_SUCCESS_GROWL).shouldBe(visible);
    
    $(SYNC_TIME).clear();
    $(SAVE_SECURITY_SYSTEM_BTN).click();
    $(SAVE_SUCCESS_GROWL).shouldBe(visible);
  }

  private void saveInvalidSyncTimeAndAssert(String time)
  {
    $(SYNC_TIME).clear();
    $(SYNC_TIME).sendKeys(time);
    $(SAVE_SECURITY_SYSTEM_BTN).click();
    $(SYNC_TIME_MESSAGE).shouldBe(visible);
  }
  
  @Test
  void testLdapInfos()
  {
    $(LDAP_NAME).shouldBe(exactValue(""));
    $("#securityLdapForm\\:ldapFullName").shouldBe(exactValue(""));
    $("#securityLdapForm\\:ldapEmail").shouldBe(exactValue(""));
    $("#securityLdapForm\\:ldapLanguage").shouldBe(exactValue(""));
    $("#securityLdapForm\\:ldapUserMemberOfAttribute").shouldBe(exactValue(""));
    assertThat(PrimeUi.selectBooleanCheckbox(By.id("securityLdapForm:ldapUseUserMemberOfForUserRoleMembership"))
            .isChecked()).isTrue();
    $("#securityLdapForm\\:ldapUserGroupMemberOfAttribute").shouldBe(exactValue(""));
    $("#securityLdapForm\\:ldapUserGroupMembersAttribute").shouldBe(exactValue(""));
    
    $(LDAP_NAME).sendKeys("test");
    $(LDAP_SAVE_BTN).click();
    $(LDAP_SAVE_GRWOL).shouldBe(visible);
    Selenide.refresh();
    
    $(LDAP_NAME).shouldBe(exactValue("test"));
    $(LDAP_NAME).clear();
    $(LDAP_SAVE_BTN).click();
    $(LDAP_SAVE_GRWOL).shouldBe(visible);
  }
  
  @Test
  void testBinding()
  {
    $("#securitySystemBindingForm\\:defaultContext").shouldBe(exactValue("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
    $(IMPORT_USERS_OF_GROUP).shouldBe(exactValue(""));
    $("#securitySystemBindingForm\\:userFilter").shouldBe(exactValue(""));
    
    $(IMPORT_USERS_OF_GROUP).sendKeys("test");
    $(SAVE_SECURITY_SYSTEM_BINDING_BTN).click();
    $(SECURITY_SYSTEM_BINDING_GROWL).shouldBe(visible);
    Selenide.refresh();
    
    $(IMPORT_USERS_OF_GROUP).shouldBe(exactValue("test"));
    $(IMPORT_USERS_OF_GROUP).clear();
    $(SAVE_SECURITY_SYSTEM_BINDING_BTN).click();
    $(SECURITY_SYSTEM_BINDING_GROWL).shouldBe(visible);
  }
  
  @Test
  void testLdapAttributesNewInvalid()
  {
    $(NEW_LDAP_ATTRIBUTE_BTN).shouldBe(visible, enabled).click();
    $(LDAP_ATTRIBUTE_MODAL).shouldBe(visible);
    $("#ldapAttributeForm\\:attributeNameMessage").shouldBe(empty);
    $("#ldapAttributeForm\\:attributeMessage").shouldBe(empty);
    
    $(SAVE_LDAP_ATTRIBUTE).click();
    $("#ldapAttributeForm\\:attributeNameMessage").shouldBe(text("Value is required"));
    $("#ldapAttributeForm\\:attributeMessage").shouldBe(text("Value is required"));
  }
  
  @Test
  void testLdapAttributes()
  {
    Table table = new Table(By.id("securityLdapAttributesForm:ldapPropertiesTable"));
    assertThat(table.getFirstColumnEntries()).hasSize(2);
    
    $(NEW_LDAP_ATTRIBUTE_BTN).click();
    $(LDAP_ATTRIBUTE_MODAL).shouldBe(visible);
    $("#ldapAttributeForm\\:attributeNameInput").sendKeys("test");
    $("#ldapAttributeForm\\:attributeInput").sendKeys("value");
    $(SAVE_LDAP_ATTRIBUTE).click();
    assertThat(table.getFirstColumnEntries()).hasSize(3).contains("test");
    table.valueForEntryShould("test", 2, exactText("value"));
    
    table.clickButtonForEntry("test", "editPropertyBtn");
    $(LDAP_ATTRIBUTE_MODAL).shouldBe(visible);
    $("#ldapAttributeForm\\:attributeInput").clear();
    $("#ldapAttributeForm\\:attributeInput").sendKeys("newValue");
    $(SAVE_LDAP_ATTRIBUTE).click();
    table.valueForEntryShould("test", 2, exactText("newValue"));
    
    table.clickButtonForEntry("test", "deleteLdapAttributeBtn");
    assertThat(table.getFirstColumnEntries()).hasSize(2).doesNotContain("test");
  }
  
  @Test
  void testLdapBrowser_wrongConfig()
  {
    $(URL).clear();
    $(URL).sendKeys("ldap://test-ad.ivyteam.io2");
    $(SAVE_SECURITY_SYSTEM_BTN).click();
    openLdapBrowserWithConnError();
    $(URL).clear();
    $(URL).sendKeys("ldap://test-ad.ivyteam.io");
    $(USERNAME).clear();
    $(USERNAME).sendKeys("bla");
    $(SAVE_SECURITY_SYSTEM_BTN).scrollIntoView("{block: \"center\"}").click();
    openLdapBrowserWithConnError();
    $(USERNAME).clear();
    $(USERNAME).sendKeys("admin@zugtstdomain.wan");
    $(SAVE_SECURITY_SYSTEM_BTN).scrollIntoView("{block: \"center\"}").click();
    openDefaultLdapBrowser();
    $(LDAP_BROWSER_FORM + "ldapConnectionFailMessage").shouldNotBe(visible);
  }
  
  @Test
  void testLdapBrowser_ssl()
  {
    $(URL).clear();
    $(URL).sendKeys("ldaps://test-ad.ivyteam.io:637");
    $(USE_SSL).click();
    $(SAVE_SECURITY_SYSTEM_BTN).click();
    openLdapBrowserWithConnError();
    $(ENABLE_INSECURE_SSL).click();
    $(SAVE_SECURITY_SYSTEM_BTN).click();
    openDefaultLdapBrowser();
    $(LDAP_BROWSER_FORM + "ldapConnectionFailMessage").shouldNotBe(visible);
    $(LDAP_BROWSER_CANCEL).click();
    $(URL).clear();
    $(URL).sendKeys("ldap://test-ad.ivyteam.io");
    $(USE_SSL).click();
    $(ENABLE_INSECURE_SSL).click();
    $(SAVE_SECURITY_SYSTEM_BTN).click();
  }
  
  @Test
  void testLdapBrowser_chooseDefaultContext()
  {
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
  void testLdapBrowser_chooseImportUsersOfGroup()
  {
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
  void testLdapBrowser_attributes()
  {
    openDefaultLdapBrowser();
    Table table = new Table(By.id("ldapBrowser:ldapBrowserForm:nodeAttrTable"));
    table.firstColumnShouldBe(CollectionCondition.empty);
    $(LDAP_BROWSER_FORM + "tree\\:2").click();
    table.valueForEntryShould("distinguishedName", 2, exactText("DC=zugtstdomain,DC=wan"));
  }
  
  @Nested
  class LdapBrowserNovell
  {
    @BeforeEach
    void beforeEach()
    {
      Navigation.toSecuritySystemDetail("test-nd");
    }
    
    @Test
    void testLdapBrowser_wrongConfig()
    {
      $(URL).clear();
      $(URL).sendKeys("ldap://zugtstdirnds2");
      $(SAVE_SECURITY_SYSTEM_BTN).click();
      openLdapBrowserWithConnError();
      $(URL).clear();
      $(URL).sendKeys("ldap://zugtstdirnds:389");
      $(USERNAME).clear();
      $(USERNAME).sendKeys("bla");
      $(SAVE_SECURITY_SYSTEM_BTN).scrollIntoView("{block: \"center\"}").click();
      openLdapBrowserWithConnError();
      $(USERNAME).clear();
      $(USERNAME).sendKeys("cn=admin, o=zugtstorg");
      $(SAVE_SECURITY_SYSTEM_BTN).scrollIntoView("{block: \"center\"}").click();
      openDefaultLdapBrowser();
      $(LDAP_BROWSER_FORM + "ldapConnectionFailMessage").shouldNotBe(visible);
    }
    
    @Test
    void testLdapBrowser_chooseImportUsersOfGroup()
    {
      Navigation.toSecuritySystemDetail("test-nd");
      $(DEFAULT_CONTEXT).shouldBe(exactValue("ou=IvyTeam Test-OU,o=zugtstorg"));
      openImportLdapBrowser();
      $(LDAP_BROWSER_FORM + "tree\\:0").shouldHave(text("ou=IvyTeam Test-OU,o=zugtstorg"));
      $(LDAP_BROWSER_FORM + "tree\\:0 .ui-tree-toggler").click();
      $(LDAP_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode").shouldHave(size(11));
      $(LDAP_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode-label")
      .find(text("cn=role1")).click();
      $(LDAP_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode-label")
      .find(text("cn=role1")).shouldHave(cssClass("ui-state-highlight"));
      $(LDAP_BROWSER_CHOOSE).click();
      $(LDAP_BROWSER_DIALOG).shouldNotBe(visible);
      $(IMPORT_USERS_OF_GROUP).shouldBe(exactValue("cn=role1,ou=IvyTeam Test-OU,o=zugtstorg"));
    }
    
    @Test
    void testLdapBrowser_chooseDefaultContext()
    {
      $(DEFAULT_CONTEXT).clear();
      openDefaultLdapBrowser();
      $$(LDAP_BROWSER_FORM + "tree > ul > li").shouldHave(size(1));
      $(LDAP_BROWSER_FORM + "tree\\:0 .ui-tree-toggler").click();
      $(LDAP_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode").shouldHave(size(4));
      $(LDAP_BROWSER_FORM + "tree\\:0_0 .ui-tree-toggler").click();
      $$(LDAP_BROWSER_FORM + "tree\\:0_0 .ui-treenode .ui-treenode-label")
              .find(text("ou=IvyTeam Test-OU")).click();
      $$(LDAP_BROWSER_FORM + "tree\\:0_0 .ui-treenode .ui-treenode-label")
              .find(text("ou=IvyTeam Test-OU")).shouldHave(cssClass("ui-state-highlight"));
      $(LDAP_BROWSER_CHOOSE).scrollTo().click();
      $(LDAP_BROWSER_DIALOG).shouldNotBe(visible);
      $(DEFAULT_CONTEXT).shouldBe(exactValue("ou=IvyTeam Test-OU,o=zugtstorg"));
    }
    
    @Test
    void testLdapBrowser_attributes()
    {
      openDefaultLdapBrowser();
      Table table = new Table(By.id("ldapBrowser:ldapBrowserForm:nodeAttrTable"));
      table.firstColumnShouldBe(CollectionCondition.empty);
      $(LDAP_BROWSER_FORM + "tree\\:0 .ui-tree-toggler").click();
      $(LDAP_BROWSER_FORM + "tree\\:0_0").click();
      table.valueForEntryShould("o", 2, exactText("zugtstorg"));
    }
    
  }
  
  private void openImportLdapBrowser()
  {
    $("#securitySystemBindingForm\\:browseImportUserOfGroup").shouldBe(visible).click();
    $(LDAP_BROWSER_DIALOG).shouldBe(visible);
  }
  
  private void openDefaultLdapBrowser()
  {
    $("#securitySystemBindingForm\\:browseDefaultContext").shouldBe(visible).click();
    $(LDAP_BROWSER_DIALOG).shouldBe(visible);
  }
  
  private void openLdapBrowserWithConnError()
  {
    openDefaultLdapBrowser();
    $(LDAP_BROWSER_FORM + "ldapBrowserMessage").shouldBe(visible).shouldNotBe(empty);
    $(LDAP_BROWSER_CHOOSE).shouldBe(disabled);
    $(LDAP_BROWSER_CANCEL).click();
  }
  
}
