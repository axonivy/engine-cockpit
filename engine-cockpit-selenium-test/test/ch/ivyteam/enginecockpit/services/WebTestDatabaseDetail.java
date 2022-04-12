package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.exist;
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
import ch.ivyteam.enginecockpit.util.AppTab;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestDatabaseDetail {
  private static final String DATABASE_NAME = "test-db";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toDatabases();
    AppTab.switchToDefault();
    Navigation.toDatabaseDetail(DATABASE_NAME);
    Configuration.fastSetValue = true;
  }

  @Test
  void testDetailOpen() {
    assertCurrentUrlContains("databasedetail.xhtml?databaseName=" + DATABASE_NAME);
    $$(".ui-panel").shouldHave(size(4));
    $("#databaseConfigurationForm\\:name").shouldBe(exactText(DATABASE_NAME));

    $("#breadcrumbOptions > a[href='#']").shouldBe(visible).click();
    $("#helpDatabaseDialog\\:helpServicesModal").shouldBe(visible);
    $(".code-block").shouldBe(text(DATABASE_NAME));
  }

  @Test
  void testDatabaseTestConnection() {
    $("#connResult\\:connectionTestModel").shouldNotBe(visible);
    $("#databaseConfigurationForm\\:testDatabaseBtn").click();
    $("#connResult\\:connectionTestModel").shouldBe(visible);
    $("#connResult\\:connTestForm\\:testConnectionBtn").click();
    $("#connResult\\:connTestForm\\:resultLog_content").shouldBe(text("Error"));

    Navigation.toDatabaseDetail("realdb");

    $("#connResult\\:connectionTestModel").shouldNotBe(visible);
    $("#databaseConfigurationForm\\:testDatabaseBtn").click();
    $("#connResult\\:connectionTestModel").shouldBe(visible);
    $("#connResult\\:connTestForm\\:testConnectionBtn").click();
    $("#connResult\\:connTestForm\\:resultLog_content").shouldBe(text("Successfully connected to database"));
  }

  @Test
  void testSaveAndResetChanges() {
    setConfiguration("url", "org.postgresql.Driver", "testUser", "13");
    Selenide.refresh();
    checkConfiguration("url", "org.postgresql.Driver", "testUser", "13");
    resetConfiguration();
    Selenide.refresh();
    checkConfiguration("jdbc:mysql://localhost:3306/test-db", "com.mysql.cj.jdbc.Driver", "user", "5");
  }

  @Test
  void addEditRemoveProperty() {
    Table properties = new Table(By.id("databasePropertiesForm:databasePropertiesTable"));
    properties.firstColumnShouldBe(size(2));

    $("#databasePropertiesForm\\:newServicePropertyBtn").shouldBe(visible).click();
    $("#propertyModal").shouldBe(visible);
    $("#propertyForm\\:nameInput").sendKeys("bla");
    $("#propertyForm\\:valueInput").sendKeys("value");
    $("#propertyForm\\:saveProperty").click();
    properties.firstColumnShouldBe(size(3));
    properties.valueForEntryShould("bla", 2, exactText("value"));
    $("#propertyModal").shouldNotBe(visible);

    properties.clickButtonForEntry("bla", "editPropertyBtn");
    $("#propertyModal").shouldBe(visible);
    $("#propertyForm\\:nameInput").shouldNotBe(exist);
    $("#propertyForm\\:valueInput").shouldBe(value("value")).sendKeys("1");
    $("#propertyForm\\:saveProperty").click();
    properties.firstColumnShouldBe(size(3));
    properties.valueForEntryShould("bla", 2, exactText("value1"));
    $("#propertyModal").shouldNotBe(visible);

    properties.clickButtonForEntry("bla", "deletePropertyBtn");
    properties.firstColumnShouldBe(size(2));
  }

  @Test
  void liveStats() {
    EngineCockpitUtil.assertLiveStats(List.of("Database Connections", "Database Queries",
            "Database Query Execution Time"), "Default > test-db");
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
