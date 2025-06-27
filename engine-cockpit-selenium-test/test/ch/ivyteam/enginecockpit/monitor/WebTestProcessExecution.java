package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.By.id;
import static org.openqa.selenium.By.tagName;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.SelenideElement;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
public class WebTestProcessExecution {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toProcessExecution();
  }

  @Test
  void navigateToConfig() {
    $(".config-link").click();
    $(tagName("h2")).shouldHave(text("System Configuration"));
    $(id("config:form:configTable:globalFilter")).shouldHave(attribute("value", "ProcessEngine.FiringStatistic"));
  }

  @Test
  void cancelStart() {
    $(id("form:start")).click();
    $(id("startPerformance:cancel")).click();
    assertStoppedEmptyButtonState();
  }

  @Test
  void performanceExecution() {
    assertStoppedEmptyButtonState();

    start();
    assertRunningEmptyButtonState();

    recordData();
    assertRunningButtonState();

    stop();
    assertStoppedButtonState();

    $(By.id("form:performanceTable:globalFilter")).sendKeys("Performance");

    tableCell(0, 4).shouldHave(text("Performance"));
    tableCell(1, 4).shouldHave(text("Performance"));
    tableCell(2, 4).shouldHave(text("Performance"));
    tableCell(3, 4).shouldHave(text("Performance"));

    tableCell(0, 5).shouldHave(text("performance.ivp"));
    tableCell(1, 5).shouldBe(empty);
    tableCell(2, 5).shouldBe(empty);
    tableCell(3, 5).shouldBe(empty);

    tableCell(0, 6).shouldHave(text("17B77E4EAE9AC806-f0"));
    tableCell(1, 6).shouldHave(text("17B77E4EAE9AC806-f3"));
    tableCell(2, 6).shouldHave(text("17B77E4EAE9AC806-f5"));
    tableCell(3, 6).shouldHave(text("17B77E4EAE9AC806-f1"));

    tableCell(0, 7).shouldHave(text("RequestStart"));
    tableCell(1, 7).shouldHave(text("Script"));
    tableCell(2, 7).shouldHave(text("Alternative"));
    tableCell(3, 7).shouldHave(text("TaskEnd"));

    tableCell(0, 9).shouldHave(text("1"));
    tableCell(1, 9).shouldHave(text("101"));
    tableCell(2, 9).shouldHave(text("101"));
    tableCell(3, 9).shouldHave(text("1"));

    clear();
    assertStoppedEmptyButtonState();
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
    EngineCockpitUtil.performanceData();
    EngineCockpitUtil.openDashboard();
    Navigation.toProcessExecution();
  }

  private static void start() {
    $(id("form:start")).shouldBe(visible, enabled).click();
    $(id("startPerformance:start")).click();
  }

  private static void stop() {
    $(id("form:stop")).shouldBe(visible, enabled).click();
  }

  private static void clear() {
    $(id("form:clear")).shouldBe(visible, enabled).click();
    $(tableBody()).shouldHave(text("No records found."));
  }

  public static SelenideElement tableCell(int row, int column) {
    return getRow(row).find("td", column);
  }

  private static SelenideElement getRow(int row) {
    return tableBody().find("tr", row);
  }

  private static SelenideElement tableBody() {
    return $(By.id("form:performanceTable")).find("tbody");
  }

  public static void prepareScreenshot() {
    Navigation.toProcessExecution();
    start();
    recordData();
    stop();
  }
}
