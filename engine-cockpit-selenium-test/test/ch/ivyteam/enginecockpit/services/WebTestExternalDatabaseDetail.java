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
    webAssertThat(() -> assertThat(driver.getTitle()).isEqualTo("External Database Detail"));
    webAssertThat(() -> assertThat(driver.findElementsByClassName("ui-panel")).hasSize(3));
    webAssertThat(() -> assertThat(driver.findElementById("databaseConfigurationForm:name").getText()).isEqualTo(DATABASE_NAME));
  }
  
  private void navigateToDatabaseDetail(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toExternalDatabaseDetail(driver, DATABASE_NAME);
    saveScreenshot(driver, "externaldatabase_testdb");
  }
}
