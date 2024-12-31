package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
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
    $("#databaseConfigurationForm\\:name").shouldBe(exactText(DATABASE_NAME));

    $(".layout-topbar-actions .help-dialog").shouldBe(visible).click();
    $("#helpDatabaseDialog\\:helpServicesModal").shouldBe(visible);
    $(".code-block").shouldBe(text(DATABASE_NAME));
  }

  @Test
  void databaseTestConnection() {
    assertDatabaseTestConnection("Error");

    Navigation.toDatabaseDetail("realdb");
    assertDatabaseTestConnection("Successfully connected to database");

    $("#databaseConfigurationForm\\:userName").sendKeys("1");
    assertDatabaseTestConnection("Error");
  }

  private void assertDatabaseTestConnection(String expectedLog) {
    $("#connResult\\:connectionTestModel").shouldNotBe(visible);
    $("#databaseConfigurationForm\\:testDatabaseBtn").click();
    $("#connResult\\:connectionTestModel").shouldBe(visible);
    $("#connResult\\:connTestForm\\:testConnectionBtn").click();
    $("#connResult\\:connTestForm\\:resultConnect").shouldBe(text(expectedLog));
    $("#connResult\\:connectionTestModel .ui-dialog-titlebar-close").click();
    $("#connResult\\:connectionTestModel").shouldNotBe(visible);
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
    var editor = new PropertyEditor("databasePropertiesForm:databasePropertiesTable:newPropertyEditor:");
    editor.addProperty("testProperty", "testValue");
    $(By.id("databasePropertiesForm:databasePropertiesTable")).shouldHave(text("testProperty"));
    $(By.id("databasePropertiesForm:databasePropertiesTable")).shouldHave(text("testValue"));
    $(By.id("databasePropertiesForm:databasePropertiesTable:1:editPropertyEditor:deletePropertyBtn")).click();
  }

  @Test
  void editProperty() {
    var editor = new PropertyEditor("databasePropertiesForm:databasePropertiesTable:0:editPropertyEditor:");
    editor.editProperty("editValue");
    $(By.id("databasePropertiesForm:databasePropertiesTable")).shouldHave(text("editValue"));
    editor.editProperty("testvalue");
  }

  @Test
  void liveStats() {
    EngineCockpitUtil.assertLiveStats(List.of("Database Connections", "Database Queries",
        "Database Query Execution Time"), "test-db", false);
  }

  private void setConfiguration(String url, String driverName, String username, String connections) {
    $("#databaseConfigurationForm\\:url").shouldBe(visible).clear();
    $("#databaseConfigurationForm\\:url").sendKeys(url);

    $("#databaseConfigurationForm\\:driver_input").clear();
    $("#databaseConfigurationForm\\:driver > button").click();
    $("#databaseConfigurationForm\\:driver_panel").shouldBe(visible);
    $x("//*[@id='databaseConfigurationForm:driver_panel']//li[text()='" + driverName + "']").click();

    $("#databaseConfigurationForm\\:userName").clear();
    $("#databaseConfigurationForm\\:userName").sendKeys(username);

    PrimeUi.inputNumber(By.id("databaseConfigurationForm:maxConnections")).setValue(connections);

    $("#databaseConfigurationForm\\:saveDatabaseConfig").click();
    $("#databaseConfigurationForm\\:databaseConfigMsg_container")
        .shouldBe(text("Database configuration saved"));
  }

  private void checkConfiguration(String url, String driverName, String username, String connections) {
    $("#databaseConfigurationForm\\:url").shouldBe(exactValue(url));
    $("#databaseConfigurationForm\\:driver_input").shouldBe(exactValue(driverName));
    $("#databaseConfigurationForm\\:userName").shouldBe(exactValue(username));
    $("#databaseConfigurationForm\\:maxConnections_input").shouldBe(exactValue(connections));
  }

  private void resetConfiguration() {
    $("#databaseConfigurationForm\\:resetConfig").click();
    $("#databaseConfigurationForm\\:resetDbConfirmDialog").shouldBe(visible);
    $("#databaseConfigurationForm\\:resetDbConfirmYesBtn").click();
    $("#databaseConfigurationForm\\:databaseConfigMsg_container")
        .shouldBe(text("Database configuration reset"));
  }

}
