package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;
import static org.openqa.selenium.By.id;
import static org.openqa.selenium.By.tagName;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
public class WebTestPerformance {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toPerformanceStatistic();
    var stop = $(id("performance:form:stop"));
    if (stop.is(enabled)) {
      stop.click();
    }
    var clear = $(id("performance:form:clear"));
    if (clear.is(enabled)) {
      clear.click();
    }
  }

  @Test
  void buttonState_initial() {
    $(id("performance:form:start")).shouldBe(visible, enabled);
    $(id("performance:form:stop")).shouldBe(visible, disabled);
    $(id("performance:form:clear")).shouldBe(visible, disabled);
    $(id("performance:form:refresh")).shouldBe(visible, disabled);
    $(id("performance:form:export")).shouldBe(visible, disabled);
    $(id("performance:form:config")).shouldBe(visible, enabled);
    $(id("performance:form:loggingWarning")).shouldBe(Condition.hidden);
  }

  @Test
  void buttonState_afterStart() {
    start();
    $(id("performance:form:start")).shouldBe(visible, disabled);
    $(id("performance:form:stop")).shouldBe(visible, enabled);
    $(id("performance:form:clear")).shouldBe(visible, disabled);
    $(id("performance:form:refresh")).shouldBe(visible, enabled);
    $(id("performance:form:export")).shouldBe(visible, disabled);
    $(id("performance:form:config")).shouldBe(visible, enabled);
  }

  @Test
  void buttonState_afterCancelStart() {
    $(id("performance:form:start")).click();
    $(id("startPerformance:cancel")).click();
    $(id("performance:form:start")).shouldBe(visible, enabled);
    $(id("performance:form:stop")).shouldBe(visible, disabled);
    $(id("performance:form:clear")).shouldBe(visible, disabled);
    $(id("performance:form:refresh")).shouldBe(visible, disabled);
    $(id("performance:form:export")).shouldBe(visible, disabled);
    $(id("performance:form:config")).shouldBe(visible, enabled);
    $(id("performance:form:loggingWarning")).shouldBe(Condition.hidden);
  }

  @Test
  void buttonState_afterStop() {
    start();
    $(id("performance:form:stop")).click();
    $(id("performance:form:start")).shouldBe(visible, enabled);
    $(id("performance:form:stop")).shouldBe(visible, disabled);
    $(id("performance:form:clear")).shouldBe(visible, disabled);
    $(id("performance:form:refresh")).shouldBe(visible, disabled);
    $(id("performance:form:export")).shouldBe(visible, disabled);
    $(id("performance:form:config")).shouldBe(visible, enabled);
  }

  @Test
  void buttonState_afterRecording() {
    recordData();

    $(id("performance:form:start")).shouldBe(visible, disabled);
    $(id("performance:form:stop")).shouldBe(visible, enabled);
    $(id("performance:form:clear")).shouldBe(visible, enabled);
    $(id("performance:form:refresh")).shouldBe(visible, enabled);
    $(id("performance:form:export")).shouldBe(visible, enabled);
    $(id("performance:form:config")).shouldBe(visible, enabled);
  }

  @Test
  void data_afterRecording() {
    recordData();
    firstColumnShouldBe(textsInAnyOrder("0", "1", "2", "3"));

    tableEntry("0", 5).shouldHave(text("Performance"));
    tableEntry("1", 5).shouldHave(text("Performance"));
    tableEntry("2", 5).shouldHave(text("Performance"));
    tableEntry("3", 5).shouldHave(text("Performance"));

    tableEntry("0", 6).shouldHave(text("performance.ivp"));
    tableEntry("1", 6).shouldBe(empty);
    tableEntry("2", 6).shouldBe(empty);
    tableEntry("3", 6).shouldBe(empty);

    tableEntry("0", 7).shouldHave(text("17B77E4EAE9AC806-f0"));
    tableEntry("1", 7).shouldHave(text("17B77E4EAE9AC806-f3"));
    tableEntry("2", 7).shouldHave(text("17B77E4EAE9AC806-f5"));
    tableEntry("3", 7).shouldHave(text("17B77E4EAE9AC806-f1"));

    tableEntry("0", 8).shouldHave(text("RequestStart"));
    tableEntry("1", 8).shouldHave(text("Script"));
    tableEntry("2", 8).shouldHave(text("Alternative"));
    tableEntry("3", 8).shouldHave(text("TaskEnd"));

    tableEntry("0", 10).shouldHave(text("1"));
    tableEntry("1", 10).shouldHave(text("101"));
    tableEntry("2", 10).shouldHave(text("101"));
    tableEntry("3", 10).shouldHave(text("1"));
  }

  @Test
  void data_afterClear() {
    recordData();
    $(id("performance:form:clear")).click();
    $x(tableBody()).shouldHave(text("No records found."));
  }

  @Test
  void navigateToConfig() {
    $(id("performance:form:config")).click();
    $(tagName("h1")).shouldHave(text("System Configuration"));
    $(id("config:form:configTable:globalFilter")).shouldHave(attribute("value", "ProcessEngine.FiringStatistic"));
  }

  private static void recordData() {
    start();

    EngineCockpitUtil.performanceData();
    EngineCockpitUtil.openDashboard();
    Navigation.toPerformanceStatistic();
  }

  private static void start() {
    $(id("performance:form:start")).click();
    $(id("startPerformance:start")).click();
  }

  public void firstColumnShouldBe(CollectionCondition cond) {
    $$x(getFirstColumnElement()).shouldBe(cond, Duration.ofSeconds(10));
  }

  public SelenideElement tableEntry(String entry, int column) {
    return $x(findColumnOverEntry(entry) + "/td[" + column + "]");
  }

  private String findColumnOverEntry(String entry) {
    return getFirstColumnElement() + "[text()='" + entry + "']/..";
  }

  private String getFirstColumnElement() {
    return tableBody()+"/tr/td[1]";
  }

  private String tableBody() {
    return "//div[@id='performance:form:performanceTable']//tbody";
  }

  public static void prepareScreenshot() {
    Navigation.toPerformanceStatistic();
    recordData();
  }
}
