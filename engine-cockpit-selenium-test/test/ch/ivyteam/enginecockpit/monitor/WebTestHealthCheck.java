package ch.ivyteam.enginecockpit.monitor;

import static com.codeborne.selenide.CollectionCondition.anyMatch;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
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
    checksTable.rows().shouldHave(sizeGreaterThanOrEqual(3));
    checksTable.rows().shouldHave(
            anyMatch("row contains", e -> e.getText().contains("HIGH Release Candidate Checks if this is a release candidate version n.a. (n.a.)")),
            anyMatch("row contains", e -> e.getText().contains("LOW Engine Mode Checks if the engine is running in Demo or Maintenance mode n.a. (n.a.)")),
            anyMatch("row contains", e -> e.getText().contains("HEALTHY High Memory Usage Checks if memory usage was higher than 90% during the last 10 minutes n.a. (n.a.)")));
  }

  @Test
  void severity() {
    checksTable.sortByColumn("Severity");
    checksTable.tableEntry(1, 1).shouldHave(text("HEALTHY"));
  }

  @Test
  void name() {
    checksTable.sortByColumn("Name");
    checksTable.tableEntry(1, 2).shouldHave(not(text("Release Candidate")));
  }

  @Test
  void description() {
    checksTable.sortByColumn("Description");
    checksTable.tableEntry(1, 3).shouldHave(not(text("Checks if this is a release candidate version")));
  }

  @Test
  void nextExecution() {
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
  void filter_name() {
    checksTable.rows().shouldHave(sizeGreaterThanOrEqual(6));
    checksTable.search("Release Candidate");
    checksTable.rows().shouldHave(size(1));
    checksTable.search("HIGH");
    checksTable.rows().shouldHave(size(4));
    checksTable.search("restart is required");
    checksTable.rows().shouldHave(size(1));
  }

  @Test
  void health_filter() {
    Navigation.toHealth();
    var table = new Table(By.id("form:messagesTable"));
    table.tableEntry(1, 4).$("a").click();
    checksTable.searchFilterShould(not(empty));
    checksTable.rows().shouldHave(size(1));
  }
}
