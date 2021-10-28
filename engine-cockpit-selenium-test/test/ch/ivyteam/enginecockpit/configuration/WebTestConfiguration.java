package ch.ivyteam.enginecockpit.configuration;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.CollectionCondition.noneMatch;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.refresh;
import static org.assertj.core.api.Assertions.assertThat;

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
public class WebTestConfiguration {
  private static final By TABLE_ID = By.id("config:form:configTable");
  private Table table;

  @BeforeEach
  void beforeEach() {
    login();
    Configuration.timeout = 10000;
  }

  @Test
  void testEmailUrlFilter() {
    String filter = "EMail";
    $("#mailConfigForm\\:configureEmailBtn").shouldBe(visible).click();
    assertUrlFiltering(filter);
  }

  @Test
  void testSystemDbConfigUrl() {
    $("#configureSystemDbBtn").shouldBe(visible).click();
    assertCurrentUrlContains("systemdb.xhtml");
  }

  @Nested
  class System {
    @BeforeEach
    void beforeEach() {
      Navigation.toSystemConfig();
      table = new Table(TABLE_ID);
    }

    @Test
    void testSystemConfig() {
      $("h1").shouldHave(text("System Config"));
    }

    @Test
    void testSearchConfig() {
      assertSearchConfigEntry();
    }

    @Test
    void testHideDefaults() {
      var config = "Data.AppDirectory";
      $("#contentFilter\\:form\\:filterBtn").shouldHave(text("Filter: none"));
      table.firstColumnShouldBe(itemWithText(config));
      toggleDefaultFilter();
      $("#contentFilter\\:form\\:filterBtn").shouldHave(text("Filter: defined"));
      table.firstColumnShouldBe(noneMatch("Config not visible", e -> config.equals(e.getText())));

      $("#contentFilter\\:form\\:resetFilter").shouldBe(visible).click();
      $("#contentFilter\\:form\\:filterBtn").shouldHave(text("Filter: none"));
      table.firstColumnShouldBe(itemWithText(config));
    }

    @Test
    void showSecuritySystemConfigs() {
      var config = "SecuritySystems.test-ad.Provider";
      $("#contentFilter\\:form\\:filterBtn").shouldHave(text("Filter: none"));
      table.firstColumnShouldBe(noneMatch("Config not visible", e -> config.equals(e.getText())));
      toggleFilter(List.of("Show Security Systems"));
      $("#contentFilter\\:form\\:filterBtn").shouldHave(text("Filter: Security Systems"));
      table.firstColumnShouldBe(itemWithText(config));
      $("#contentFilter\\:form\\:resetFilter").shouldBe(visible).click();
      $("#contentFilter\\:form\\:filterBtn").shouldHave(text("Filter: none"));
      table.firstColumnShouldBe(noneMatch("Config not visible", e -> config.equals(e.getText())));
    }

    @Test
    void testShowConfigFile() {
      String key = "Connector.HTTP.AllowTrace";
      assertShowConfigFile(key);
    }

    @Test
    void testNewConfigInvalid() {
      $("#newConfigBtn").click();
      assertNewConfigInvalid();
    }

    @Test
    void testNewEditAndResetConfig() {
      String key = "testKey";
      String value = "testValue";
      $("#newConfigBtn").click();
      assertNewConfig(key, value);
      assertEditConfig(key, value, "newValue", "");
      assertResetConfig(key);
    }

    @Test
    void testSearchAndUpdateConfig() {
      String config = "EMail.Server.EncryptionMethod";
      table.firstColumnShouldBe(sizeGreaterThan(0));
      table.row(config).shouldHave(cssClass("default-value"));
      Selenide.sleep(200);
      table.search("email");
      table.firstColumnShouldBe(size(9));

      table.clickButtonForEntry(config, "editConfigBtn");
      PrimeUi.selectOne(By.id("config:editConfigurationForm:editConfigurationValue"))
              .selectItemByLabel("SSL");
      $("#config\\:saveEditConfiguration").click();
      table.row(config).shouldNotHave(cssClass("default-value"));

      toggleFilter(List.of("Show only defined values"));
      table.firstColumnShouldBe(size(1));
      assertResetConfig(config);
      table.firstColumnShouldBe(size(0));
    }

    @Test
    void testEditConfig_booleanFormat() {
      String config = "EMail.Server.SSL.UseKey";
      table.clickButtonForEntry(config, "editConfigBtn");
      assertThatConfigEditModalIsVisible(config, "false", "");
    }

    @Test
    void testEditConfig_numberFormat() {
      String config = "Elasticsearch.ExternalServer.BootTimeout";
      table.clickButtonForEntry(config, "editConfigBtn");
      assertThatConfigEditModalIsVisible(config, "60", "Defines how long");
    }

    @Test
    void testEditConfig_daytimeFormat() {
      String config = "EMail.DailyTaskSummary.TriggerTime";
      table.clickButtonForEntry(config, "editConfigBtn");
      assertThatConfigEditModalIsVisible(config, "00:00", "Time of day");
    }

    @Test
    void testEditConfig_enumerationFormat() {
      String config = "SystemTask.Failure.Behaviour";
      table.clickButtonForEntry(config, "editConfigBtn");
      assertThatConfigEditModalIsVisible(config, "FAIL_TASK_DO_RETRY", "Defines the behaviour");
    }

    @Test
    void testRestartHint() {
      String config = "Connector.HTTP.Address";
      assertEditConfig(config, "", "hi", "https://tomcat.apache.org");
      refresh();
      $(".restart-notification").shouldBe(visible);
      table.valueForEntryShould(config, 2, exactText("hi"));
      assertResetConfig(config);
      refresh();
      $(".restart-notification").shouldNotBe(visible);
    }

  }

  @Nested
  class Application {
    @BeforeEach
    void beforeEach() {
      Navigation.toApplicationDetail("test-ad");
      $(By.id("contentFilter:form:filterBtn"))
              .scrollIntoView("{behavior: \"instant\", block: \"center\", inline: \"center\"}");
      table = new Table(TABLE_ID);
    }

    @Test
    void testSearchConfig() {
      assertSearchConfigEntry();
    }

    @Test
    void testHideDefaults() {
      var config = "Data.FilesDirectory";
      assertThat(table.getFirstColumnEntries()).contains(config);
      toggleDefaultFilter();
      assertThat(table.getFirstColumnEntries()).doesNotContain(config);
      $("#contentFilter\\:form\\:filterBtn").shouldBe(visible).click();
      $("#contentFilter\\:form\\:resetFilterBtn").shouldBe(visible).click();
      assertThat(table.getFirstColumnEntries()).contains(config);
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
      assertThat(table.getFirstColumnEntries()).doesNotContain(config);
      toggleFilter(List.of(filter));
      assertThat(table.getFirstColumnEntries()).contains(config);
      $("#contentFilter\\:form\\:filterBtn").shouldBe(visible).click();
      $("#contentFilter\\:form\\:resetFilterBtn").shouldBe(visible).click();
      assertThat(table.getFirstColumnEntries()).doesNotContain(config);
    }

    @Test
    void dynamicExpressions() {
      toggleFilter(List.of("Show Rest Clients"));
      String dynamic = "RestClients.second-rest.Properties.appKey";
      table.clickButtonForEntry(dynamic, "editConfigBtn");
      new ConfigAssert("config")
              .assertDefault("${ivy.var.password}")
              .assertValue("${ivy.var.password}");
    }

    @Test
    void testShowConfigFile() {
      String key = "SecuritySystem";
      assertShowConfigFile(key);
    }

    @Test
    void testNewConfigInvalid() {
      $("#newConfigBtn").click();
      assertNewConfigInvalid();
    }

    @Test
    void testNewEditAndResetConfig() {
      String key = "testKey";
      String value = "testValue";
      $("#newConfigBtn").click();
      assertNewConfig(key, value);
      assertEditConfig(key, value, "newValue", "");
      assertResetConfig(key);
    }

    @Test
    void overrideProject_pmvSelector() {
      String config = "OverrideProject";
      String value = "notMyLibrary";

      $("#newConfigBtn").click();
      assertNewConfig(config, value);

      table.clickButtonForEntry(config, "editConfigBtn");
      assertThatConfigEditModalIsVisible(config, " ", "", "");
      $(By.id("config:editConfigurationForm:editConfigurationValue"))
              .shouldHave(cssClass("ui-selectonemenu"));
    }
  }

  @Nested
  class ApplicationPortal {
    @BeforeEach
    void beforeEach() {
      Navigation.toApplicationDetail("demo-portal");
      $(By.id("contentFilter:form:filterBtn"))
              .scrollIntoView("{behavior: \"instant\", block: \"center\", inline: \"center\"}");
      table = new Table(TABLE_ID);
    }

    @Test
    void testStandardProcess_defaultPages() {
      String config = "StandardProcess.DefaultPages";
      table.clickButtonForEntry(config, "editConfigBtn");
      assertThatConfigEditModalIsVisible(config, "ch.ivyteam.ivy.project.portal:portalTemplate",
              "standard-processes", "");
      $(By.id("config:editConfigurationForm:editConfigurationValue"))
              .shouldHave(cssClass("ui-selectonemenu"));
    }

    @Test
    void testStandardProcess_mailNotification() {
      String config = "StandardProcess.MailNotification";
      table.clickButtonForEntry(config, "editConfigBtn");
      assertThatConfigEditModalIsVisible(config, " ", "standard-processes", "");
      $(By.id("config:editConfigurationForm:editConfigurationValue"))
              .shouldHave(cssClass("ui-selectonemenu"));
    }
  }

  private void assertUrlFiltering(String filter) {
    assertCurrentUrlContains("systemconfig.xhtml?filter=" + filter);
    table = new Table(TABLE_ID);
    assertThat(table.getSearchFilter()).isEqualTo(filter);
    table.firstColumnShouldBe(size(9));
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
    $("#contentFilter\\:form\\:filterBtn").shouldBe(visible).click();
    $$("#contentFilter\\:form\\:filterPanel .ui-chkbox").shouldBe(sizeGreaterThanOrEqual(1));
    var checkboxes = PrimeUi.selectManyCheckbox(By.cssSelector("#contentFilter\\:form\\:filterCheckboxes"));
    checkboxes.setCheckboxes(filter);
    $("#contentFilter\\:form\\:applyFilter").shouldBe(visible).click();
  }

  private void assertShowConfigFile(String key) {
    table.clickButtonForEntry(key, "tasksButton");
    table.buttonMenuForEntryShouldBeVisible(key, "activityMenu");
    table.clickButtonForEntry(key, "showFileBtn");
    $("#config\\:showConfigurationFileModal").shouldBe(visible);
    $(".code-block").shouldHave(text(key));
  }

  private void assertNewConfigInvalid() {
    $("#config\\:newConfigurationModal").shouldBe(visible);
    $("#config\\:newConfigurationForm\\:newConfigurationKey").shouldBe(exactValue(""));
    $("#config\\:newConfigurationForm\\:saveNewConfiguration").click();
    $("#config\\:newConfigurationForm\\:newConfigurationKeyMessage").shouldBe(visible,
            exactText("Value is required"));
    $("#config\\:newConfigurationForm\\:newConfigurationValueMessage").shouldBe(visible,
            exactText("Value is required"));
  }

  private void assertResetConfig(String key) {
    table.clickButtonForEntry(key, "tasksButton");
    table.buttonMenuForEntryShouldBeVisible(key, "activityMenu");
    table.clickButtonForEntry(key, "resetConfigBtn");
    $("#config\\:resetConfigConfirmDialog").shouldBe(visible);

    $("#config\\:resetConfigConfirmForm\\:resetConfigConfirmYesBtn").click();
    $("#config\\:form\\:msgs_container").shouldHave(text(key), text("reset"));
  }

  private void assertNewConfig(String key, String value) {
    $("#config\\:newConfigurationModal").shouldBe(visible);

    $("#config\\:newConfigurationForm\\:newConfigurationKey").sendKeys(key);
    $("#config\\:newConfigurationForm\\:newConfigurationValue").sendKeys(value);
    $("#config\\:newConfigurationForm\\:saveNewConfiguration").click();
    $("#config\\:form\\:msgs_container").shouldHave(text(key), text("saved"));
    table.valueForEntryShould(key, 2, exactText(value));
  }

  private void assertEditConfig(String key, String value, String newValue, String desc) {
    table.clickButtonForEntry(key, "editConfigBtn");
    assertThatConfigEditModalIsVisible(key, value, desc, "");

    $("#config\\:editConfigurationForm\\:editConfigurationValue").clear();
    $("#config\\:editConfigurationForm\\:editConfigurationValue").sendKeys(newValue);
    $("#config\\:saveEditConfiguration").click();
    $("#config\\:form\\:msgs_container").shouldHave(text(key), text("saved"));
    table.valueForEntryShould(key, 2, exactText(newValue));
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
                .shouldBeChecked(Boolean.valueOf(value));
      } else if (StringUtils.contains(classAttr, "ui-inputnumber")) {
        $(By.id(idPrefix + ":editConfigurationForm:editConfigurationValue_input"))
                .shouldBe(exactValue(value));
      } else if (StringUtils.contains(classAttr, "ui-selectonemenu")) {
        SelectOneMenu menu = PrimeUi
                .selectOne(By.id(idPrefix + ":editConfigurationForm:editConfigurationValue"));
        assertThat(menu.getSelectedItem()).isEqualTo(value);
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
