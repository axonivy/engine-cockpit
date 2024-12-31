package ch.ivyteam.enginecockpit.configuration;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.anyMatch;
import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.CollectionCondition.noneMatch;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.refresh;

import java.time.Duration;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.axonivy.ivy.webtest.primeui.widget.SelectOneMenu;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestConfiguration {

  private static final By CONTENT_FILTER_BTN = By.id("config:form:configTable:filterBtn");
  private static final By TABLE_ID = By.id("config:form:configTable");
  private Table table;

  @BeforeEach
  void beforeEach() {
    login();
    Configuration.timeout = 10000;
  }

  @Test
  void emailUrlFilter() {
    String filter = "EMail";
    $("#configureEmailBtn").shouldBe(visible).click();
    assertUrlFiltering(filter);
  }

  private void assertUrlFiltering(String filter) {
    assertCurrentUrlContains("systemconfig.xhtml?filter=" + filter);
    table = new Table(TABLE_ID, "span");
    table.searchFilterShould(exactValue(filter));
    table.firstColumnShouldBe(size(10));
  }

  @Test
  void systemDbConfigUrl() {
    $("#configureSystemDbBtn").shouldBe(visible).click();
    assertCurrentUrlContains("systemdb.xhtml");
  }

  @Nested
  class System {

    @BeforeEach
    void beforeEach() {
      Navigation.toSystemConfig();
      table = new Table(TABLE_ID, "span");
    }

    @Test
    void systemConfig() {
      $("h2").shouldHave(text("System Config"));
    }

    @Test
    void searchConfig() {
      assertSearchConfigEntry();
    }

    @Test
    void hideDefaults() {
      var config = "Data.AppDirectory";
      assertContentFilterText("Filter: none");
      table.firstColumnShouldBe(itemWithText(config));
      toggleDefaultFilter();
      assertContentFilterText("Filter: defined");
      table.firstColumnShouldBe(noneMatch("Config not visible", e -> config.equals(e.getText())));

      resetContentFilter();
      table.firstColumnShouldBe(itemWithText(config));
    }

    @Test
    void showSecuritySystemConfigs() {
      var config = "SecuritySystems.test-ad.IdentityProvider.Name";
      assertContentFilterText("Filter: none");
      table.firstColumnShouldBe(noneMatch("Config not visible", e -> config.equals(e.getText())));
      toggleFilter(List.of("Show Security Systems"));
      assertContentFilterText("Filter: Security Systems");
      table.firstColumnShouldBe(itemWithText(config));
      resetContentFilter();
      table.firstColumnShouldBe(noneMatch("Config not visible", e -> config.equals(e.getText())));
    }

    @Test
    void showConfigFile() {
      String key = "Connector.HTTP.AllowTrace";
      assertShowConfigFile(key);
    }

    @Test
    void updateConfig() {
      var config = "EMail.Server.EncryptionMethod";
      table.firstColumnShouldBe(sizeGreaterThan(0));
      table.row(config).shouldHave(cssClass("default-value"));

      table.clickButtonForEntry(config, "editConfigBtn");
      PrimeUi.selectOne(By.id("config:editConfigurationForm:editConfigurationValue"))
          .selectItemByLabel("SSL");
      $("#config\\:editConfigurationForm\\:saveEditConfiguration").click();
      table.row(config).shouldNotHave(cssClass("default-value"));

      toggleFilter(List.of("Show only defined values"));
      table.firstColumnShouldBe(itemWithText(config));
      assertResetConfig(config);
      table.firstColumnShouldBe(noneMatch("Config no longer listed under defined values",
          e -> e.getText().equals(config)));
    }

    @Test
    void editConfig_booleanFormat() {
      var config = "EMail.Server.SSL.UseKey";
      table.clickButtonForEntry(config, "editConfigBtn");
      assertThatConfigEditModalIsVisible(config, "false", "");
    }

    @Test
    void editConfig_numberFormat() {
      var config = "SystemDb.MaxConnections";
      table.clickButtonForEntry(config, "editConfigBtn");
      assertThatConfigEditModalIsVisible(config, "50", "Maximum number");
    }

    @Test
    void editConfig_enumerationFormat() {
      var config = "SystemTask.Failure.Behaviour";
      table.clickButtonForEntry(config, "editConfigBtn");
      assertThatConfigEditModalIsVisible(config, "FAIL_TASK_DO_RETRY", "Defines the behaviour");
    }

    @Test
    void restartHint() {
      var config = "Connector.HTTP.Address";
      assertEditConfig(config, "", "hi", "For servers with more than one IP address");
      Selenide.sleep(100); // restart will be evaluated by async file watcher
      refresh();
      table.valueForEntryShould(config, 2, exactText("hi"));
      var health = $(".health-messages");
      Selenide.Wait()
          .withTimeout(Duration.ofSeconds(5))
          .ignoring(AssertionError.class)
          .until(webDriver -> {
            health.find("a").click();
            Selenide.sleep(10);
            return health.attr("class").contains("active-topmenuitem");
          });
      health.$$("li").shouldHave(anyMatch("message contains", e -> e.getText().contains("Restart is required")));
      assertResetConfig(config);
    }
  }

  @Nested
  class Application {

    @BeforeEach
    void beforeEach() {
      navigateTo("test-ad");
    }

    private void navigateTo(String app) {
      Navigation.toApplicationDetail(app);
      $(CONTENT_FILTER_BTN).scrollIntoView("{behavior: \"instant\", block: \"center\", inline: \"center\"}");
      table = new Table(TABLE_ID, "span");
    }

    @Test
    void searchConfig() {
      assertSearchConfigEntry();
    }

    @Test
    void hideDefaults() {
      var config = "Data.FilesDirectory";
      table.firstColumnShouldBe(itemWithText(config));
      toggleDefaultFilter();
      table.firstColumnShouldBe(noneMatch("Hide default values", e -> StringUtils.equals(e.getText(), config)));
      resetContentFilter();
      table.firstColumnShouldBe(itemWithText(config));
    }

    @Test
    void showVariablesConfigs() {
      assertShowAppConfigFilter("Show Variables", "Variables.variable");
    }

    @Test
    void showDatabasesConfigs() {
      assertShowAppConfigFilter("Show Databases", "Databases.realdb.Driver");
    }

    @Test
    void showRestClientsConfigs() {
      assertShowAppConfigFilter("Show Rest Clients", "RestClients.test-rest.Url");
    }

    @Test
    void showWebSerivceClientsConfigs() {
      assertShowAppConfigFilter("Show Web Service Clients", "WebServiceClients.test-web.WsCallLibrary");
    }

    private void assertShowAppConfigFilter(String filter, String config) {
      table.firstColumnShouldBe(noneMatch("Config should not be listed in the config table per default: " + config,
          element -> StringUtils.equals(element.getText(), config)));
      toggleFilter(List.of(filter));
      table.firstColumnShouldBe(itemWithText(config));
      resetContentFilter();
      table.firstColumnShouldBe(noneMatch("Config should not be listed in the config table after reset filter: " + config,
          element -> StringUtils.equals(element.getText(), config)));
    }

    @Test
    void dynamicExpressions() {
      toggleFilter(List.of("Show Rest Clients"));
      var dynamic = "RestClients.second-rest.Properties.appKey";
      table.clickButtonForEntry(dynamic, "editConfigBtn");
      new ConfigAssert("config")
          .assertDefault("${ivy.var.password}")
          .assertValue("${ivy.var.password}");
    }

    @Test
    void showConfigFile() {
      navigateTo("demo-portal");
      var key = "OverrideProject";
      assertShowConfigFile(key);
    }

    @Test
    void overrideProject_pmvSelector() {
      var config = "OverrideProject";
      table.clickButtonForEntry(config, "editConfigBtn");
      assertThatConfigEditModalIsVisible(config, " ", "Defines a project containing overriding SubProcesses", "");
      $(By.id("config:editConfigurationForm:editConfigurationValue"))
          .shouldHave(cssClass("ui-selectonemenu"));
    }
  }

  @Nested
  class StandardProcess {

    @BeforeEach
    void beforeEach() {
      Navigation.toApplicationDetail("demo-portal");
      $(By.id("config:form:configTable:filterBtn")).scrollIntoView("{behavior: \"instant\", block: \"center\", inline: \"center\"}");
      table = new Table(TABLE_ID, "span");
    }

    @Test
    void defaultPages() {
      var config = "StandardProcess.DefaultPages";
      table.clickButtonForEntry(config, "editConfigBtn");
      assertThatConfigEditModalIsVisible(config, "com.axonivy.portal:portal", "default-pages", "auto");
      $(By.id("config:editConfigurationForm:editConfigurationValue")).shouldHave(cssClass("ui-selectonemenu"));
    }
  }

  private void assertSearchConfigEntry() {
    table.firstColumnShouldBe(sizeGreaterThan(0));
    String search = table.getFirstColumnEntries().get(0);
    table.search(search);
    table.firstColumnShouldBe(size(1));
    table.firstColumnShouldBe(exactTexts(search));
  }

  private void toggleDefaultFilter() {
    toggleFilter(List.of("Show only defined values"));
  }

  private void toggleFilter(List<String> filter) {
    $(CONTENT_FILTER_BTN).shouldBe(visible).click();
    $$("#contentFilter\\:filterPanel .ui-chkbox").shouldBe(sizeGreaterThanOrEqual(1));
    var checkboxes = PrimeUi.selectManyCheckbox(By.cssSelector("#contentFilter\\:filterForm\\:filterCheckboxes"));
    checkboxes.setCheckboxes(filter);
    $("#contentFilter\\:filterForm\\:applyFilter").shouldBe(visible).click();
  }

  private void assertShowConfigFile(String key) {
    table.clickButtonForEntry(key, "tasksButton");
    table.buttonMenuForEntryShouldBeVisible(key, "activityMenu");
    table.clickButtonForEntry(key, "showFileBtn");
    $("#config\\:showConfigurationFileModal").shouldBe(visible);
    $(".code-block").shouldHave(text(key));
  }

  private void assertResetConfig(String key) {
    table.clickButtonForEntry(key, "tasksButton");
    table.buttonMenuForEntryShouldBeVisible(key, "activityMenu");
    table.clickButtonForEntry(key, "resetConfigBtn");
    $("#config\\:resetConfigConfirmDialog").shouldBe(visible);

    $("#config\\:resetConfigConfirmForm\\:resetConfigConfirmYesBtn").click();
    $("#config\\:form\\:msgs_container").shouldHave(text(key), text("reset"));
  }

  private void assertEditConfig(String key, String value, String newValue, String desc) {
    table.clickButtonForEntry(key, "editConfigBtn");
    assertThatConfigEditModalIsVisible(key, value, desc, "");

    $("#config\\:editConfigurationForm\\:editConfigurationValue").clear();
    $("#config\\:editConfigurationForm\\:editConfigurationValue").sendKeys(newValue);
    $("#config\\:editConfigurationForm\\:saveEditConfiguration").click();
    $("#config\\:form\\:msgs_container").shouldHave(text(key), text("saved"));
    executeJavaScript("arguments[0].click();", $(".ui-growl-icon-close"));
    table.valueForEntryShould(key, 2, exactText(newValue));
    $("#config\\:form\\:msgs_container").shouldNotBe(visible);
  }

  private void assertThatConfigEditModalIsVisible(String key, String value, String desc) {
    assertThatConfigEditModalIsVisible("config", key, value, desc, value);
  }

  private void assertThatConfigEditModalIsVisible(String key, String value, String desc,
      String defaultValue) {
    assertThatConfigEditModalIsVisible("config", key, value, desc, defaultValue);
  }

  public static void assertThatConfigEditModalIsVisible(String idPrefix, String key, String value,
      String desc, String defaultValue) {
    new ConfigAssert(idPrefix)
        .assertKey(key)
        .assertDesc(desc)
        .assertDefault(defaultValue)
        .assertValue(value);
  }

  private void resetContentFilter() {
    $(CONTENT_FILTER_BTN).shouldBe(visible).click();
    $("#contentFilter\\:filterForm\\:resetFilterBtn").shouldBe(visible).click();
    assertContentFilterText("Filter: none");
  }

  private void assertContentFilterText(String expectedFilter) {
    $(CONTENT_FILTER_BTN).shouldHave(attribute("title", expectedFilter));
  }

  public static class ConfigAssert {
    private final String idPrefix;

    public ConfigAssert(String idPrefix) {
      this.idPrefix = idPrefix;
    }

    public ConfigAssert assertKey(String key) {
      $(By.id(idPrefix + ":editConfigurationModal")).shouldBe(visible);
      $(By.id(idPrefix + ":editConfigurationForm:editConfigurationKey")).shouldBe(exactText(key));
      return this;
    }

    public ConfigAssert assertDefault(String defaultValue) {
      if (StringUtils.isBlank(defaultValue)) {
        $(By.id(idPrefix + ":editConfigurationForm:editConfigurationDefaultValue")).shouldNot(exist);
      } else {
        $(By.id(idPrefix + ":editConfigurationForm:editConfigurationDefaultValue"))
            .shouldBe(exactText(defaultValue));
      }
      return this;
    }

    public ConfigAssert assertDesc(String desc) {
      if (StringUtils.isBlank(desc)) {
        $(By.id(idPrefix + ":editConfigurationForm:editConfigurationDescription")).shouldNot(exist);
      } else {
        $(By.id(idPrefix + ":editConfigurationForm:editConfigurationDescription")).shouldBe(text(desc));
      }
      return this;
    }

    public ConfigAssert assertValue(String value) {
      var configValue = $(By.id(idPrefix + ":editConfigurationForm:editConfigurationValue"));
      String classAttr = configValue.getAttribute("class");
      if (StringUtils.contains(classAttr, "ui-chkbox")) {
        PrimeUi.selectBooleanCheckbox(By.id(idPrefix + ":editConfigurationForm:editConfigurationValue"))
            .shouldBeChecked(Boolean.parseBoolean(value));
      } else if (StringUtils.contains(classAttr, "ui-inputnumber")) {
        $(By.id(idPrefix + ":editConfigurationForm:editConfigurationValue_input"))
            .shouldBe(exactValue(value));
      } else if (StringUtils.contains(classAttr, "ui-selectonemenu")) {
        SelectOneMenu menu = PrimeUi.selectOne(By.id(idPrefix + ":editConfigurationForm:editConfigurationValue"));
        menu.selectedItemShould(exactText(value));
      } else if (StringUtils.contains(configValue.getTagName(), "textarea")) {
        configValue.shouldNotBe(visible).shouldBe(exactValue(value));
        $(".CodeMirror").shouldBe(visible, text("this is a json file"));
      } else {
        $(By.id(idPrefix + ":editConfigurationForm:editConfigurationValue")).shouldBe(exactValue(value));
      }
      return this;
    }
  }
}
