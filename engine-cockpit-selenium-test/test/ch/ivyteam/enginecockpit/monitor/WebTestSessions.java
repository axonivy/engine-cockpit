package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.containExactTextsCaseSensitive;
import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.engine.EngineUrl;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestSessions {

  private static final String SESSION_USER = "foo";

  private static final By TABLE_ID = By.id("form:sessionTable");
  private Table table;

  @BeforeAll
  static void beforeAll() {
    login();
  }

  @BeforeEach
  void beforeEach() {
    Navigation.toSessions();
    table = new Table(TABLE_ID, true);
  }

  @Test
  void view() {
    $("h2").shouldHave(text("Sessions"));
    new Table(TABLE_ID).firstColumnShouldBe(containExactTextsCaseSensitive("admin"));
  }

  @Test
  void filterUnauthenticatedSession() {
    createUnauthenticatedSession();

    Selenide.refresh();
    new Table(TABLE_ID).firstColumnShouldBe(containExactTextsCaseSensitive(""));
    $("#form\\:sessionTable\\:filterBtn").click();
    $("#form\\:sessionTable\\:filterUnauthenticatedSession").click();
    $("#form\\:sessionTable\\:applyFilter").click();
    new Table(TABLE_ID).firstColumnShouldBe(containExactTextsCaseSensitive("admin"));

    $("#form\\:sessionTable\\:filterBtn").click();
    $("#form\\:sessionTable\\:resetFilterBtn").click();
    new Table(TABLE_ID).firstColumnShouldBe(containExactTextsCaseSensitive(""));
  }

  private void createUnauthenticatedSession() {
    try {
      var url = rootUri().toUrl();
      var httpClient = HttpClient.newHttpClient();
      var httpRequest = HttpRequest.newBuilder(URI.create(url)).build();
      httpClient.send(httpRequest, BodyHandlers.discarding());
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  @Test
  void liveStats() {
    EngineCockpitUtil.assertLiveStats(List.of("Sessions"));
  }

  @Test
  void killSession() {
    openAnotherSession();
    table.firstColumnShouldBe(textsInAnyOrder(SESSION_USER));
    table.clickButtonForEntry(SESSION_USER, "killSession");
    table.search(SESSION_USER);
    table.firstColumnShouldBe(CollectionCondition.empty);
  }

  private void openAnotherSession() {
    $(".layout-topbar-actions .help-link a").shouldBe(visible).click();
    Selenide.switchTo().window(1);
    openLogin();
    $("#loginForm\\:userName").shouldBe(visible).sendKeys(SESSION_USER);
    $("#loginForm\\:password").shouldBe(visible).sendKeys(SESSION_USER);
    $("#loginForm\\:login").click();
    $("#sessionUserName").shouldHave(text(SESSION_USER));
    Selenide.switchTo().window(0);
    Selenide.refresh();
  }

  private void openLogin() {
    var url = rootUri()

            .toUrl();
    Selenide.open(url);
  }

  private EngineUrl rootUri() {
    if (EngineUrl.isDesigner()) {
      return EngineUrl.create().app("default-workflow").path("faces/login.xhtml");
    }
    return EngineUrl.create().app("test").path("login");
  }
}
