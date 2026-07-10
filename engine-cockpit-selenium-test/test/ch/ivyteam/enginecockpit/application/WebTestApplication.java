package ch.ivyteam.enginecockpit.application;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestApplication {

  private Table table;
  private String version;

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toApplications();
    openFirstApplication();
    table = new Table(By.id("application:versionsForm:versionsTable"), true);
    version = addNewVersion();
  }

  @AfterEach
  void afterEach() {
    removeVersion(version);
  }

  @Test
  void versionsAreListed() {
    table.rows().shouldHave(sizeGreaterThan(0));
  }

  @Test
  void addVersion() {
    var tableRows = table.rows().size();
    $(By.id("application:createVersion")).shouldBe(visible).click();
    table.rows().shouldHave(sizeGreaterThan(tableRows));
  }

  @Test
  void activateAndDeactivateButton() {
    table.clickButtonForEntry(version, "activateBtn");
    table.buttonForEntryShouldBeDisabled(version, "activateBtn");
    table.clickButtonForEntry(version, "deactivateBtn");
    table.buttonForEntryShouldBeDisabled(version, "deactivateBtn");
  }

  @Test
  void tasksButtonOpensActivityMenu() {
    table.clickButtonForEntry(version, "tasksButton");
    table.buttonMenuForEntryShouldBeVisible(version, "activityMenu");
  }

  @Test
  void releaseMenuItemReleasesVersion() {
    table.clickButtonForEntry(version, "tasksButton");
    table.buttonMenuForEntryShouldBeVisible(version, "activityMenu");
    table.clickButtonForEntry(version, "releaseBtn");
  }

  @Test
  void deleteMenuItemRemovesVersion() {
    var version = addNewVersion();
    table.clickButtonForEntry(version, "tasksButton");
    table.buttonMenuForEntryShouldBeVisible(version, "activityMenu");
    table.clickButtonForEntry(version, "deleteBtn");
    table.firstColumnShouldBe(sizeGreaterThan(0));
    removeVersion(String.valueOf(table.rows().size()));
  }

  private void openFirstApplication() {
    var table = new Table(By.cssSelector("[id$='applicationsTable']"), true);
    table.rows().shouldHave(sizeGreaterThan(0));

    var applicationLink = table.tableEntry(1, 1).$("a");
    assertThat(applicationLink.getText()).isNotBlank();
    applicationLink.shouldBe(visible).click();

    assertCurrentUrlContains("application.xhtml");
  }

  private String addNewVersion() {
    var sizeBefore = table.rows().size();
    $(By.id("application:createVersion")).shouldBe(visible).click();
    table.rows().shouldHave(sizeGreaterThan(sizeBefore));
    return String.valueOf(table.rows().size());
  }

  private void removeVersion(String version) {
    table.clickButtonForEntry(version, "tasksButton");
    table.buttonMenuForEntryShouldBeVisible(version, "activityMenu");
    table.clickButtonForEntry(version, "deleteBtn");
  }
}