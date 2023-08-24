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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.Navigation;

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
  void ldapUserAttributesNewInvalid() {
  // TODO : check in new framework that values are required. ... then remove me

    $(NEW_LDAP_USER_ATTRIBUTE_BTN).shouldBe(visible).click();
    $(LDAP_USER_ATTRIBUTE_MODAL).shouldBe(visible);
    $("#ldapUserAttributeForm\\:attributeNameMessage").shouldBe(empty);
    $("#ldapUserAttributeForm\\:attributeValueMessage").shouldBe(empty);

    $(SAVE_LDAP_USER_ATTRIBUTE).click();
    $("#ldapUserAttributeForm\\:attributeNameMessage").shouldBe(text("Value is required"));
    $("#ldapUserAttributeForm\\:attributeValueMessage").shouldBe(text("Value is required"));
  }

  @Test
  void ldapBrowser_wrongConfig() {
    // keep me -> adapt me
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
  void browseEscapedNames() {
    // keep me for now: can we integrate it into our dummy-provider? --> if yes, delete me
    // regu\êl\.wermelinger
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
  void adldapBrowser_chooseDefaultContext() {
    // introduce me!
  }

  @Nested
  class LdapBrowserNovell {
    @BeforeEach
    void beforeEach() {
      Navigation.toSecuritySystemLdap("test-nd");
    }

    @Test
    void ldapBrowser_chooseDefaultContext() {
      // TODO Keep me: maybe in simpler form ... just showing anything from our real edirectory.

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
