package ch.ivyteam.enginecockpit.system;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.getAdminUser;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.viewUrl;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Selenide;

@IvyWebTest
public class WebTestLogin {

  @Test
  void testLogin() {
    login();
    assertCurrentUrlContains("dashboard.xhtml");
    assertThat(Selenide.title()).startsWith("Engine Cockpit").doesNotContain("Login");
    $("#sessionUserName").shouldBe(exactText(getAdminUser()));
  }

  @Test
  void testLoginInvalid() {
    Selenide.open(viewUrl("login.xhtml"));
    $("#loginForm\\:userName").shouldBe(visible).clear();
    $("#loginForm\\:password").shouldBe(visible).clear();
    $("#loginForm\\:login").shouldBe(visible).click();
    $("#loginForm\\:userName").shouldHave(cssClass("ui-state-error"));
    $("#loginForm\\:password").shouldHave(cssClass("ui-state-error"));

    $("#loginForm\\:userName").sendKeys(getAdminUser());
    $("#loginForm\\:login").click();
    $("#loginForm\\:userName").shouldNotHave(cssClass("ui-state-error"));
    $("#loginForm\\:password").shouldHave(cssClass("ui-state-error"));

    $("#loginForm\\:password").sendKeys("test");
    $("#loginForm\\:login").click();
    $("#loginForm\\:userName").shouldNotHave(cssClass("ui-state-error"));
    $("#loginForm\\:password").shouldNotHave(cssClass("ui-state-error"));
    $("#loginForm\\:loginMessage").shouldBe(visible);
  }

  @Test
  void testLogout() {
    login();
    logout();
    assertLoginPageVisible();
    Selenide.open(viewUrl("dashboard.xhtml"));
    $("#sessionUserName").shouldBe(exactText(getAdminUser()));
  }

  private void assertLoginPageVisible() {
    assertCurrentUrlContains("login.xhtml");
    assertThat(Selenide.title()).contains("Login");
  }

  private void logout() {
    $(".user-profile > a").click();
    $("#sessionLogoutBtn").shouldBe(visible).click();
  }
}
