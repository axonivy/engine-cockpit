package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.PropertyEditor;
import ch.ivyteam.enginecockpit.util.Tab;

@IvyWebTest
class WebTestDatabaseDetail {
  private static final String DATABASE_NAME = "test-db";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toDatabases();
    Tab.APP.switchToDefault();
    Navigation.toDatabaseDetail(DATABASE_NAME);
    Configuration.fastSetValue = true;
  }

  @Test
  void detailOpen() {
    assertCurrentUrlContains("databasedetail.xhtml?app=" + Tab.DEFAULT_APP + "&name=" + DATABASE_NAME);
    $$(".card").shouldHave(size(4));
    $("#databaseConfiguration\\:databaseConfigurationForm\\:name").shouldBe(exactText(DATABASE_NAME));

    $(".layout-topbar-actions .help-dialog").shouldBe(visible).click();
    $(By.id("helpDatabaseDialog:helpServicesModal")).shouldBe(visible);
    $(By.id("helpDatabaseDialog:helpServicesForm:codeBlock")).shouldBe(value(DATABASE_NAME));
  }

  @Test
  void databaseTestConnection() {
    assertDatabaseTestConnection("Error");

    Navigation.toDatabaseDetail("realdb");
    assertDatabaseTestConnection("Successfully connected to database");

    $("#databaseConfiguration\\:databaseConfigurationForm\\:userName").sendKeys("1");
    assertDatabaseTestConnection("Error");
  }

  private void assertDatabaseTestConnection(String expectedLog) {
    $("#databaseConfiguration\\:connResult\\:connectionTestModel").shouldNotBe(visible);
    $("#databaseConfiguration\\:databaseConfigurationForm\\:testDatabaseBtn").click();
    $("#databaseConfiguration\\:connResult\\:connectionTestModel").shouldBe(visible);
    $("#databaseConfiguration\\:connResult\\:connTestForm\\:testConnectionBtn").click();
    $("#databaseConfiguration\\:connResult\\:connTestForm\\:resultConnect").shouldBe(text(expectedLog));
    $("#databaseConfiguration\\:connResult\\:connectionTestModel .ui-dialog-titlebar-close").click();
    $("#databaseConfiguration\\:connResult\\:connectionTestModel").shouldNotBe(visible);
  }

  @Test
  void saveAndResetChanges() {
    setConfiguration("url", "org.postgresql.Driver", "testUser", "13");
    Selenide.refresh();
    checkConfiguration("url", "org.postgresql.Driver", "testUser", "13");
    resetConfiguration();
    Selenide.refresh();
    checkConfiguration("jdbc:mysql://localhost:3306/test-db", "com.mysql.cj.jdbc.Driver", "user", "5");
  }

  @Test
  void addProperty() {
    var editor = new PropertyEditor("databaseProperties:databasePropertiesForm:databasePropertiesTable");
    editor.addProperty("testProperty", "testValue");
    editor.deleteProperty("testProperty");
  }

  @Test
  void editProperty() {
    var editor = new PropertyEditor("databaseProperties:databasePropertiesForm:databasePropertiesTable");
    editor.editProperty("test", "editValue");
    editor.editProperty("test", "testvalue");
  }

  @Test
  void liveStats() {
    EngineCockpitUtil.assertLiveStats(List.of("Database Connections", "Database Queries",
        "Database Query Execution Time"), "test-db", false);
  }

  private void setConfiguration(String url, String driverName, String username, String connections) {
    $("#databaseConfiguration\\:databaseConfigurationForm\\:url").shouldBe(visible).clear();
    $("#databaseConfiguration\\:databaseConfigurationForm\\:url").sendKeys(url);

    $("#databaseConfiguration\\:databaseConfigurationForm\\:driver_input").clear();
    $("#databaseConfiguration\\:databaseConfigurationForm\\:driver > button").click();
    $("#databaseConfiguration\\:databaseConfigurationForm\\:driver_panel").shouldBe(visible);
    $x("//*[@id='databaseConfiguration:databaseConfigurationForm:driver_panel']//li[text()='" + driverName + "']").click();

    $("#databaseConfiguration\\:databaseConfigurationForm\\:userName").clear();
    $("#databaseConfiguration\\:databaseConfigurationForm\\:userName").sendKeys(username);

    PrimeUi.inputNumber(By.id("databaseConfiguration:databaseConfigurationForm:maxConnections")).setValue(connections);

    $("#databaseConfiguration\\:databaseConfigurationForm\\:saveDatabaseConfig").click();
    $("#databaseConfiguration\\:databaseConfigurationForm\\:databaseConfigMsg_container")
        .shouldBe(text("Database configuration saved"));
  }

  private void checkConfiguration(String url, String driverName, String username, String connections) {
    $("#databaseConfiguration\\:databaseConfigurationForm\\:url").shouldBe(exactValue(url));
    $("#databaseConfiguration\\:databaseConfigurationForm\\:driver_input").shouldBe(exactValue(driverName));
    $("#databaseConfiguration\\:databaseConfigurationForm\\:userName").shouldBe(exactValue(username));
    $("#databaseConfiguration\\:databaseConfigurationForm\\:maxConnections_input").shouldBe(exactValue(connections));
  }

  private void resetConfiguration() {
    $("#databaseConfiguration\\:databaseConfigurationForm\\:resetConfig").click();
    $("#databaseConfiguration\\:databaseConfigurationForm\\:resetDbConfirmDialog").shouldBe(visible);
    $("#databaseConfiguration\\:databaseConfigurationForm\\:resetDbConfirmYesBtn").click();
    $("#databaseConfiguration\\:databaseConfigurationForm\\:databaseConfigMsg_container")
        .shouldBe(text("Database configuration reset"));
  }

}
