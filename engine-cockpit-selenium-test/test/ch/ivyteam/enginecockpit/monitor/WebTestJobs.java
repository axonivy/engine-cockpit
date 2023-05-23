package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.Wait;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.CollectionCondition;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestJobs {

  private static final String DURATION_STR = "[0-9][0-9]? (s|m|h|d)";
  private static final Pattern DATE_TIME = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}");
  private static final Pattern DURATION = Pattern.compile(DURATION_STR);
  private static final String CRON = "(([0-9][0-9]?|\\*|\\?)\\s){6}";
  private static final Pattern CONFIGURATION = Pattern.compile("("+DURATION+"\\s|"+CRON+")\\(.*\\)");

  private static final By TABLE_ID = By.id("form:jobTable");
  private Table table;

  @BeforeAll
  static void beforeAll() {
    login();
  }

  @BeforeEach
  void beforeEach() {
    Navigation.toJobs();
    table = new Table(TABLE_ID, true);
  }

  @Test
  void view() {
    $("h2").shouldHave(text("Jobs"));
    table.rows().shouldHave(CollectionCondition.sizeGreaterThan(20));
    for (int row = 1; row < table.rows().size(); row++) {
      table.tableEntry(row, 1).shouldBe(not(empty));
      var config = table.tableEntry(row, 3).text();
      assertThat(config).isNotBlank().matches(CONFIGURATION);
      var time = table.tableEntry(row, 4).text();
      assertThat(time).isNotBlank().matches(DATE_TIME);
      var timeTo = table.tableEntry(row, 5).text();
      assertThat(timeTo).isNotBlank().matches(DURATION);
      var executions = table.tableEntry(row, 6).text();
      assertThat(executions).isNotBlank().satisfies(exec -> Long.parseLong(exec));
      var errors = table.tableEntry(row, 7).text();
      assertThat(errors).isNotBlank().satisfies(err -> Long.parseLong(err));
    }
  }

  @Test
  void filter() {
    table.rows().shouldHave(CollectionCondition.sizeGreaterThan(20));
    $(By.id("form:jobTable:globalFilter")).sendKeys("Search system task");
    table.rows().shouldHave(CollectionCondition.size(1));

    $(By.id("form:jobTable:globalFilter")).clear();
    $(By.id("form:jobTable:globalFilter")).sendKeys("\n");
    table.rows().shouldHave(CollectionCondition.sizeGreaterThan(20));
    $(By.id("form:jobTable:globalFilter")).sendKeys("Searches for system tasks");
    table.rows().shouldHave(CollectionCondition.size(1));
  }

  @Test
  void refresh() {
    var content = table.rows().texts();
    $(By.id("refresh")).click();
    assertThat(table.rows().texts()).isNotEqualTo(content);
  }

  @Test
  void schedule() {
    var initialExecutions = Long.parseLong(table.tableEntry(1, 6).text());
    $(By.id("form:jobTable:0:schedule")).click();
    $(By.id("scheduleJob:schedule")).click();

    Wait()
        .withTimeout(Duration.ofSeconds(10))
        .ignoring(AssertionError.class)
        .until(webDriver -> {
          webDriver.navigate().refresh();
          var executions = Long.parseLong(table.tableEntry(1, 6).text());
          assertThat(executions).isGreaterThan(initialExecutions);
          return true;
        });
  }

  @Test
  void liveStats() {
    $(By.id("layout-config-button")).click();
    $$("h4").get(0).shouldBe(text("Jobs Executed"));
    $$("h4").get(1).shouldHave(text("Job Execution Time"));
  }
}
