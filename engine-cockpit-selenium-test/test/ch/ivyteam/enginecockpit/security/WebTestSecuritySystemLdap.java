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

@IvyWebTest
public class WebTestSecuritySystemLdap {

  private static final String DEFAULT_CONTEXT = "identityProvider:dynamicConfigForm:group:1:property:0:propertyString";

  private static final String SAVE_CONNECTION_BTN = "identityProvider:dynamicConfigForm:group:0:save";
  private static final String CONNECTION_SAVE_GRWOL = "#identityProvider\\:dynamicConfigForm\\:dynamicConfigFormSaveSuccess_container";
  private static final String URL = "identityProvider:dynamicConfigForm:group:0:property:0:propertyString";
  private static final String DIRECTORY_BROWSER_DIALOG = "directoryBrowser:directoryBrowserDialog";
  private static final String DIRECTORY_BROWSER_FORM = "#directoryBrowser\\:directoryBrowserForm\\:";
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
    openDefaultLdapBrowser();
    $(By.id("directoryBrowser:directoryBrowserForm:directoryBrowserMessage")).shouldNotBe(visible);
  }

  @Test
  void adldapBrowser_chooseDefaultContext() {
    $(By.id("identityProvider:dynamicConfigForm:group:1:property:0:browseDirectory"))
      .should(visible).click();
    $(By.id("directoryBrowser:directoryBrowserForm:tree:0"))
      .should(visible);
    $(By.id("directoryBrowser:directoryBrowserForm:tree")).shouldHave(text("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
    $(By.id("directoryBrowser:directoryBrowserForm:tree:0_0"))
      .shouldHave(text("fullusername1")).click();

    $(By.id("directoryBrowser:cancelDirectoryBrowser")).should(visible);
    $(By.id("directoryBrowser:chooseDirectoryName")).should(visible).click();
    $(By.id("identityProvider:dynamicConfigForm:group:1:property:0:propertyString"))
      .shouldHave(value("CN=fullusername1,OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
  }

  @Test
  void disableChoose() {
    $(By.id(URL)).clear();
    saveConnection();

    $(By.id(DEFAULT_CONTEXT)).clear();
    $(By.id("identityProvider:dynamicConfigForm:group:1:save")).click();

    openDefaultLdapBrowser();
    $(By.id(DIRECTORY_BROWSER_CHOOSE)).shouldHave(cssClass("ui-state-disabled"));
    $(By.id("directoryBrowser:cancelDirectoryBrowser")).click();

    $(By.id(URL)).sendKeys("ldap://test-ad.ivyteam.io");
    saveConnection();

    $(By.id(DEFAULT_CONTEXT)).sendKeys("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan");
    $(By.id("identityProvider:dynamicConfigForm:group:1:save")).click();
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
      $(By.id(DEFAULT_CONTEXT)).shouldBe(exactValue(""));
      saveConnection();
      openDefaultLdapBrowser();
      $$(DIRECTORY_BROWSER_FORM + "tree > ul > li").shouldHave(size(1));
      $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-tree-toggler").click();
      $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode")
        .shouldHave(size(10));
      $(By.id("directoryBrowser:directoryBrowserForm:tree:0_0"))
        .shouldHave(text("cn=role1")).click();
      $(By.id(DIRECTORY_BROWSER_CHOOSE)).scrollTo().click();
      $(By.id(DIRECTORY_BROWSER_DIALOG)).shouldNotBe(visible);
      $(By.id("identityProvider:dynamicConfigForm:group:1:property:0:propertyString"))
        .shouldBe(exactValue("cn=role1,ou=IvyTeam Test-OU,o=zugtstorg"));
    }
  }

  private void saveConnection() {
    $(By.id(SAVE_CONNECTION_BTN)).scrollIntoView("{block: \"center\"}").click();
    $(CONNECTION_SAVE_GRWOL).shouldBe(visible);
    Selenide.executeJavaScript("arguments[0].click();", $(CONNECTION_SAVE_GRWOL + " .ui-growl-icon-close"));
  }

  private void openDefaultLdapBrowser() {
    $(By.id("identityProvider:dynamicConfigForm:group:1:property:0:browseDirectory")).shouldBe(visible).click();
    $(By.id(DIRECTORY_BROWSER_DIALOG)).shouldBe(visible);
  }

  private void openLdapBrowserWithConnError() {
    openDefaultLdapBrowser();
    try {
      $(By.id("directoryBrowser:directoryBrowserForm:directoryBrowserMessage"))
        .shouldBe(visible)
        .shouldNotBe(empty);
    } finally {
      $(By.id("directoryBrowser:cancelDirectoryBrowser")).click();
    }
  }
}
