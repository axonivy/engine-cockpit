package ch.ivyteam.enginecockpit.system;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.createOldDb;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.deleteTempDb;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.resetConfig;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.waitUntilAjaxIsFinished;
import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.Condition.and;
import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.axonivy.ivy.webtest.primeui.widget.SelectBooleanCheckbox;
import com.axonivy.ivy.webtest.primeui.widget.SelectOneMenu;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestSystemDb {
  private static final String SYS_DB = System.getProperty("db.host",
          "db host not set via ${db.host} system property");
  private static final String SYS_DB_PW = "1234";
  private static final String SYS_DB_USER = "root";
  private static final String CONNECTION_BUTTON = "#systemDb\\:systemDbForm\\:checkConnectionButton";
  private static final String CONNECTION_PANEL = "#systemDb\\:systemDbForm\\:connectionPanel";
  private static final String OLD_DB_NAME = "old_version_150";
  private static final String TEST_DB_NAME = "temp";

  @BeforeAll
  static void setup() {
    createOldDb();
  }

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toSystemDb();
  }

  @AfterEach
  void afterEach() {
    resetConfig();
    deleteTempDb();
  }

  @Test
  void testSystemDb() {
    $("h2").shouldBe(text("System Database"));
    assertDefaultValues();
    assertSystemDbCreationDialog();
    assertSystemDbCreation();
  }

  @Test
  void testSaveConfiguration() {
    $(".sysdb-dynamic-form-user").sendKeys(" ");
    $(".sysdb-dynamic-form-user").clear();
    $(CONNECTION_PANEL).shouldBe(text("Connection state unknown"));
    $("#saveUnknownSystemDbConfig").shouldBe(visible).click();
    $("#saveUnknownConnectionModel").shouldBe(visible);
    $("#saveUnknownConnectionForm\\:saveUnknownConneciton").click();
    $("#saveUnknownConnectionModel").shouldNotBe(visible);
    $("#systemDbSave_container").shouldBe(text("System Database config saved successfully"));

    $(CONNECTION_BUTTON).click();
    $(CONNECTION_PANEL).shouldBe(text("Connected"));
    $("#saveSystemDbConfig").shouldBe(visible).click();
    $("#systemDbSave_container").shouldBe(text("System Database config saved successfully"));
  }

  @Test
  void testConnectionResults() {
    assertConnectionResults();
  }

  @Test
  void testDefaultPortSwitch() {
    assertDefaultPortSwitch();
  }

  @Test
  void testDatabaseDropdownSwitch() {
    assertDatabaseTypeSwitch();
  }

  @Test
  void testAdditionalProperties() {
    assertAdditionalProperties();
  }

  @Test
  void liveStats() {
    EngineCockpitUtil.assertLiveStats(List.of("Connections", "Transactions", "Processing Time"));
  }

  private static void insertDbConnection(String database, String driverName, String host,
          String databaseName, String user, String password) {
    SelectOneMenu dbType = PrimeUi.selectOne(By.id("systemDb:systemDbForm:databaseType"));
    SelectOneMenu dbDriver = PrimeUi.selectOne(By.id("systemDb:systemDbForm:databaseDriver"));
    dbType.selectItemByLabel(database);
    $(CONNECTION_PANEL).shouldBe(text("Connection state unknown"));
    dbDriver.selectItemByLabel(driverName);
    $(".sysdb-dynamic-form-host").clear();
    $(".sysdb-dynamic-form-host").sendKeys(host);
    $(".sysdb-dynamic-form-databasename").clear();
    $(".sysdb-dynamic-form-databasename").sendKeys(databaseName);
    $(".sysdb-dynamic-form-user").clear();
    $(".sysdb-dynamic-form-user").sendKeys(user);
    $(".sysdb-dynamic-form-password").clear();
    $(".sysdb-dynamic-form-password").sendKeys(password);
    waitUntilAjaxIsFinished();
    $(CONNECTION_BUTTON).shouldBe(enabled, visible);
  }

  public static void assertSystemDbCreationDialog() {
    insertDbConnection("MySQL", "mySQL", SYS_DB, TEST_DB_NAME, SYS_DB_USER, SYS_DB_PW);
    $(CONNECTION_BUTTON).click();
    $(CONNECTION_PANEL)
            .shouldBe(text("Missing Database/Schema"), text("Create system database."));
    $("#systemDb\\:systemDbForm\\:createDatabaseButton").shouldBe(enabled);

    $("#systemDb\\:systemDbForm\\:createDatabaseButton").click();
    $("#systemDb\\:createDatabaseDialog").shouldBe(visible);
    $(".creation-param-databasename").shouldBe(exactValue(TEST_DB_NAME));
  }

  public static void assertSystemDbCreation() {
    $(By.id("systemDb:createDatabaseForm:confirmCreateButton")).click();
    $(By.id("systemDb:createDatabaseForm:confirmCreateButton")).shouldNotBe(enabled);
    $("#systemDb\\:createDatabaseForm\\:confirmCreateButton > .ui-icon")
            .shouldHave(cssClass("si-is-spinning"));
    $("#systemDb\\:createDatabaseForm\\:closeCreationButton")
            .shouldBe(and("wait until db created", appear, enabled), Duration.ofSeconds(20));
    $("#systemDb\\:createDatabaseForm\\:creationError").shouldNot(exist);
    $("#systemDb\\:createDatabaseForm\\:creationInfo").shouldBe(text("The database was created successfully"));
    $("#systemDb\\:createDatabaseForm\\:closeCreationButton").click();
    $("#systemDb\\:createDatabaseDialog").shouldNotBe(visible);
    $(CONNECTION_PANEL).shouldBe(text("Connected"));
  }

  public static void assertSystemDbConversionDialog() {
    insertDbConnection("MySQL", "mySQL", SYS_DB, OLD_DB_NAME, SYS_DB_USER, SYS_DB_PW);
    $(CONNECTION_BUTTON).click();
    $(CONNECTION_PANEL).shouldBe(text("Database too old"), text("Convert system database."));
  }

  public static void assertConnectionResults() {
    insertDbConnection("MySQL", "mySQL", "db2", TEST_DB_NAME, SYS_DB_USER, SYS_DB_PW);
    $(CONNECTION_BUTTON).click();
    $(CONNECTION_PANEL).shouldBe(text("Incorrect host or port"));

    Selenide.refresh();
    insertDbConnection("MySQL", "mySQL", SYS_DB, TEST_DB_NAME, SYS_DB_USER, SYS_DB_PW);
    SelectBooleanCheckbox defaultPort = PrimeUi.selectBooleanCheckbox(
            By.cssSelector(".sysdb-dynamic-form-port-default-checkbox"));
    defaultPort.removeChecked();
    $(".sysdb-dynamic-form-port input").shouldBe(enabled);
    $(".sysdb-dynamic-form-port input").clear();
    $(".sysdb-dynamic-form-port input").sendKeys("1");
    waitUntilAjaxIsFinished();
    $(CONNECTION_BUTTON).click();
    $(CONNECTION_PANEL).shouldBe(text("Incorrect host or port"));

    Selenide.refresh();
    insertDbConnection("MySQL", "mySQL", SYS_DB, TEST_DB_NAME, "root2", SYS_DB_PW);
    $(CONNECTION_BUTTON).click();
    $(CONNECTION_PANEL).shouldBe(text("Incorrect username or password"));

    Selenide.refresh();
    insertDbConnection("MySQL", "mySQL", SYS_DB, TEST_DB_NAME, SYS_DB_USER, "12345");
    $(CONNECTION_BUTTON).click();
    $(CONNECTION_PANEL).shouldBe(text("Incorrect username or password"));
  }

  public static void assertAdditionalProperties() {
    Table table = new Table(By.id("systemDb:systemDbForm:additionalPropertiesTable"));
    $("#systemDb\\:systemDbForm\\:additionalPropertiesTable").shouldBe(text("No records found."));

    $("#systemDb\\:systemDbForm\\:newAdditionalPropertyBtn").click();
    $("#systemDb\\:addAdditionalPropertyDialog").shouldBe(visible);
    $("#systemDb\\:addAdditionalPropertyForm\\:key").shouldBe(value(""));
    $("#systemDb\\:addAdditionalPropertyForm\\:keyMessage").shouldBe(empty);
    $("#systemDb\\:addAdditionalPropertyForm\\:value").shouldBe(value(""));
    $("#systemDb\\:addAdditionalPropertyForm\\:valueMessage").shouldBe(empty);

    $("#systemDb\\:addAdditionalPropertyForm\\:saveProperty").click();
    $("#systemDb\\:addAdditionalPropertyForm\\:keyMessage").shouldBe(text("Value is required"));
    $("#systemDb\\:addAdditionalPropertyForm\\:valueMessage").shouldBe(text("Value is required"));

    $("#systemDb\\:addAdditionalPropertyForm\\:key").sendKeys("test");
    $("#systemDb\\:addAdditionalPropertyForm\\:saveProperty").click();
    $("#systemDb\\:addAdditionalPropertyForm\\:keyMessage").shouldBe(empty);
    $("#systemDb\\:addAdditionalPropertyForm\\:valueMessage").shouldBe(text("Value is required"));

    $("#systemDb\\:addAdditionalPropertyForm\\:value").sendKeys("testValue");
    $("#systemDb\\:addAdditionalPropertyForm\\:saveProperty").click();
    $("#systemDb\\:addAdditionalPropertyDialog").shouldNotBe(visible);
    table.firstColumnShouldBe(exactTexts("test"));
    table.valueForEntryShould("test", 2, exactText("testValue"));
    $(CONNECTION_PANEL).shouldBe(text("Connection state unknown"));

    table.clickButtonForEntry("test", "removeAdditionalProperty");
    $("#systemDb\\:systemDbForm\\:additionalPropertiesTable").shouldBe(text("No records found."));
    $(CONNECTION_PANEL).shouldBe(text("Connection state unknown"));
  }

  public static void assertDefaultValues() {
    $(CONNECTION_PANEL).shouldBe(text("Connected"));
    $("#systemDb\\:systemDbForm\\:databaseType_label").shouldBe(exactText("Hypersonic SQL Db"));
    $("#systemDb\\:systemDbForm\\:databaseDriver_label").shouldBe(exactText("HSQL Db Memory"));
    $(".sysdb-dynamic-form-databasename").shouldBe(exactValue("AxonIvySystemDatabase"));
    $(".sysdb-dynamic-form-user").shouldBe(exactValue(""));
    $(".sysdb-dynamic-form-password").shouldBe(exactValue(""));
  }

  public static void assertDefaultPortSwitch() {
    SelectOneMenu dbType = PrimeUi.selectOne(By.id("systemDb:systemDbForm:databaseType"));
    dbType.selectItemByLabel("MySQL");
    $(".sysdb-dynamic-form-port input").shouldBe(visible);

    SelectBooleanCheckbox defaultPort = PrimeUi.selectBooleanCheckbox(
            By.cssSelector(".sysdb-dynamic-form-port-default-checkbox"));
    $(".sysdb-dynamic-form-port input").shouldNotBe(enabled);
    defaultPort.shouldBeChecked(true);

    defaultPort.removeChecked();
    defaultPort.shouldBeChecked(false);
    $(".sysdb-dynamic-form-port input").shouldBe(enabled);

    defaultPort.setChecked();
    defaultPort.shouldBeChecked(true);
    $(".sysdb-dynamic-form-port input").shouldNotBe(enabled);
  }

  public static void assertDatabaseTypeSwitch() {
    SelectOneMenu dbType = PrimeUi.selectOne(By.id("systemDb:systemDbForm:databaseType"));
    SelectOneMenu dbDriver = PrimeUi.selectOne(By.id("systemDb:systemDbForm:databaseDriver"));
    assertThat(dbType.getSelectedItem()).isEqualTo("Hypersonic SQL Db");
    assertThat(dbDriver.getSelectedItem()).isEqualTo("HSQL Db Memory");
    $(".sysdb-dynamic-form-databasename").shouldBe(exactValue("AxonIvySystemDatabase"));

    dbType.selectItemByLabel("Oracle");
    assertThat(dbType.getSelectedItem()).isEqualTo("Oracle");
    assertThat(dbDriver.getSelectedItem()).isEqualTo("Oracle Thin");
    $(".sysdb-dynamic-form-oracleservicename").shouldBe(exactValue(""));
    $(".sysdb-dynamic-form-port input").shouldBe(exactValue("1521"));

    dbType.selectItemByLabel("MySQL");
    assertThat(dbType.getSelectedItem()).isEqualTo("MySQL");
    assertThat(dbDriver.getSelectedItem()).isEqualTo("mySQL");
    $(".sysdb-dynamic-form-host").shouldBe(exactValue("localhost"));
    $(".sysdb-dynamic-form-port input").shouldBe(exactValue("3306"));

    dbType.selectItemByLabel("Hypersonic SQL Db");
    assertThat(dbType.getSelectedItem()).isEqualTo("Hypersonic SQL Db");
    assertThat(dbDriver.getSelectedItem()).isEqualTo("HSQL Db Memory");
  }

}
