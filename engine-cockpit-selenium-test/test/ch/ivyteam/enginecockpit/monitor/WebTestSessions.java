package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.containExactTextsCaseSensitive;
import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;
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

  private Table tableWithoutLink;

  @BeforeAll
  static void beforeAll() {
    EngineCockpitUtil.destroyOtherSessions();
    login();
  }

  @BeforeEach
  void beforeEach() {
    Navigation.toSessions();
    table = new Table(TABLE_ID, true);
    table.sortByColumn("Created");
    table.sortByColumn("Created");
    tableWithoutLink = new Table(TABLE_ID, false);
  }

  @Test
  void view() {
    $("h2").shouldHave(text("Sessions"));
    if (EngineUrl.isDesigner()) {
      table.firstColumnShouldBe(containExactTextsCaseSensitive(EngineCockpitUtil.getAdminUser()));
    } else {
      tableWithoutLink.firstColumnShouldBe(containExactTextsCaseSensitive(EngineCockpitUtil.getAdminUser()));
    }
  }

  @Test
  void filterUnauthenticatedSession() {
    if (EngineUrl.isDesigner()) {
      return;
    }
    createUnauthenticatedSession();

    Selenide.refresh();
    tableWithoutLink.firstColumnShouldBe(containExactTextsCaseSensitive(""));
    $("#form\\:sessionTable\\:filterBtn").click();
    $("#form\\:sessionTable\\:filterUnauthenticatedSession").click();
    $("#form\\:sessionTable\\:applyFilter").click();
    tableWithoutLink.firstColumnShouldBe(containExactTextsCaseSensitive(EngineCockpitUtil.getAdminUser()));

    $("#form\\:sessionTable\\:filterBtn").click();
    $("#form\\:sessionTable\\:resetFilterBtn").click();
    tableWithoutLink.firstColumnShouldBe(containExactTextsCaseSensitive(""));
  }

  private void createUnauthenticatedSession() {
    try {
      var url = rootUri().toUrl();
      var httpClient = HttpClient.newHttpClient();
      var httpRequest = HttpRequest.newBuilder(URI.create(url)).build();
      var response = httpClient.send(httpRequest, BodyHandlers.discarding());
      assertThat(response.statusCode()).isLessThan(400);
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
      return EngineUrl.create().app("dev-workflow-ui").path("faces/login.xhtml");
    }
    return EngineUrl.create().app("test").path("login");
  }
}
