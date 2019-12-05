package ch.ivyteam.enginecockpit.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

public class WebTestExternalDatabaseDetail extends WebTestBase
{
  private static final String DATABASE_NAME = "test-db";
  
  @Test
  void testExternalDatabaseDetailOpen()
  {
    navigateToDatabaseDetail();
    webAssertThat(() -> assertThat(driver.getCurrentUrl()).endsWith("externaldatabasedetail.xhtml?databaseName=" + DATABASE_NAME));
    webAssertThat(() -> assertThat(driver.findElementsByClassName("ui-panel")).hasSize(4));
    webAssertThat(() -> assertThat(driver.findElementById("databaseConfigurationForm:name").getText()).isEqualTo(DATABASE_NAME));
  }
  
  @Test
  void testOpenExternalDatabaseHelp()
  {
    navigateToDatabaseDetail();
    
    driver.findElementByXPath("//div[@id='breadcrumbOptions']/a").click();
    saveScreenshot("help_modal");
    webAssertThat(() -> assertThat(driver.findElementById("helpExternalDatabaseDialog:helpServicesModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementByClassName("code-block").getText()).contains(DATABASE_NAME));
  }
  
  @Test
  void testExternalDatabaseTestConnection()
  {
    navigateToDatabaseDetail();
    
    webAssertThat(() -> assertThat(driver.findElementById("connResult:connectionTestModel").isDisplayed()).isFalse());
    driver.findElementById("databaseConfigurationForm:testDatabaseBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("connResult:connectionTestModel").isDisplayed()).isTrue());
    driver.findElementById("connResult:connTestForm:testConnectionBtn").click();
    saveScreenshot("connection_fail");
    webAssertThat(() -> assertThat(driver.findElementById("connResult:connTestForm:resultLog_content").getText()).contains("Error"));
  
    Navigation.toExternalDatabaseDetail(driver, "realdb");
    
    webAssertThat(() -> assertThat(driver.findElementById("connResult:connectionTestModel").isDisplayed()).isFalse());
    driver.findElementById("databaseConfigurationForm:testDatabaseBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("connResult:connectionTestModel").isDisplayed()).isTrue());
    driver.findElementById("connResult:connTestForm:testConnectionBtn").click();
    saveScreenshot("connection_ok");
    webAssertThat(() -> assertThat(driver.findElementById("connResult:connTestForm:resultLog_content").getText()).contains("Successfully connected to database"));
  }
  
  @Test
  void testSaveAndResetChanges()
  {
    navigateToDatabaseDetail();
    
    setConfiguration("url", "org.postgresql.Driver", "testUser", "13");
    driver.navigate().refresh();
    checkConfiguration("url", "org.postgresql.Driver", "testUser", "13");
    resetConfiguration();
    driver.navigate().refresh();
    checkConfiguration("jdbc:mysql://localhost:3306/test-db", "com.mysql.jdbc.Driver", "user", "5");
  }

  private void setConfiguration(String url, String driverName, String username, String connections)
  {
    driver.findElementById("databaseConfigurationForm:url").clear();
    driver.findElementById("databaseConfigurationForm:url").sendKeys(url);
    
    driver.findElementById("databaseConfigurationForm:driver_input").clear();
    driver.findElementByXPath("//*[@id='databaseConfigurationForm:driver']/button").click();
    webAssertThat(() -> assertThat(driver.findElementById("databaseConfigurationForm:driver_panel").isDisplayed()).isTrue());
    driver.findElementByXPath("//*[@id='databaseConfigurationForm:driver_panel']//li[text()='" + driverName + "']").click();
    
    driver.findElementById("databaseConfigurationForm:userName").clear();
    driver.findElementById("databaseConfigurationForm:userName").sendKeys(username);

    driver.findElementById("databaseConfigurationForm:maxConnections_input").clear();
    driver.findElementById("databaseConfigurationForm:maxConnections_input").sendKeys(connections);
    
    saveScreenshot("set");
    
    driver.findElementById("databaseConfigurationForm:saveDatabaseConfig").click();
    webAssertThat(() -> assertThat(driver.findElementById("databaseConfigurationForm:databaseConfigMsg_container")
            .getText()).contains("Database configuration saved"));
    saveScreenshot("save");
  }
  
  private void checkConfiguration(String url, String driverName, String username, String connections)
  {
    saveScreenshot("check");
    webAssertThat(() -> assertThat(driver.findElementById("databaseConfigurationForm:url").getAttribute("value"))
            .isEqualTo(url));
    webAssertThat(() -> assertThat(driver.findElementById("databaseConfigurationForm:driver_input").getAttribute("value"))
            .isEqualTo(driverName));
    webAssertThat(() -> assertThat(driver.findElementById("databaseConfigurationForm:userName").getAttribute("value"))
            .isEqualTo(username));
    webAssertThat(() -> assertThat(driver.findElementById("databaseConfigurationForm:maxConnections_input").getAttribute("value"))
            .isEqualTo(connections));
  }
  
  private void resetConfiguration()
  {
    driver.findElementById("databaseConfigurationForm:resetConfig").click();
    webAssertThat(() -> assertThat(driver.findElementById("databaseConfigurationForm:resetDbConfirmDialog").isDisplayed()).isTrue());
    driver.findElementById("databaseConfigurationForm:resetDbConfirmYesBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("databaseConfigurationForm:databaseConfigMsg_container")
            .getText()).contains("Database configuration reset"));
  }
  
  @Test
  void testConnectionAndHistory()
  {
    runExternalDbQuery(driver);
    login();
    Navigation.toExternalDatabaseDetail(driver, "realdb");
    Table connTable = new Table(driver, By.id("databaseConnectionForm:databaseConnectionsTable"));
    Table historyTable = new Table(driver, By.id("databaseExecHistoryForm:databaseExecHistoryTable"));
    webAssertThat(() -> assertThat(connTable.getFirstColumnEntries()).isNotEmpty());
    webAssertThat(() -> assertThat(historyTable.getFirstColumnEntries()).isNotEmpty());
    saveScreenshot("not_empty");
  }
  
  private void navigateToDatabaseDetail()
  {
    login();
    Navigation.toExternalDatabaseDetail(driver, DATABASE_NAME);
    saveScreenshot("externaldatabase_testdb");
  }
}
