package ch.ivyteam.enginecockpit.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestExternalDatabaseDetail extends WebTestBase
{
  private static final String DATABASE_NAME = "test-db";
  
  @Test
  void testExternalDatabaseDetailOpen(FirefoxDriver driver)
  {
    navigateToDatabaseDetail(driver);
    webAssertThat(() -> 
            assertThat(driver.getCurrentUrl()).endsWith("externaldatabasedetail.xhtml?databaseName=" + DATABASE_NAME));
    webAssertThat(() -> assertThat(driver.findElementsByClassName("ui-panel")).hasSize(2));
    webAssertThat(() -> assertThat(driver.findElementById("databaseConfigurationForm:name").getText()).isEqualTo(DATABASE_NAME));
  }
  
  @Test
  void testOpenExternalDatabaseHelp(FirefoxDriver driver)
  {
    navigateToDatabaseDetail(driver);
    
    driver.findElementById("databaseConfigurationForm:helpDatabaseBtn").click();
    saveScreenshot(driver, "help_modal");
    webAssertThat(() -> assertThat(driver.findElementById("helpExternalDatabaseDialog:helpServicesModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementByClassName("code-block").getText()).contains(DATABASE_NAME));
  }
  
  @Test
  void testExternalDatabaseTestConnection(FirefoxDriver driver)
  {
    navigateToDatabaseDetail(driver);
    
    webAssertThat(() -> assertThat(driver.findElementById("databaseConfigurationForm:databaseConfigMsg_container").isDisplayed()).isFalse());
    driver.findElementById("databaseConfigurationForm:testDatabaseBtn").click();
    saveScreenshot(driver, "connection_fail");
    webAssertThat(() -> assertThat(driver.findElementById("databaseConfigurationForm:databaseConfigMsg_container").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("databaseConfigurationForm:databaseConfigMsg_container").getText()).contains("Error"));
  
    Navigation.toExternalDatabaseDetail(driver, "realdb");
    
    webAssertThat(() -> assertThat(driver.findElementById("databaseConfigurationForm:databaseConfigMsg_container").isDisplayed()).isFalse());
    driver.findElementById("databaseConfigurationForm:testDatabaseBtn").click();
    saveScreenshot(driver, "connection_ok");
    webAssertThat(() -> assertThat(driver.findElementById("databaseConfigurationForm:databaseConfigMsg_container").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("databaseConfigurationForm:databaseConfigMsg_container").getText()).contains("Successful connected to database"));
  }
  
  @Test
  void testSaveAndResetChanges(FirefoxDriver driver)
  {
    navigateToDatabaseDetail(driver);
    
    setConfiguration(driver, "url", "org.postgresql.Driver", "testUser", "13");
    driver.navigate().refresh();
    checkConfiguration(driver, "url", "org.postgresql.Driver", "testUser", "13");
    resetConfiguration(driver);
    driver.navigate().refresh();
    checkConfiguration(driver, "jdbc:mysql://localhost:3306/test-db", "com.mysql.jdbc.Driver", "user", "5");
  }

  private void setConfiguration(FirefoxDriver driver, String url, String driverName, String username, String connections)
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
    
    saveScreenshot(driver, "set");
    
    driver.findElementById("databaseConfigurationForm:saveDatabaseConfig").click();
    webAssertThat(() -> assertThat(driver.findElementById("databaseConfigurationForm:databaseConfigMsg_container")
            .getText()).contains("Database configuration saved"));
    saveScreenshot(driver, "save");
  }
  
  private void checkConfiguration(FirefoxDriver driver, String url, String driverName, String username, String connections)
  {
    saveScreenshot(driver, "check");
    webAssertThat(() -> assertThat(driver.findElementById("databaseConfigurationForm:url").getAttribute("value"))
            .isEqualTo(url));
    webAssertThat(() -> assertThat(driver.findElementById("databaseConfigurationForm:driver_input").getAttribute("value"))
            .isEqualTo(driverName));
    webAssertThat(() -> assertThat(driver.findElementById("databaseConfigurationForm:userName").getAttribute("value"))
            .isEqualTo(username));
    webAssertThat(() -> assertThat(driver.findElementById("databaseConfigurationForm:maxConnections_input").getAttribute("value"))
            .isEqualTo(connections));
  }
  
  private void resetConfiguration(FirefoxDriver driver)
  {
    driver.findElementById("databaseConfigurationForm:resetConfig").click();
    webAssertThat(() -> assertThat(driver.findElementById("databaseConfigurationForm:resetDbConfirmDialog").isDisplayed()).isTrue());
    driver.findElementById("databaseConfigurationForm:resetDbConfirmYesBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("databaseConfigurationForm:databaseConfigMsg_container")
            .getText()).contains("Database configuration reseted"));
  }
  
  private void navigateToDatabaseDetail(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toExternalDatabaseDetail(driver, DATABASE_NAME);
    saveScreenshot(driver, "externaldatabase_testdb");
  }
}
