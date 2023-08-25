package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest (headless = false)
public class WebTestSecuritySystemLdap {

  private static final String DEFAULT_CONTEXT = "securityIdentityProviderForm:group:1:property:0:propertyString";

  private static final String SAVE_CONNECTION_BTN = "securityIdentityProviderForm:group:0:save";
  private static final String CONNECTION_SAVE_GRWOL = "#securityIdentityProviderForm\\:securityIdentityProviderSaveSuccess_container";
  private static final String URL = "securityIdentityProviderForm:group:0:property:0:propertyString";
  private static final String USERNAME = "securityIdentityProviderForm:group:0:property:2:propertyString";

  public static final String DIRECTORY_BROWSER_DIALOG = "directoryBrowser:directoryBrowserDialog";
  public static final String DIRECTORY_BROWSER_FORM = "#directoryBrowser\\:directoryBrowserForm\\:";
  private static final String DIRECTORY_BROWSER_CHOOSE = "directoryBrowser:chooseDirectoryName";
  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toSecuritySystemLdap("test-ad");
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
  void adldapBrowser_chooseDefaultContext() {
    $(By.id("securityIdentityProviderForm:group:1:property:0:browseDefaultContext")).should(visible).click();
    $(By.id("directoryBrowser:directoryBrowserForm:tree:2")).should(visible);
    $(DIRECTORY_BROWSER_FORM + "tree\\:2 .ui-tree-toggler").click();
    $(By.id("directoryBrowser:directoryBrowserForm:tree")).shouldHave(text("OU=IvyTeam Test-OU"));
    $(DIRECTORY_BROWSER_FORM + "tree\\:2_8 .ui-tree-toggler").click();
    $(By.id("directoryBrowser:directoryBrowserForm:tree:2_8_0")).shouldHave(text("fullusername1")).click();

    $(By.id("directoryBrowser:cancelDirectoryBrowser")).should(visible);
    $(By.id("directoryBrowser:chooseDirectoryName")).should(visible).click();
    $(By.id("securityIdentityProviderForm:group:1:property:0:propertyString")).shouldHave(value("CN=fullusername1,OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
  }


  @Nested
  class LdapBrowserNovell {
    @BeforeEach
    void beforeEach() {
      Navigation.toSecuritySystemLdap("test-nd");
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
      $(By.id("securityIdentityProviderForm:group:1:property:0:propertyString")).shouldBe(exactValue("ou=IvyTeam Test-OU,o=zugtstorg"));
    }

  }

  private void saveConnection() {
    $(By.id(SAVE_CONNECTION_BTN)).scrollIntoView("{block: \"center\"}").click();
    $(CONNECTION_SAVE_GRWOL).shouldBe(visible);
    Selenide.executeJavaScript("arguments[0].click();", $(CONNECTION_SAVE_GRWOL + " .ui-growl-icon-close"));
  }

  private void openDefaultLdapBrowser() {
    $(By.id("securityIdentityProviderForm:group:1:property:0:browseDefaultContext")).shouldBe(visible).click();
    $(By.id(DIRECTORY_BROWSER_DIALOG)).shouldBe(visible);
  }

  private void openLdapBrowserWithConnError() {
    openDefaultLdapBrowser();
    try {
      $(By.id("directoryBrowser:directoryBrowserForm:directoryBrowserMessage")).shouldBe(visible).shouldNotBe(empty);
    } finally {
      $(By.id("directoryBrowser:cancelDirectoryBrowser")).click();
    }
  }
}
