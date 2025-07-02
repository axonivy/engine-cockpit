package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.open;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.viewUrl;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.attributeMatching;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.openqa.selenium.By.id;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestSlowRequests {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toSlowRequests();
  }

  @Test
  void notExisting() {
    open(viewUrl("monitorTraceDetail.xhtml", Map.of("traceId", "NOT-EXISTING")));
    $(By.className("ui-message-warn-summary")).shouldHave(text("Request no longer available"));
    $(By.className("ui-message-warn-detail")).shouldHave(text("The request was remove from the slowest requests because other requests were even slower!"));
  }

  @Test
  void cancelStart() {
    $(id("form:start")).click();
    $(id("startTraces:cancel")).click();
    assertStoppedEmptyButtonState();
  }

  @Test
  void slowRequests() {
    assertStoppedEmptyButtonState();

    start();
    assertRunningEmptyButtonState();

    recordData();
    assertRunningButtonState();

    stop();
    assertStoppedButtonState();

    var entry = "HTTP/1.1 GET /system/engine-cockpit/faces/monitorProcessExecution.xhtml";
    var traces = new Table(By.id("form:traceTable"), true);
    var columns = traces.body().$$("tr").findBy(text(entry)).$$("td");
    columns.get(1).shouldHave(text(entry));
    columns.get(1).$("a").$("span").shouldHave(attributeMatching("title", "(?s).*http\\.url.*"));
    columns.get(2).shouldBe(not(empty));
    columns.get(3).shouldBe(not(empty));
    columns.get(4).shouldBe(not(empty));

    clear();
    assertStoppedEmptyButtonState();
  }

  @Test
  void navigateToDetail() {
    start();
    recordData();
    stop();

    try {
      $$("span").findBy(text("HTTP/1.1 GET /system/engine-cockpit/faces/monitorProcessExecution.xhtml")).click();
      $$(".card").shouldHave(size(2));

      var spans = new Table(By.id("spansTree"), false);
      spans.tableEntry("HTTP/1.1 GET", 1).shouldHave(text("HTTP/1.1 GET"));
      spans.tableEntry("HTTP/1.1 GET", 2).shouldBe(not(empty));
      spans.tableEntry("HTTP/1.1 GET", 3).shouldBe(not(empty));
      spans.tableEntry("HTTP/1.1 GET", 4).shouldBe(not(empty));
      spans.tableEntry("HTTP/1.1 GET", 5).shouldBe(text("http.url"));

      var attributes = new Table(By.id("attributesTable"), false);
      attributes.tableEntry("http.method", 1).shouldHave(text("http.method"));
      attributes.tableEntry("http.method", 2).shouldHave(text("GET"));
    } finally {
      Navigation.toSlowRequests();
      clear();
    }
  }

  private void assertStoppedEmptyButtonState() {
    $(id("form:start")).shouldBe(visible, enabled);
    $(id("form:stop")).shouldBe(visible, disabled);
    $(id("form:clear")).shouldBe(visible, disabled);
    $(id("form:refresh")).shouldBe(visible, disabled);
    $(id("form:export")).shouldBe(visible, disabled);
  }

  private void assertStoppedButtonState() {
    $(id("form:start")).shouldBe(visible, enabled);
    $(id("form:stop")).shouldBe(visible, disabled);
    $(id("form:clear")).shouldBe(visible, enabled);
    $(id("form:refresh")).shouldBe(visible, disabled);
    $(id("form:export")).shouldBe(visible, enabled);
  }

  private void assertRunningEmptyButtonState() {
    $(id("form:start")).shouldBe(visible, disabled);
    $(id("form:stop")).shouldBe(visible, enabled);
    $(id("form:clear")).shouldBe(visible, disabled);
    $(id("form:refresh")).shouldBe(visible, enabled);
    $(id("form:export")).shouldBe(visible, disabled);
  }

  private void assertRunningButtonState() {
    $(id("form:start")).shouldBe(visible, disabled);
    $(id("form:stop")).shouldBe(visible, enabled);
    $(id("form:clear")).shouldBe(visible, enabled);
    $(id("form:refresh")).shouldBe(visible, enabled);
    $(id("form:export")).shouldBe(visible, enabled);
  }

  private static void recordData() {
    recordData(1);
  }

  private static void recordData(int requests) {
    while (requests-- > 0) {
      Navigation.toProcessExecution();
    }
    Navigation.toSlowRequests();
  }

  private static void start() {
    $(id("form:start")).shouldBe(visible, enabled).click();
    $(id("startTraces:start")).shouldBe(visible, enabled).click();
  }

  public static void stop() {
    $(id("form:stop")).shouldBe(visible, enabled).click();
  }

  public static void clear() {
    $(id("form:clear")).shouldBe(visible, enabled).click();
    Navigation.toTrafficGraph();
    $(id("form:clear")).shouldBe(visible, enabled).click();
    Navigation.toSlowRequests();
  }

  public static void prepareScreenshot() {
    Navigation.toSlowRequests();
    start();
    recordData(10);
    stop();
  }
}
