package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Selenide.$;

import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;

import ch.ivyteam.enginecockpit.util.Conditions;
import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest(headless = false)
class WebTestIntermediateEvents {

  private static final String DURATION_STR = "[0-9][0-9]?[0-9]? (us|ms|s|m|h|d)";
  private static final String DATE_TIME_STR = "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}";
  private static final Pattern NEXT_EXECUTION = Pattern.compile("("+DURATION_STR + "\\s" + "\\(" + DATE_TIME_STR + "\\))|(n\\.a\\. \\(n\\.a\\.\\))");
  private static final Pattern REQUEST_PATH = Pattern.compile("^.*?\\$[0-9]+\\/[A-Fa-f0-9]{16}-[a-f0-9]{2}$");
  private static final Condition NEXT_EXECUTION_TEXT = Conditions.matchText(NEXT_EXECUTION);
  private static final Condition REQUEST_PATH_TEXT = Conditions.matchText(REQUEST_PATH);
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
}
