package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.Conditions.NOT_NEGATIVE_INTEGER_TEXT;
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
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;

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
  private static final Condition NEXT_EXECUTION_TEXT = Conditions.matchText(NEXT_EXECUTION);
  private static final Condition REQUEST_PATH_TEXT = Conditions.matchText(REQUEST_PATH);
  private static final Condition DURATION_TEXT = Conditions.matchText(DURATION);
  private static final Condition DURATIONS_TEXT = Conditions.matchText(DURATIONS);
  private static final Condition DATE_TIME_TEXT = Conditions.matchText(DATE_TIME);

  private static final By TABLE_ID = By.id("form:beanTable");
  private static final By FIRING_TABLE_ID = By.id("startEventFiring:firingTable");
  private Table table;
  private Table firingTable;

  @BeforeAll
  static void beforeAll() {
    login();
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

  @Test
  void poll() {
    var initialExecutions = Long.parseLong(table.tableEntry(1, 5).text());
    $(By.id("form:beanTable:0:poll")).click();
    $(By.id("pollBean:poll")).click();

    Wait()
        .withTimeout(Duration.ofSeconds(10))
        .ignoring(AssertionError.class)
        .until(webDriver -> {
          $(By.id("refresh")).click();
          var executions = Long.parseLong(table.tableEntry(1, 5).text());
          assertThat(executions).isGreaterThan(initialExecutions);
          return true;
        });
  }

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

    $(By.id("startEventBean:class")).shouldHave(text("ch.ivyteam.ivy.process.eventstart.beans.TimerBean"));
    $(By.id("startEventBean:name")).shouldHave(text("TimerBean"));
    $(By.id("startEventBean:description")).shouldHave(text("Starts a process periodically or at certain times"));
    $(By.id("startEventBean:requestPath")).shouldHave(text("/engine-cockpit-test-data$1/188AE871FC5C4A58/eventLink.ivp"));
    $(By.id("startEventBean:serviceState")).shouldHave(text("RUNNING"));
    $(By.id("startEventBean:configuration")).shouldHave(text("120"));
    $(By.id("startEventBean:lastStart")).shouldHave(DATE_TIME_TEXT);
    $(By.id("startEventBean:lastStartError:message")).shouldHave(text("n.a."));
    $(By.id("startEventBean:lastInitError:message")).shouldHave(text("n.a."));
    $(By.id("startEventBean:lastStopError:message")).shouldHave(text("n.a."));
  }

  @Test
  void details_nominal_bean_stop_start() {
    navigateToDetails("eventLink.ivp");

    $(By.id("startEventBean:start")).shouldBe(disabled);
    $(By.id("startEventBean:stop")).shouldBe(enabled);
    $(By.id("startEventBean:serviceState")).shouldHave(text("RUNNING"));

    $(By.id("startEventBean:stop")).click();

    $(By.id("startEventBean:start")).shouldBe(enabled);
    $(By.id("startEventBean:stop")).shouldBe(disabled);
    $(By.id("startEventBean:serviceState")).shouldHave(text("STOPPED"));

    $(By.id("startEventBean:start")).click();

    $(By.id("startEventBean:start")).shouldBe(disabled);
    $(By.id("startEventBean:stop")).shouldBe(enabled);
    $(By.id("startEventBean:serviceState")).shouldHave(text("RUNNING"));
  }

  @Test
  void details_nominal_poll() {
    navigateToDetails("eventLink.ivp");

    detailsPoll();

    $(By.id("startEventPoll:polls")).shouldBe(NOT_NEGATIVE_INTEGER_TEXT);
    $(By.id("startEventPoll:pollDuration")).shouldBe(DURATIONS_TEXT);
    $(By.id("startEventPoll:pollErrors")).shouldHave(text("0"));
    $(By.id("startEventPoll:lastPollError:message")).shouldHave(text("n.a."));
    $(By.id("startEventPoll:pollConfiguration")).shouldHave(text("Every 2 minutes (PT2M)"));
    $(By.id("startEventPoll:nextPoll")).shouldHave(NEXT_EXECUTION_TEXT);
  }

  @Test
  void details_nominal_firings() {
    navigateToDetails("eventLink.ivp");

    detailsPoll();

    $(By.id("startEventFiring:executions")).shouldHave(Conditions.satisfiesText(executions -> assertThat(executions).isPositive()));
    $(By.id("startEventFiring:duration")).shouldHave(DURATIONS_TEXT);
    $(By.id("startEventFiring:errors")).shouldHave(text("0"));

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

    $(By.id("startEventFiring:executions")).shouldHave(satisfiesText(executions -> assertThat(executions).isPositive()));
    $(By.id("startEventFiring:duration")).shouldHave(DURATIONS_TEXT);
    $(By.id("startEventFiring:errors")).shouldHave(satisfiesText(errors -> assertThat(errors).isPositive()));

    firingTable = new Table(FIRING_TABLE_ID, true);
    firingTable.rows().shouldHave(CollectionCondition.sizeGreaterThan(0));
    for (int row = 1; row <= firingTable.rows().size(); row++) {
      firingTable.tableEntry(row, 4).shouldHave(text("ivy:error:script"));
    }
    $(By.id("startEventFiring:firingTable:0:error:showDetails")).click();
    assertErrorDialog("ivy:error:script");
  }

  @Test
  void details_poll_error() {
    navigateToDetails("eventLink3.ivp");

    detailsPoll();

    $(By.id("startEventPoll:lastPollError:message")).shouldHave(text("Exception in poll method"));
    $(By.id("startEventPoll:lastPollError:showDetails")).click();
    assertErrorDialog("Exception in poll method");
  }

  @Test
  void details_init_error() {
    navigateToDetails("eventLink4.ivp");

    $(By.id("startEventBean:lastInitError:message")).shouldHave(text("Exception in initialize method"));
    $(By.id("startEventBean:lastInitError:showDetails")).click();
    assertErrorDialog("Exception in initialize method");
  }

  @Test
  void details_start_error() {
    navigateToDetails("eventLink5.ivp");

    $(By.id("startEventBean:lastStartError:message")).shouldHave(text("Exception in start method"));
    $(By.id("startEventBean:lastStartError:showDetails")).click();
    assertErrorDialog("Exception in start method");
  }

  @Test
  void details_stop_error() {
    navigateToDetails("eventLink6.ivp");

    $(By.id("startEventBean:stop")).click();

    $(By.id("startEventBean:lastStopError:message")).shouldHave(text("Exception in stop method"));
    $(By.id("startEventBean:lastStopError:showDetails")).click();
    assertErrorDialog("Exception in stop method");
  }

  private void navigateToDetails(String link) {
    link = EngineCockpitUtil.getAppName()+"/engine-cockpit-test-data$1/188AE871FC5C4A58/"+link;
    $(By.id("form:beanTable:globalFilter")).sendKeys(link);
    table.rows().shouldHave(CollectionCondition.size(1));
    table.tableEntry(1, 1).shouldBe(visible, enabled).click();
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
    $(By.id("startEventPoll:polls")).shouldHave(NOT_NEGATIVE_INTEGER_TEXT);

    var initialPolls = Integer.parseInt($(By.id("startEventPoll:polls")).text());
    $(By.id("startEventPoll:poll")).click();
    $(By.id("pollBean:poll")).click();

    Wait()
        .withTimeout(Duration.ofSeconds(10))
        .ignoring(AssertionError.class)
        .until(webDriver -> {
          webDriver.navigate().refresh();
          var polls = Integer.parseInt($(By.id("startEventPoll:polls")).text());
          assertThat(polls).isGreaterThan(initialPolls);
          return true;
        });
  }
}
