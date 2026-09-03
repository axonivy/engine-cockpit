package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.open;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.viewUrl;
import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.codeborne.selenide.SelenideElement;

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

  @AfterEach
  void afterEach() {
    var root = rootLoggerRow();
    var reset = root.findAll(By.tagName("button"))
        .findBy(attribute("title", "Reset runtime level"));
    if (reset.exists()) {
      reset.click();
    }
  }

  @Test
  void view() {
    $(CONFIGURATION_INFO).shouldBe(visible)
        .shouldHave(text("Configuration file"), text("Last loaded at"), text("Configuration last modified"));

    var loggerTable = new Table(LOGGER_TABLE);
    loggerTable.headerShouldBe(exactTexts("Name", "Configured level", "Effective level", "Appenders", "Additive", "Set level"));
    loggerTable.rows().shouldHave(sizeGreaterThan(0));
    $(LOGGER_TABLE).findAll(By.className("log-level")).shouldHave(sizeGreaterThan(0));
    $(LOGGER_TABLE).shouldHave(text("root"));

    var appenderTable = new Table(APPENDER_TABLE);
    appenderTable.headerShouldBe(exactTexts("Name", "Type", "Target"));
    appenderTable.rows().shouldHave(sizeGreaterThan(0));
  }

  @Test
  void filter() {
    var loggerTable = new Table(LOGGER_TABLE, "span");
    loggerTable.search("root");
    var visibleRows = loggerTable.rows().filter(visible);

    visibleRows.shouldHave(size(1));
    visibleRows.first().shouldHave(text("root"));
  }

  @Test
  void setLevelUpdatesLoggerTable() {
    var loggerTable = new Table(LOGGER_TABLE, "span");
    loggerTable.search("root");
    var root = loggerTable.rows().filter(visible).first();
    var levelMenu = root.find(By.className("ui-selectonemenu"));

    PrimeUi.selectOne(By.id(levelMenu.getAttribute("id"))).selectItemByLabel("TRACE");
    root.findAll(By.tagName("button"))
        .findBy(attribute("title", "Set level")).click();

    $(By.className("ui-growl-message")).shouldHave(text("Logger level changed"),
        text("Logger 'root' now uses level 'TRACE'."));
    var effectiveLevel = rootLoggerRow().find(By.xpath("./td[3]"))
        .find(By.className("log-level"));
    effectiveLevel.shouldHave(text("TRACE"), cssClass("log-level-TRACE"));
  }

  @Test
  void showAllLoggers() {
    var checkbox = PrimeUi.selectBooleanCheckbox(SHOW_ALL_LOGGERS);
    checkbox.shouldBeChecked(false).setChecked().shouldBeChecked(true);
    new Table(LOGGER_TABLE).rows().shouldHave(sizeGreaterThan(0));
  }

  private SelenideElement rootLoggerRow() {
    var loggerTable = new Table(LOGGER_TABLE, "span");
    loggerTable.search("root");
    return loggerTable.rows().filter(visible).first();
  }
}
