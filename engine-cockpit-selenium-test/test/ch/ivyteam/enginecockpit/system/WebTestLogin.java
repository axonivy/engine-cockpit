package ch.ivyteam.enginecockpit.system;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.DASHBOARD;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.LOGIN;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.forceLogin;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.getAdminUser;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.open;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.openDashboard;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.viewUrl;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.browserup.bup.filters.ResponseFilter;
import com.browserup.bup.util.HttpMessageContents;
import com.browserup.bup.util.HttpMessageInfo;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.test.ProxyExtension;
import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import io.netty.handler.codec.http.HttpResponse;

@IvyWebTest
@ExtendWith({ProxyExtension.class})
public class WebTestLogin {

  @BeforeAll
  static void beforeAll() {
    open(viewUrl(LOGIN));
  }

  @AfterEach
  void afterEach() {
    Selenide.webdriver().driver().getProxy().removeResponseFilter(LOGIN);
  }

  @Test
  void autoLogin() {
    EngineCockpitUtil.login();
    assertCurrentUrlContains(DASHBOARD);
    assertThat(Selenide.title()).startsWith("Engine Cockpit").doesNotContain("Login");
    $("#sessionUserName").shouldBe(exactText(getAdminUser()));
  }

  @Test
  void login() {
    var filter = addStatusFilter();
    forceLogin();
    assertThat(filter.code).isEqualTo(302);
    assertThat(filter.isAjax).isEqualTo(false);
    assertCurrentUrlContains(DASHBOARD);
    assertThat(Selenide.title()).startsWith("Engine Cockpit").doesNotContain("Login");
    $("#sessionUserName").shouldBe(exactText(getAdminUser()));
  }

  @Test
  void loginInvalid() {
    var filter = addStatusFilter();
    $("#loginForm\\:userName").shouldBe(visible).clear();
    $("#loginForm\\:password").shouldBe(visible).clear();
    $("#loginForm\\:login").shouldBe(visible).click();
    assertThat(filter.code).isEqualTo(200);
    assertThat(filter.isAjax).isEqualTo(false);
    $("#loginForm\\:userName").shouldHave(cssClass("ui-state-error"));
    $("#loginForm\\:password").shouldHave(cssClass("ui-state-error"));

    $("#loginForm\\:userName").sendKeys(getAdminUser());
    $("#loginForm\\:login").click();
    assertThat(filter.code).isEqualTo(200);
    assertThat(filter.isAjax).isEqualTo(false);
    $("#loginForm\\:userName").shouldNotHave(cssClass("ui-state-error"));
    $("#loginForm\\:password").shouldHave(cssClass("ui-state-error"));

    $("#loginForm\\:password").sendKeys("test");
    $("#loginForm\\:login").click();
    assertThat(filter.code).isEqualTo(401);
    assertThat(filter.isAjax).isEqualTo(false);
    $("#loginForm\\:userName").shouldNotHave(cssClass("ui-state-error"));
    $("#loginForm\\:password").shouldNotHave(cssClass("ui-state-error"));
    $("#loginForm\\:loginMessage").shouldBe(visible);
  }

  @Test
  void logout() {
    EngineCockpitUtil.login();
    doLogout();
    assertLoginPageVisible();
    openDashboard();
    $("#sessionUserName").shouldBe(exactText(getAdminUser()));
  }

  private void assertLoginPageVisible() {
    assertCurrentUrlContains(LOGIN);
    assertThat(Selenide.title()).contains("Login");
  }

  private void doLogout() {
    $(".user-profile > a").click();
    $("#sessionLogoutBtn").shouldBe(visible).click();
  }

  private RecordLoginStatusCode addStatusFilter() {
    open(viewUrl(LOGIN));
    var filter = new RecordLoginStatusCode();
    Selenide.webdriver().driver().getProxy().addResponseFilter(LOGIN, filter);
    return filter;
  }

  private static final class RecordLoginStatusCode implements ResponseFilter {
    int code;
    boolean isAjax;

    @Override
    public void filterResponse(HttpResponse response, HttpMessageContents contents, HttpMessageInfo messageInfo) {
      if (messageInfo.getOriginalUrl().endsWith(LOGIN)) {
        code = response.status().code();
        var facesRequest = messageInfo.getOriginalRequest().headers().get("Faces-Request");
        isAjax = facesRequest != null && "partial/ajax".equals(facesRequest);
      }
    }
  }

}
