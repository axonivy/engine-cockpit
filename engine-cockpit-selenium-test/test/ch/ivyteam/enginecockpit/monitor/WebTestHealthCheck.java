package ch.ivyteam.enginecockpit.monitor;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestHealthCheck {

  private Table checksTable;

  @BeforeEach
  void beforeEach() {
    EngineCockpitUtil.login();
    Navigation.toHealth();
    $(By.id("checks")).click();
    checksTable = new Table(By.id("form:checkTable"));
  }

  @Test
  void checks() {
    checksTable.rows().shouldHave(sizeGreaterThanOrEqual(3));
  }

  @Test
  void severity() {
    var cell = checksTable.tableEntry(1, 1);
    cell.shouldHave(text("HIGH"));
    cell.$("span").shouldHave(cssClass("health-high"));
    cell.$("span>i").shouldHave(cssClass("si-alert-circle"));

    cell = checksTable.tableEntry(2, 1);
    cell.shouldHave(text("LOW"));
    cell.$("span").shouldHave(cssClass("health-low"));
    cell.$("span>i").shouldHave(cssClass("si-road-sign-warning"));

    cell = checksTable.tableEntry(3, 1);
    cell.shouldHave(text("HEALTHY"));
    cell.$("span").shouldHave(cssClass("health-healthy"));
    cell.$("span>i").shouldHave(cssClass("si-check-1"));

    checksTable.sortByColumn("Severity");

    checksTable.tableEntry(1, 1).shouldHave(text("HEALTHY"));
  }

  @Test
  void name() {
    checksTable.tableEntry(1, 2).shouldHave(text("Release Candidate"));
    checksTable.tableEntry(2, 2).shouldHave(text("Engine Mode"));

    checksTable.sortByColumn("Name");

    checksTable.tableEntry(1, 2).shouldHave(not(text("Release Candidate")));
  }

  @Test
  void description() {
    checksTable.tableEntry(1, 3).shouldHave(text("Checks if this is a release candidate version"));
    checksTable.tableEntry(2, 3).shouldHave(text("Checks if the engine is running in Demo or Maintenance mode"));

    checksTable.sortByColumn("Description");

    checksTable.tableEntry(1, 3).shouldHave(not(text("Checks if this is a release candidate version")));
  }

  @Test
  void nextExecution() {
    checksTable.tableEntry(1, 4).shouldHave(text("n.a. (n.a.)"));
    checksTable.tableEntry(2, 4).shouldHave(text("n.a. (n.a.)"));

    checksTable.sortByColumn("Next Execution");
    
    checksTable.tableEntry(1, 4).shouldNotHave(text("n.a. (n.a.)"));
    
    checksTable.sortByColumn("Next Execution");

    checksTable.tableEntry(1, 4).shouldHave(text("n.a. (n.a.)"));
  }

  @Test
  void disable_enable() {
    checksTable.sortByColumn("Name");
    var enable = $(By.id("form:checkTable:0:enable"));
    var disable = $(By.id("form:checkTable:0:disable"));

    enable.shouldBe(disabled);
    disable.shouldBe(enabled);

    disable.click();

    enable.shouldBe(enabled);
    disable.shouldBe(disabled);

    enable.click();

    enable.shouldBe(disabled);
    disable.shouldBe(enabled);
  }

  @Test
  void checkNow() {
    $(By.id("form:checkTable:0:checkNow")).click();
  }

  @Test
  void refresh() {
    $(By.id("refresh")).click();
  }

  @Test
  void filter_name() {
    checksTable.rows().shouldHave(sizeGreaterThanOrEqual(6));
    checksTable.search("Release Candidate");
    checksTable.rows().shouldHave(size(1));
  }

  @Test
  void filter_severity() {
    checksTable.rows().shouldHave(sizeGreaterThanOrEqual(6));
    checksTable.search("HIGH");
    checksTable.rows().shouldHave(size(4));
  }

  @Test
  void filter_description() {
    checksTable.rows().shouldHave(sizeGreaterThanOrEqual(6));
    checksTable.search("restart is required");
    checksTable.rows().shouldHave(size(1));
  }

  @Test
  void health_filter() {
    Navigation.toHealth();
    var table = new Table(By.id("form:messagesTable"));
    table.tableEntry(1, 4).$("a").click();
    checksTable.searchFilterShould(value("Release Candidate"));
    checksTable.rows().shouldHave(size(1));
  }
}
