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
import static com.codeborne.selenide.Selenide.$x;
import static org.openqa.selenium.By.id;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import ch.ivyteam.enginecockpit.util.Navigation;

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

    tracesTableEntry("HTTP/1.1 GET", 1).shouldHave(text("HTTP/1.1 GET"));
    tracesTableEntry("HTTP/1.1 GET", 1).$("a").$("span").shouldHave(attributeMatching("title", "(?s).*http\\.uri.*"));
    tracesTableEntry("HTTP/1.1 GET", 2).shouldBe(not(empty));
    tracesTableEntry("HTTP/1.1 GET", 3).shouldBe(not(empty));
    tracesTableEntry("HTTP/1.1 GET", 4).shouldBe(not(empty));
  }

  @Test
  void navigateToDetail() {
    recordData();

    tracesTableEntry("HTTP/1.1 GET", 1).$("a").click();
    $("h1").shouldHave(text("Slow Request Detail"));
    spanTableEntry("HTTP/1.1 GET", 1).shouldHave(text("HTTP/1.1 GET"));
    spanTableEntry("HTTP/1.1 GET", 2).shouldBe(not(empty));
    spanTableEntry("HTTP/1.1 GET", 3).shouldBe(not(empty));
    spanTableEntry("HTTP/1.1 GET", 4).shouldBe(not(empty));
    spanTableEntry("HTTP/1.1 GET", 5).shouldBe(text("http.uri"));
  }

  @Test
  void data_afterClear() {
    recordData();
    $(id("traces:form:clear")).click();
    $x(tracesTableBody()).shouldHave(text("No records found."));
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

  public SelenideElement tracesTableEntry(String entry, int column) {
    return $x(findTracesColumnOverEntry(entry) + "/td[" + column + "]");
  }

  public SelenideElement spanTableEntry(String entry, int column) {
    return $x(findSpanColumnOverEntry(entry) + "/td[" + column + "]");
  }

  private String findTracesColumnOverEntry(String entry) {
    return getTracesFirstColumnElement() + "[text()='" + entry + "']/../../..";
  }

  private String findSpanColumnOverEntry(String entry) {
    return getSpanFirstColumnElement() + "[text()='" + entry + "']/../..";
  }

  private String getTracesFirstColumnElement() {
    return tracesTableBody()+"/tr/td[1]/a/span";
  }

  private String getSpanFirstColumnElement() {
    return spanTableBody()+"/tr/td[1]/span[2]";
  }

  private String tracesTableBody() {
    return "//div[@id='traces:form:traceTable']//tbody";
  }

  private String spanTableBody() {
    return "//div[@id='slowRequests:form:tree']//tbody";
  }

  public static void prepareScreenshot() {
    Navigation.toSlowRequests();
    recordData(10);
  }
}
