package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.Conditions.NOT_NEGATIVE_INTEGER_TEXT;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.Wait;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestIntermediateEvents {

  private static final String INTERMEDIATE_EVENT_PATH = "/engine-cockpit-test-data$1/188B95440FE25CA6-f19";

  private static final By TABLE_ID = By.id("form:beanTable");
  private Table table;

  @BeforeAll
  static void beforeAll() {
    EngineCockpitUtil.createIntermediateEvent();
    login();
  }

  @BeforeEach
  void beforeEach() {
    Navigation.toIntermediateEvents();
    table = new Table(TABLE_ID, true);
  }

  @Test
  void filter() {
    table.rows().shouldHave(CollectionCondition.sizeGreaterThan(1));
    $(By.id("form:beanTable:globalFilter")).sendKeys(EngineCockpitUtil.getAppName() + INTERMEDIATE_EVENT_PATH);
    table.rows().shouldHave(CollectionCondition.size(1));

    $(By.id("form:beanTable:globalFilter")).clear();
    $(By.id("form:beanTable:globalFilter")).sendKeys("\n");
    table.rows().shouldHave(CollectionCondition.sizeGreaterThan(1));
    $(By.id("form:beanTable:globalFilter")).sendKeys(EngineCockpitUtil.getAppName() + INTERMEDIATE_EVENT_PATH);
    table.rows().shouldHave(CollectionCondition.size(1));
  }

  @Test
  void details_poll_error() {
    navigateToDetails("188B95440FE25CA6-f6");

    detailsPoll();

    $(By.id("polls:eventPoll:lastPollError:message")).shouldHave(text("Exception in poll method"));
    $(By.id("polls:eventPoll:lastPollError:showDetails")).click();
    assertErrorDialog("Exception in poll method");
  }

  @Test
  void no_firing_execution_duration() {
    navigateToDetails("188B95440FE25CA6-f6");
    $(By.id("firings:eventFiring:duration")).shouldNotBe(visible);
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

  private void navigateToDetails(String element) {
    element = EngineCockpitUtil.getAppName()+"/engine-cockpit-test-data$1/"+element;
    $(By.id("form:beanTable:globalFilter")).sendKeys(element);
    table.rows().shouldHave(CollectionCondition.size(1));
    table.tableEntry(1, 1).shouldBe(visible, enabled).findElement(By.tagName("a")).click();
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
