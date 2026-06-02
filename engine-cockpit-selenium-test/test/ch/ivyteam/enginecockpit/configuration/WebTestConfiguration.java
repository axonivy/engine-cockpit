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
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.refresh;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.axonivy.ivy.webtest.primeui.widget.SelectOneMenu;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ScrollIntoViewOptions;
import com.codeborne.selenide.ScrollIntoViewOptions.Block;
import com.codeborne.selenide.ScrollIntoViewOptions.Inline;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestConfiguration {

  private static final By CONTENT_FILTER_BTN = By.id("config:form:configTable:filterBtn");
  private static final By TABLE_ID = By.id("config:form:configTable");
  private static final By APPLICATION_CONTENT_FILTER_BTN = By.id("configuration:config:form:configTable:filterBtn");
  private static final By APPLICATION_TABLE_ID = By.id("configuration:config:form:configTable");
  private static final SelectorContext SYSTEM_CONTEXT = new SelectorContext(CONTENT_FILTER_BTN, TABLE_ID, "config", "contentFilter");
  private static final SelectorContext APPLICATION_CONTEXT =
      new SelectorContext(APPLICATION_CONTENT_FILTER_BTN, APPLICATION_TABLE_ID, "configuration:config", "configuration:contentFilter");
  private Table table;

  private static final class SelectorContext {
    private final By contentFilterBtn;
    private final By tableId;
    private final String configPrefix;
    private final String contentFilterPrefix;

    private SelectorContext(By contentFilterBtn, By tableId, String configPrefix, String contentFilterPrefix) {
      this.contentFilterBtn = contentFilterBtn;
      this.tableId = tableId;
      this.configPrefix = configPrefix;
      this.contentFilterPrefix = contentFilterPrefix;
    }
  }

  @BeforeEach
  void beforeEach() {
    login();
    Configuration.timeout = 10000;
  }

  @Test
  void emailUrlFilter() {
    $("#email\\:configureEmailBtn").shouldBe(visible).click();
    assertCurrentUrlContains("systemconfig.xhtml?filter=EMail");
    table = new Table(TABLE_ID, "span");
    table.searchFilterShould(exactValue("EMail"));
    table.firstColumnShouldBe(sizeGreaterThanOrEqual(10));
  }

  @Test
  void systemDbConfigUrl() {
    $("#systemDatabase\\:configureSystemDbBtn").shouldBe(visible).click();
    assertCurrentUrlContains("systemdb.xhtml");
  }

  @Nested
  class System {

    @BeforeEach
    void beforeEach() {
      Navigation.toSystemConfig();
      table = new Table(SYSTEM_CONTEXT.tableId, "span");
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
      assertContentFilterText(SYSTEM_CONTEXT, "Filter: none");
      table.firstColumnShouldBe(itemWithText(config));
      toggleDefaultFilter(SYSTEM_CONTEXT);
      assertContentFilterText(SYSTEM_CONTEXT, "Filter: defined");
      table.firstColumnShouldBe(noneMatch("Config not visible", e -> config.equals(e.getText())));

      resetContentFilter(SYSTEM_CONTEXT);
      table.firstColumnShouldBe(itemWithText(config));
    }

    @Test
    void showSecuritySystemConfigs() {
      var config = "SecuritySystems.test-ad.IdentityProvider.Name";
      assertContentFilterText(SYSTEM_CONTEXT, "Filter: none");
      table.firstColumnShouldBe(noneMatch("Config not visible", e -> config.equals(e.getText())));
      toggleFilter(SYSTEM_CONTEXT, List.of("Show Security Systems"));
      assertContentFilterText(SYSTEM_CONTEXT, "Filter: Security Systems");
      table.firstColumnShouldBe(itemWithText(config));
      resetContentFilter(SYSTEM_CONTEXT);
      table.firstColumnShouldBe(noneMatch("Config not visible", e -> config.equals(e.getText())));
    }

    @Test
    void showConfigFile() {
      String key = "Connector.HTTP.AllowTrace";
      assertShowConfigFile(SYSTEM_CONTEXT, key);
    }

    @Test
    void reloadConfig() {
      $(By.id("reloadConfig")).shouldBe(visible).click();
      $(By.id("config:form:msgs_container")).shouldBe(visible).should(text("System configuration reloaded"));
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
        assertAndCloseGrowl(SYSTEM_CONTEXT, config);
      table.row(config).shouldNotHave(cssClass("default-value"));

        toggleDefaultFilter(SYSTEM_CONTEXT);
      table.firstColumnShouldBe(itemWithText(config));
        assertResetConfig(SYSTEM_CONTEXT, config);
      table.firstColumnShouldBe(noneMatch("Config no longer listed under defined values",
          e -> e.getText().equals(config)));
    }

    @Test
    void editConfig_booleanFormat() {
      var config = "EMail.Server.SSL.UseKey";
      table.clickButtonForEntry(config, "editConfigBtn");
      assertThatConfigEditModalIsVisible(SYSTEM_CONTEXT, config, "false", "Specifies whetere a client certificate should be used for authentication");
    }

    @Test
    void editConfig_numberFormat() {
      var config = "SystemDb.MaxConnections";
      table.clickButtonForEntry(config, "editConfigBtn");
      assertThatConfigEditModalIsVisible(SYSTEM_CONTEXT, config, "50", "Maximum number");
    }

    @Test
    void editConfig_enumerationFormat() {
      var config = "SystemTask.Failure.Behaviour";
      table.clickButtonForEntry(config, "editConfigBtn");
      assertThatConfigEditModalIsVisible(SYSTEM_CONTEXT, config, "FAIL_TASK_DO_RETRY", "Defines the behaviour");
    }

    @Test
    void restartHint() {
      var config = "Connector.HTTP.Address";
      assertEditConfig(SYSTEM_CONTEXT, config, "", "hi", "For servers with more than one IP address");
      Selenide.sleep(100); // restart will be evaluated by async file watcher
      refresh();
      table.valueForEntryShould(config, 2, exactText("hi"));
      var health = $(".health-messages");
      Selenide.Wait()
          .withTimeout(Duration.ofSeconds(5))
          .ignoring(AssertionError.class)
          .until(_ -> {
            health.find("a").click();
            Selenide.sleep(10);
            return health.attr("class").contains("active-topmenuitem");
          });
      health.$$("li").shouldHave(anyMatch("message contains", e -> e.getText().contains("Restart is required")));
      assertResetConfig(SYSTEM_CONTEXT, config);
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
      $(APPLICATION_CONTEXT.contentFilterBtn).scrollIntoView(ScrollIntoViewOptions.instant().block(Block.center).inline(Inline.center));
      table = new Table(APPLICATION_CONTEXT.tableId, "span");
    }

    @Test
    void searchConfig() {
      assertSearchConfigEntry();
    }

    @Test
    void hideDefaults() {
      var config = "Data.FilesDirectory";
      table.firstColumnShouldBe(itemWithText(config));
      toggleDefaultFilter(APPLICATION_CONTEXT);
      table.firstColumnShouldBe(noneMatch("Hide default values", e -> Objects.equals(e.getText(), config)));
      resetContentFilter(APPLICATION_CONTEXT);
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
      assertShowAppConfigFilter("Show Web Service Clients", "WebServiceClients.test-web.Properties.username");
    }

    private void assertShowAppConfigFilter(String filter, String config) {
      table.firstColumnShouldBe(noneMatch("Config should not be listed in the config table per default: " + config,
          element -> Objects.equals(element.getText(), config)));
      toggleFilter(APPLICATION_CONTEXT, List.of(filter));
      table.firstColumnShouldBe(itemWithText(config));
      resetContentFilter(APPLICATION_CONTEXT);
      table.firstColumnShouldBe(noneMatch("Config should not be listed in the config table after reset filter: " + config,
          element -> Objects.equals(element.getText(), config)));
    }

    @Test
    void dynamicExpressions() {
      toggleFilter(APPLICATION_CONTEXT, List.of("Show Rest Clients"));
      var dynamic = "RestClients.second-rest.Properties.appKey";
      table.clickButtonForEntry(dynamic, "editConfigBtn");
      new ConfigAssert(APPLICATION_CONTEXT.configPrefix)
          .assertDefault("${ivy.var.password}")
          .assertValue("${ivy.var.password}");
    }

    @Test
    void showConfigFile() {
      navigateTo("demo-portal");
      var key = "OverrideProject";
      assertShowConfigFile(APPLICATION_CONTEXT, key);
    }

    @Test
    void overrideProject_pmvSelector() {
      var config = "OverrideProject";
      table.clickButtonForEntry(config, "editConfigBtn");
      assertThatConfigEditModalIsVisible(APPLICATION_CONTEXT, config, " ", "Defines a project containing overriding SubProcesses", "");
      $(By.id(APPLICATION_CONTEXT.configPrefix + ":editConfigurationForm:editConfigurationValue"))
          .shouldHave(cssClass("ui-selectonemenu"));
    }
  }

  @Nested
  class StandardProcess {

    @BeforeEach
    void beforeEach() {
      Navigation.toApplicationDetail("demo-portal");
      $(APPLICATION_CONTEXT.contentFilterBtn).scrollIntoView(ScrollIntoViewOptions.instant().block(Block.center).inline(Inline.center));
      table = new Table(APPLICATION_CONTEXT.tableId, "span");
    }

    @Test
    void defaultPages() {
      var config = "StandardProcess.DefaultPages";
      table.clickButtonForEntry(config, "editConfigBtn");
      assertThatConfigEditModalIsVisible(APPLICATION_CONTEXT, config, "com.axonivy.portal:portal", "default-pages", "auto");
      $(By.id(APPLICATION_CONTEXT.configPrefix + ":editConfigurationForm:editConfigurationValue")).shouldHave(cssClass("ui-selectonemenu"));
    }
  }

  private void assertSearchConfigEntry() {
    table.firstColumnShouldBe(sizeGreaterThan(0));
    String search = table.getFirstColumnEntries().get(0);
    table.search(search);
    table.firstColumnShouldBe(size(1));
    table.firstColumnShouldBe(exactTexts(search));
  }

  private void toggleDefaultFilter(SelectorContext context) {
    table.body().find(".default-value").shouldBe(visible);
    toggleFilter(context, List.of("Show only defined values"));
    table.body().find(".default-value").shouldNotBe(visible);
  }

  private void toggleFilter(SelectorContext context, List<String> filter) {
    $(context.contentFilterBtn).shouldBe(visible).click();
    String escapedContentFilterPrefix = context.contentFilterPrefix.replace(":", "\\:");
    $$("#" + escapedContentFilterPrefix + "\\:filterPanel .ui-chkbox").shouldBe(sizeGreaterThanOrEqual(1));
    var checkboxes = PrimeUi.selectManyCheckbox(By.cssSelector("#" + escapedContentFilterPrefix + "\\:filterForm\\:filterCheckboxes"));
    checkboxes.setCheckboxes(filter);
    $("#" + escapedContentFilterPrefix + "\\:filterForm\\:applyFilter").shouldBe(visible).click();
  }

  private void assertShowConfigFile(SelectorContext context, String key) {
    table.clickButtonForEntry(key, "tasksButton");
    table.buttonMenuForEntryShouldBeVisible(key, "activityMenu");
    table.clickButtonForEntry(key, "showFileBtn");
    $(By.id(context.configPrefix + ":showConfigurationFileModal")).shouldBe(visible);
    $(By.id(context.configPrefix + ":showConfigurationFileForm:codeBlock")).shouldHave(value(key));
  }

  private void assertResetConfig(SelectorContext context, String key) {
    table.clickButtonForEntry(key, "tasksButton");
    table.buttonMenuForEntryShouldBeVisible(key, "activityMenu");
    table.clickButtonForEntry(key, "resetConfigBtn");
    $(By.id(context.configPrefix + ":resetConfigConfirmDialog")).shouldBe(visible);

    $(By.id(context.configPrefix + ":resetConfigConfirmForm:resetConfigConfirmYesBtn")).click();
    $(By.id(context.configPrefix + ":form:msgs_container")).shouldHave(text(key), text("reset"));
  }

  private void assertEditConfig(SelectorContext context, String key, String value, String newValue, String desc) {
    table.clickButtonForEntry(key, "editConfigBtn");
    assertThatConfigEditModalIsVisible(context, key, value, desc, "");

    $(By.id(context.configPrefix + ":editConfigurationForm:editConfigurationValue")).clear();
    $(By.id(context.configPrefix + ":editConfigurationForm:editConfigurationValue")).sendKeys(newValue);
    $(By.id(context.configPrefix + ":editConfigurationForm:saveEditConfiguration")).click();
    assertAndCloseGrowl(context, key);
    table.valueForEntryShould(key, 2, exactText(newValue));
    $(By.id(context.configPrefix + ":form:msgs_container")).shouldNotBe(visible);
  }

  private void assertThatConfigEditModalIsVisible(SelectorContext context, String key, String value, String desc) {
    assertThatConfigEditModalIsVisible(context.configPrefix, key, value, desc, value);
  }

  private void assertThatConfigEditModalIsVisible(SelectorContext context, String key, String value, String desc,
      String defaultValue) {
    assertThatConfigEditModalIsVisible(context.configPrefix, key, value, desc, defaultValue);
  }

  public static void assertThatConfigEditModalIsVisible(String idPrefix, String key, String value,
      String desc, String defaultValue) {
    new ConfigAssert(idPrefix)
        .assertKey(key)
        .assertDesc(desc)
        .assertDefault(defaultValue)
        .assertValue(value);
  }

  private void resetContentFilter(SelectorContext context) {
    $(context.contentFilterBtn).shouldBe(visible).click();
    String escapedContentFilterPrefix = context.contentFilterPrefix.replace(":", "\\:");
    $("#" + escapedContentFilterPrefix + "\\:filterForm\\:resetFilterBtn").shouldBe(visible).click();
    assertContentFilterText(context, "Filter: none");
  }

  private void assertContentFilterText(SelectorContext context, String expectedFilter) {
    $(context.contentFilterBtn).shouldHave(attribute("title", expectedFilter));
  }

  private void assertAndCloseGrowl(SelectorContext context, String key) {
    $(By.id(context.configPrefix + ":form:msgs_container")).shouldHave(text(key), text("saved"));
    executeJavaScript("arguments[0].click();", $(".ui-growl-icon-close"));
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
      if (classAttr.contains("ui-chkbox")) {
        PrimeUi.selectBooleanCheckbox(By.id(idPrefix + ":editConfigurationForm:editConfigurationValue"))
            .shouldBeChecked(Boolean.parseBoolean(value));
      } else if (classAttr.contains("ui-inputnumber")) {
        $(By.id(idPrefix + ":editConfigurationForm:editConfigurationValue_input"))
            .shouldBe(exactValue(value));
      } else if (classAttr.contains("ui-selectonemenu")) {
        SelectOneMenu menu = PrimeUi.selectOne(By.id(idPrefix + ":editConfigurationForm:editConfigurationValue"));
        menu.selectedItemShould(exactText(value));
      } else if (configValue.getTagName().contains("textarea")) {
        configValue.shouldNotBe(visible).shouldBe(exactValue(value));
        $(".CodeMirror").shouldBe(visible, text("this is a json file"));
      } else {
        $(By.id(idPrefix + ":editConfigurationForm:editConfigurationValue")).shouldBe(exactValue(value));
      }
      return this;
    }
  }
}
