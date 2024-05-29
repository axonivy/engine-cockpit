package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.Conditions.NOT_NEGATIVE_INTEGER_TEXT;
import static ch.ivyteam.enginecockpit.util.Conditions.matchText;
import static ch.ivyteam.enginecockpit.util.Conditions.satisfiesText;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.Wait;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverConditions;
import com.codeborne.selenide.WebElementCondition;

import ch.ivyteam.enginecockpit.util.Conditions;
import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestStartEvents {

  private static final String DURATION_STR = "[0-9][0-9]?[0-9]? (us|ms|s|m|h|d)";
  private static final String DATE_TIME_STR = "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}";
  private static final Pattern NEXT_EXECUTION = Pattern.compile("("+DURATION_STR + "\\s" + "\\(" + DATE_TIME_STR + "\\))|(n\\.a\\. \\(n\\.a\\.\\))");
  private static final Pattern REQUEST_PATH = Pattern.compile("[^\\/]+\\/[^\\$]+\\$[0-9]+\\/[0-9A-F]+\\/[^\\.]+\\.ivp");
  private static final Pattern DURATION = Pattern.compile(DURATION_STR);
  private static final Pattern DURATIONS = Pattern.compile(DURATION_STR + "\\s\\/\\s" + DURATION_STR + "\\s\\/\\s" + DURATION_STR);
  private static final Pattern DATE_TIME = Pattern.compile(DATE_TIME_STR);
  private static final WebElementCondition NEXT_EXECUTION_TEXT = matchText(NEXT_EXECUTION);
  private static final WebElementCondition REQUEST_PATH_TEXT = matchText(REQUEST_PATH);
  private static final WebElementCondition DURATION_TEXT = matchText(DURATION);
  private static final WebElementCondition DURATIONS_TEXT = matchText(DURATIONS);
  private static final WebElementCondition DATE_TIME_TEXT = matchText(DATE_TIME);

  private static final By TABLE_ID = By.id("form:beanTable");
  private static final By FIRING_TABLE_ID = By.id("firings:eventFiring:firingTable");
  static final By THREAD_TABLE_ID = By.id("threads:eventBeanThreads:threadTable");
  private Table table;
  private Table firingTable;

  @BeforeAll
  static void beforeAll() {
    login();
  }

  static void stopTimerBeans() {
    Navigation.toStartEvents();
    Table table = new Table(TABLE_ID, true);
    table.rows().shouldHave(CollectionCondition.sizeGreaterThan(1));
    for (int row = 1; row < table.rows().size(); row++) {
      if (table.tableEntry(row, 1).text().equals("TimerBean")) {
        $(By.id("form:beanTable:"+(row-1)+":stop")).shouldBe(enabled);
        $(By.id("form:beanTable:"+(row-1)+":stop")).click();
        $(By.id("form:beanTable:"+(row-1)+":stop")).shouldBe(disabled);
      }
    }
  }

  static void startTimerBeans() {
    Navigation.toStartEvents();
    Table table = new Table(TABLE_ID, true);
    table.rows().shouldHave(CollectionCondition.sizeGreaterThan(1));
    for (int row = 1; row < table.rows().size(); row++) {
      if (table.tableEntry(row, 1).text().equals("TimerBean")) {
        $(By.id("form:beanTable:"+(row-1)+":start")).shouldBe(enabled);
        $(By.id("form:beanTable:"+(row-1)+":start")).click();
        $(By.id("form:beanTable:"+(row-1)+":start")).shouldBe(disabled);
      }
    }
  }

  @BeforeEach
  void beforeEach() {
    Navigation.toStartEvents();
    table = new Table(TABLE_ID, true);
  }

  @Test
  void view() {
    $("h2").shouldHave(text("Start Events"));
    table.rows().shouldHave(CollectionCondition.sizeGreaterThan(1));
    for (int row = 1; row <= table.rows().size(); row++) {
      table.tableEntry(row, 1).shouldBe(not(empty));
      table.tableEntry(row, 2).shouldBe(not(empty));
      table.tableEntry(row, 3).shouldBe(REQUEST_PATH_TEXT);
      table.tableEntry(row, 4).shouldBe(NEXT_EXECUTION_TEXT);
      table.tableEntry(row, 5).shouldBe(NOT_NEGATIVE_INTEGER_TEXT);
      table.tableEntry(row, 6).shouldBe(NOT_NEGATIVE_INTEGER_TEXT);
    }
  }

  @Test
  void filter() {
    table.rows().shouldHave(CollectionCondition.sizeGreaterThan(1));
    $(By.id("form:beanTable:globalFilter")).sendKeys(EngineCockpitUtil.getAppName()+"/engine-cockpit-test-data$1/188AE871FC5C4A58/eventLink.ivp");
    table.rows().shouldHave(CollectionCondition.size(1));

    $(By.id("form:beanTable:globalFilter")).clear();
    $(By.id("form:beanTable:globalFilter")).sendKeys("\n");
    table.rows().shouldHave(CollectionCondition.sizeGreaterThan(1));
    $(By.id("form:beanTable:globalFilter")).sendKeys(EngineCockpitUtil.getAppName()+"/engine-cockpit-test-data$1/188AE871FC5C4A58/eventLink.ivp");
    table.rows().shouldHave(CollectionCondition.size(1));
  }
  
  @Test
  void sort() {
    table.sortByColumn("Name");
    table.tableEntry(1, 1).shouldBe(text("InitError"));
    table.sortByColumn("Name");
    table.tableEntry(1, 1).shouldBe(text("TimerBean"));
  }

  @Test
  void refresh() {
    var content = table.rows().texts();
    Wait()
      .withTimeout(Duration.ofSeconds(10))
      .ignoring(AssertionError.class)
      .until(webDriver -> {
        $(By.id("refresh")).click();
        assertThat(table.rows().texts()).isNotEqualTo(content);
        return true;
      });
  }

  // @Test
  // void poll() {
  //   var initialExecutions = Long.parseLong(table.tableEntry(1, 5).text());
  //   $(By.id("form:beanTable:0:poll")).click();
  //   $(By.id("pollBean:poll")).click();

  //   Wait()
  //       .withTimeout(Duration.ofSeconds(10))
  //       .ignoring(AssertionError.class)
  //       .until(webDriver -> {
  //         $(By.id("refresh")).click();
  //         var executions = Long.parseLong(table.tableEntry(1, 5).text());
  //         assertThat(executions).isGreaterThan(initialExecutions);
  //         return true;
  //       });
  // }

  @Test
  void stop_start() {
    $(By.id("form:beanTable:0:start")).shouldBe(disabled);
    $(By.id("form:beanTable:0:stop")).shouldBe(enabled);

    $(By.id("form:beanTable:0:stop")).click();

    $(By.id("form:beanTable:0:start")).shouldBe(enabled);
    $(By.id("form:beanTable:0:stop")).shouldBe(disabled);

    $(By.id("form:beanTable:0:start")).click();

    $(By.id("form:beanTable:0:start")).shouldBe(disabled);
    $(By.id("form:beanTable:0:stop")).shouldBe(enabled);
  }

  @Test
  void details_nominal_bean() {
    navigateToDetails("eventLink.ivp");

    $(By.id("bean:eventBean:class")).shouldHave(text("ch.ivyteam.ivy.process.eventstart.beans.TimerBean"));
    $(By.id("bean:eventBean:name")).shouldHave(text("TimerBean"));
    $(By.id("bean:eventBean:description")).shouldHave(text("Starts a process periodically or at certain times"));
    $(By.id("bean:eventBean:requestPath")).shouldHave(text("/engine-cockpit-test-data$1/188AE871FC5C4A58/eventLink.ivp"));
    $(By.id("bean:eventBean:serviceState")).shouldHave(text("RUNNING"));
    $(By.id("bean:eventBean:configuration")).shouldHave(text("120"));
    $(By.id("bean:eventBean:lastStart")).shouldHave(DATE_TIME_TEXT);
    $(By.id("bean:eventBean:lastStartError:message")).shouldHave(text("n.a."));
    $(By.id("bean:eventBean:lastInitError:message")).shouldHave(text("n.a."));
    $(By.id("bean:eventBean:lastStopError:message")).shouldHave(text("n.a."));
  }

  @Test
  void details_nominal_bean_stop_start() {
    navigateToDetails("eventLink.ivp");

    $(By.id("bean:eventBean:start")).shouldBe(disabled);
    $(By.id("bean:eventBean:stop")).shouldBe(enabled);
    $(By.id("bean:eventBean:serviceState")).shouldHave(text("RUNNING"));

    $(By.id("bean:eventBean:stop")).click();

    $(By.id("bean:eventBean:start")).shouldBe(enabled);
    $(By.id("bean:eventBean:stop")).shouldBe(disabled);
    $(By.id("bean:eventBean:serviceState")).shouldHave(text("STOPPED"));

    $(By.id("bean:eventBean:start")).click();

    $(By.id("bean:eventBean:start")).shouldBe(disabled);
    $(By.id("bean:eventBean:stop")).shouldBe(enabled);
    $(By.id("bean:eventBean:serviceState")).shouldHave(text("RUNNING"));
  }

  @Test
  void details_nominal_poll() {
    navigateToDetails("eventLink.ivp");

    detailsPoll();

    $(By.id("polls:eventPoll:polls")).shouldBe(NOT_NEGATIVE_INTEGER_TEXT);
    $(By.id("polls:eventPoll:pollDuration")).shouldBe(DURATIONS_TEXT);
    $(By.id("polls:eventPoll:pollErrors")).shouldHave(text("0"));
    $(By.id("polls:eventPoll:lastPollError:message")).shouldHave(text("n.a."));
    $(By.id("polls:eventPoll:pollConfiguration")).shouldHave(text("Every 2 minutes (PT2M)"));
    $(By.id("polls:eventPoll:nextPoll")).shouldHave(NEXT_EXECUTION_TEXT);
  }

  @Test
  void details_nominal_firings() {
    navigateToDetails("eventLink.ivp");

    detailsPoll();

    $(By.id("firings:eventFiring:executions")).shouldHave(Conditions.satisfiesText(executions -> assertThat(executions).isPositive()));
    $(By.id("firings:eventFiring:duration")).shouldHave(DURATIONS_TEXT);
    $(By.id("firings:eventFiring:errors")).shouldHave(text("0"));

    firingTable = new Table(FIRING_TABLE_ID, true);
    firingTable.rows().shouldHave(CollectionCondition.sizeGreaterThan(0));
    for (int row = 1; row <= firingTable.rows().size(); row++) {
      firingTable.tableEntry(row, 3).shouldHave(text("Time elapsed or reached 120"));
      firingTable.tableEntry(row, 4).shouldHave(text("n.a."));
      firingTable.tableEntry(row, 1).shouldHave(DATE_TIME_TEXT);
      firingTable.tableEntry(row, 2).shouldHave(DURATION_TEXT);
    }
  }

  @Test
  void details_error_firings() {
    navigateToDetails("eventLink2.ivp");

    detailsPoll();

    $(By.id("firings:eventFiring:executions")).shouldHave(satisfiesText(executions -> assertThat(executions).isPositive()));
    $(By.id("firings:eventFiring:duration")).shouldHave(DURATIONS_TEXT);
    $(By.id("firings:eventFiring:errors")).shouldHave(satisfiesText(errors -> assertThat(errors).isPositive()));

    firingTable = new Table(FIRING_TABLE_ID, true);
    firingTable.rows().shouldHave(CollectionCondition.sizeGreaterThan(0));
    for (int row = 1; row <= firingTable.rows().size(); row++) {
      firingTable.tableEntry(row, 4).shouldHave(text("ivy:error:script"));
    }
    $(By.id("firings:eventFiring:firingTable:0:error:showDetails")).click();
    assertErrorDialog("ivy:error:script");
  }

  @Test
  void details_poll_error() {
    navigateToDetails("eventLink3.ivp");

    detailsPoll();

    $(By.id("polls:eventPoll:lastPollError:message")).shouldHave(text("Exception in poll method"));
    $(By.id("polls:eventPoll:lastPollError:showDetails")).click();
    assertErrorDialog("Exception in poll method");
  }

  @Test
  void details_init_error() {
    navigateToDetails("eventLink4.ivp");

    $(By.id("bean:eventBean:lastInitError:message")).shouldHave(text("Exception in initialize method"));
    $(By.id("bean:eventBean:lastInitError:showDetails")).click();
    assertErrorDialog("Exception in initialize method");
  }

  @Test
  void details_start_error() {
    navigateToDetails("eventLink5.ivp");

    $(By.id("bean:eventBean:lastStartError:message")).shouldHave(text("Exception in start method"));
    $(By.id("bean:eventBean:lastStartError:showDetails")).click();
    assertErrorDialog("Exception in start method");
  }

  @Test
  void details_stop_error() {
    navigateToDetails("eventLink6.ivp");

    $(By.id("bean:eventBean:stop")).click();

    $(By.id("bean:eventBean:lastStopError:message")).shouldHave(text("Exception in stop method"));
    $(By.id("bean:eventBean:lastStopError:showDetails")).click();
    assertErrorDialog("Exception in stop method");
  }

  @Test
  void details_threads() {
    navigateToDetails("eventLink7.ivp");

    var threadTable = new Table(THREAD_TABLE_ID, true);
    threadTable.rows().shouldHave(CollectionCondition.sizeGreaterThan(0));
    $(By.id("threads:eventBeanThreads:threadTable:0:id")).click();
    Selenide.webdriver().shouldHave(WebDriverConditions.urlContaining("monitorThreads.xhtml"));
    $(By.id("form:threadTable:globalFilter")).shouldBe(not(empty));
  }

  @Test
  void details_threads_error() {
    navigateToDetails("eventLink8.ivp");

    var threadTable = new Table(THREAD_TABLE_ID, true);
    threadTable.rows().shouldHave(CollectionCondition.sizeGreaterThan(0));
    for (int row = 1; row <= threadTable.rows().size(); row++) {
        threadTable.tableEntry(row, 4).shouldHave(text("Error in event bean thread"));
    }
    $(By.id("threads:eventBeanThreads:threadTable:0:error:showDetails")).click();
    assertErrorDialog("Error in event bean thread");
  }

  private void navigateToDetails(String link) {
    link = EngineCockpitUtil.getAppName()+"/engine-cockpit-test-data$1/188AE871FC5C4A58/"+link;
    $(By.id("form:beanTable:globalFilter")).sendKeys(link);
    table.rows().shouldHave(CollectionCondition.size(1));
    table.tableEntry(1, 1).shouldBe(visible, enabled).find(By.tagName("a")).click();
    webdriver().shouldHave(urlContaining("monitorStartEventDetails.xhtml"));
  }

  private void assertErrorDialog(String message) {
    $(By.id("errorDialog")).shouldBe(visible);
    $(By.id("error:message")).shouldHave(text(message));
    var stackTrace = $(By.id("error:stackTrace")).text();
    stackTrace = stackTrace.replace("\n  ", "\n\t\t");
    stackTrace = stackTrace.replace("\n ", "\n\t");
    stackTrace = stackTrace.replace("\tError Id:", " Error Id:");
    stackTrace = stackTrace.replace("\tContext:", " Context:");
    stackTrace = stackTrace.replace("\tProcess Element:", " Process Element:");
    stackTrace = stackTrace + "\n";
    stackTrace = StringUtils.abbreviate(stackTrace, "", 0, 10000);
    $(By.id("error:copyError")).shouldBe(visible, enabled).click();
    Selenide.confirm(stackTrace);
  }

  private void detailsPoll() {
    $(By.id("polls:eventPoll:polls")).shouldHave(NOT_NEGATIVE_INTEGER_TEXT);

    var initialPolls = Integer.parseInt($(By.id("polls:eventPoll:polls")).text());
    $(By.id("polls:eventPoll:pollBtn")).click();
    $(By.id("polls:pollBean:pollBtn")).click();

    Wait()
        .withTimeout(Duration.ofSeconds(10))
        .ignoring(AssertionError.class)
        .until(webDriver -> {
          webDriver.navigate().refresh();
          var polls = Integer.parseInt($(By.id("polls:eventPoll:polls")).text());
          assertThat(polls).isGreaterThan(initialPolls);
          return true;
        });
  }
}
