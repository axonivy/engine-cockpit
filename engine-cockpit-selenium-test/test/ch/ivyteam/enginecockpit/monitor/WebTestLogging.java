package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.open;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.viewUrl;
import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;

import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestLogging {

  private static final By CONFIGURATION_INFO = By.id("loggingForm:configurationInfo");
  private static final By LOGGER_TABLE = By.id("loggingForm:loggerTable");
  private static final By APPENDER_TABLE = By.id("loggingForm:appenderTable");
  private static final By SHOW_ALL_LOGGERS = By.id("loggingForm:showAllLoggers");

  @BeforeEach
  void beforeEach() {
    login();
    open(viewUrl("logging.xhtml"));
  }

  @Test
  void view() {
    $(CONFIGURATION_INFO).shouldBe(visible);

    var loggerTable = new Table(LOGGER_TABLE);
    loggerTable.headerShouldBe(exactTexts("Name", "Configured level", "Effective level", "Appenders", "Additive", "Set level"));
    loggerTable.rows().shouldHave(sizeGreaterThan(0));

    var appenderTable = new Table(APPENDER_TABLE);
    appenderTable.headerShouldBe(exactTexts("Name", "Type", "Target"));
    appenderTable.rows().shouldHave(sizeGreaterThan(0));
  }

  @Test
  void showAllLoggers() {
    var checkbox = PrimeUi.selectBooleanCheckbox(SHOW_ALL_LOGGERS);
    checkbox.shouldBeChecked(false).setChecked().shouldBeChecked(true);
    new Table(LOGGER_TABLE).rows().shouldHave(sizeGreaterThan(0));
  }
}
