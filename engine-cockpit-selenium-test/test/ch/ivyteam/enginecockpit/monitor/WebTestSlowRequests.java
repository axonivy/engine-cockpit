package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.attributeMatching;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.By.id;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestSlowRequests {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toSlowRequests();
    var stop = $(id("traces:form:stop"));
    if (stop.is(enabled)) {
      stop.click();
    }
    var clear = $(id("traces:form:clear"));
    if (clear.is(enabled)) {
      clear.click();
    }
  }

  @Test
  void notExisting() {
    var url = EngineCockpitUtil.viewUrl("monitorTraceDetail.xhtml?traceId=NOT-EXISTING");
    Selenide.open(url);
    $(By.className("ui-message-warn-summary")).shouldHave(text("Request no longer available"));
    $(By.className("ui-message-warn-detail")).shouldHave(text("The request was remove from the slowest requests because other requests were even slower!"));
  }

  @Test
  void buttonState_initial() {
    $(id("traces:form:start")).shouldBe(visible, enabled);
    $(id("traces:form:stop")).shouldBe(visible, disabled);
    $(id("traces:form:clear")).shouldBe(visible, disabled);
    $(id("traces:form:refresh")).shouldBe(visible, disabled);
    $(id("traces:form:export")).shouldBe(visible, disabled);
    $(id("performance:form:loggingWarning")).shouldBe(Condition.hidden);
  }

  @Test
  void buttonState_afterStart() {
    start();
    $(id("traces:form:start")).shouldBe(visible, disabled);
    $(id("traces:form:stop")).shouldBe(visible, enabled);
    $(id("traces:form:clear")).shouldBe(visible, disabled);
    $(id("traces:form:refresh")).shouldBe(visible, enabled);
    $(id("traces:form:export")).shouldBe(visible, disabled);
  }

  @Test
  void buttonState_afterCancelStart() {
    $(id("traces:form:start")).click();
    $(id("startTraces:cancel")).click();
    $(id("traces:form:start")).shouldBe(visible, enabled);
    $(id("traces:form:stop")).shouldBe(visible, disabled);
    $(id("traces:form:clear")).shouldBe(visible, disabled);
    $(id("traces:form:refresh")).shouldBe(visible, disabled);
    $(id("traces:form:export")).shouldBe(visible, disabled);
    $(id("traces:form:loggingWarning")).shouldBe(Condition.hidden);
  }

  @Test
  void buttonState_afterStop() {
    start();
    $(id("traces:form:stop")).click();
    $(id("traces:form:start")).shouldBe(visible, enabled);
    $(id("traces:form:stop")).shouldBe(visible, disabled);
    $(id("traces:form:clear")).shouldBe(visible, disabled);
    $(id("traces:form:refresh")).shouldBe(visible, disabled);
    $(id("traces:form:export")).shouldBe(visible, disabled);
  }

  @Test
  void buttonState_afterRecording() {
    recordData();

    $(id("traces:form:start")).shouldBe(visible, disabled);
    $(id("traces:form:stop")).shouldBe(visible, enabled);
    $(id("traces:form:clear")).shouldBe(visible, enabled);
    $(id("traces:form:refresh")).shouldBe(visible, enabled);
    $(id("traces:form:export")).shouldBe(visible, enabled);
  }

  @Test
  void data_afterRecording() {
    recordData();

    Table traces = new Table(By.id("traces:form:traceTable"), true);
    traces.tableEntry("HTTP/1.1 GET", 1).shouldHave(text("HTTP/1.1 GET"));
    traces.tableEntry("HTTP/1.1 GET", 1).$("a").$("span").shouldHave(attributeMatching("title", "(?s).*http\\.uri.*"));
    traces.tableEntry("HTTP/1.1 GET", 2).shouldBe(not(empty));
    traces.tableEntry("HTTP/1.1 GET", 3).shouldBe(not(empty));
    traces.tableEntry("HTTP/1.1 GET", 4).shouldBe(not(empty));
  }

  @Test
  void navigateToDetail() {
    recordData();

    Table traces = new Table(By.id("traces:form:traceTable"), true);
    traces.tableEntry("HTTP/1.1 GET", 1).$("a").click();
    $(id("spansTitle")).shouldHave(text("Slow Request Detail"));
    Table spans = new Table(By.id("spansTree"), false);
    spans.tableEntry("HTTP/1.1 GET", 1).shouldHave(text("HTTP/1.1 GET"));
    spans.tableEntry("HTTP/1.1 GET", 2).shouldBe(not(empty));
    spans.tableEntry("HTTP/1.1 GET", 3).shouldBe(not(empty));
    spans.tableEntry("HTTP/1.1 GET", 4).shouldBe(not(empty));
    spans.tableEntry("HTTP/1.1 GET", 5).shouldBe(text("http.uri"));

    $(id("attributesTitle")).shouldHave(text("Attributes"));
    Table attributes = new Table(By.id("attributesTable"), false);
    attributes.tableEntry("http.method", 1).shouldHave(text("http.method"));
    attributes.tableEntry("http.method", 2).shouldHave(text("GET"));
  }

  @Test
  void data_afterClear() {
    recordData();
    $(id("traces:form:clear")).click();
    Table traces = new Table(By.id("traces:form:traceTable"), true);
    traces.body().shouldHave(text("No records found."));
  }

  private static void recordData() {
    recordData(1);
  }

  private static void recordData(int requests) {
    start();
    while (requests-- > 0) {
      Navigation.toPerformanceStatistic();
    }
    Navigation.toSlowRequests();
  }

  private static void start() {
    $(id("traces:form:start")).click();
    $(id("startTraces:start")).click();
  }

  public static void prepareScreenshot() {
    Navigation.toSlowRequests();
    recordData(1);
  }
}
