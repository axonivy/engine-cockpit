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
  
  private void navigateToDatabaseDetail(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toExternalDatabaseDetail(driver, DATABASE_NAME);
    saveScreenshot(driver, "externaldatabase_testdb");
  }
}
