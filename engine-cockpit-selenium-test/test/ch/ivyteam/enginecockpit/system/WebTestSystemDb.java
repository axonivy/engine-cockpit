package ch.ivyteam.enginecockpit.system;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.axonivy.ivy.supplements.primeui.tester.PrimeUi;
import com.axonivy.ivy.supplements.primeui.tester.PrimeUi.SelectBooleanCheckbox;
import com.axonivy.ivy.supplements.primeui.tester.PrimeUi.SelectOneMenu;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

public class WebTestSystemDb extends WebTestBase
{
  private static final String OLD_DB_NAME = "engine_cockpit_old_db_version44";
  
  @AfterEach
  void cleanup()
  {
    resetConfig(driver);
  }
  
  @Test
  void testSystemDb()
  {
    navigateToSystemDb();
    webAssertThat(() -> assertThat(driver.findElementByTagName("h1").getText()).contains("System Database"));
    assertDefaultValues(driver);
    
    //TODO: add connection and assert connection info
  }
  
  @Test
  void testConvertOldDb()
  {
    createOldDb(driver);
    navigateToSystemDb();
    insertDbConnection(driver, "MySQL", "mySQL", "zugtstdbsmys", OLD_DB_NAME, "admin", "nimda");
    
    driver.findElementById("systemDb:systemDbForm:checkConnectionButton").click();
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:systemDbForm:connectionPanel").getText())
            .contains("Database too old", "Convert system database."));
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:systemDbForm:migrateDatabaseButton")
            .isEnabled()).isTrue());
    
    driver.findElementById("systemDb:systemDbForm:migrateDatabaseButton").click();
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:convertDatabaseDialog").isDisplayed()).isTrue());
    
    driver.findElementById("systemDb:convertDatabaseForm:confirmConvertButton").click();
    webAssertThat(() -> assertThat(driver.findElementByCssSelector(".fa.fa-circle-o-notch.fa-spin")
            .isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:convertDatabaseForm:conversionResult").getText())
            .contains("Success")); //Fix this
  }
  
  @Test
  void testDefaultPortSwitch()
  {
    navigateToSystemDb();
    assertDefaultPortSwitch(driver);
  }
  
  @Test
  void testDatabaseDropdownSwitch()
  {
    navigateToSystemDb();
    assertDatabaseTypeSwitch(driver);
  }
  
  @Test
  void testAdditionalProperties()
  {
    navigateToSystemDb();
    assertAdditionalProperties(driver);
  }
  
  private static void insertDbConnection(RemoteWebDriver driver, String database, String driverName, 
          String host, String databaseName, String user, String password)
  {
    PrimeUi primeUi = new PrimeUi(driver);
    SelectOneMenu dbType = primeUi.selectOne(By.id("systemDb:systemDbForm:databaseType"));
    SelectOneMenu dbDriver = primeUi.selectOne(By.id("systemDb:systemDbForm:databaseDriver"));
    dbType.selectItemByLabel(database);
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:systemDbForm:connectionPanel").getText())
            .contains("Connection state unknown"));
    dbDriver.selectItemByLabel(driverName);
    driver.findElementByCssSelector(".sysdb-dynamic-form-host").clear();
    driver.findElementByCssSelector(".sysdb-dynamic-form-host").sendKeys(host);
    driver.findElementByCssSelector(".sysdb-dynamic-form-databasename").clear();
    driver.findElementByCssSelector(".sysdb-dynamic-form-databasename").sendKeys(databaseName);
    driver.findElementByCssSelector(".sysdb-dynamic-form-user").clear();
    driver.findElementByCssSelector(".sysdb-dynamic-form-user").sendKeys(user);
    driver.findElementByCssSelector(".sysdb-dynamic-form-password").clear();
    driver.findElementByCssSelector(".sysdb-dynamic-form-password").sendKeys(password);
    saveScreenshot(driver, "insert_db_connection");
  }
  
  public static void assertAdditionalProperties(RemoteWebDriver driver)
  {
    Table table = new Table(driver, By.id("systemDb:systemDbForm:additionalPropertiesTable"));
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:systemDbForm:additionalPropertiesTable").getText())
            .contains("No records found."));
    
    driver.findElementById("systemDb:systemDbForm:newAdditionalPropertyBtn").click();
    saveScreenshot(driver, "model");
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:addAdditionalPropertyDialog").isDisplayed())
            .isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:addAdditionalPropertyForm:key")
            .getAttribute("value")).isBlank());
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:addAdditionalPropertyForm:keyMessage")
            .getText()).isBlank());
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:addAdditionalPropertyForm:value")
            .getAttribute("value")).isBlank());
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:addAdditionalPropertyForm:valueMessage")
            .getText()).isBlank());
    
    driver.findElementById("systemDb:addAdditionalPropertyForm:saveProperty").click();
    saveScreenshot(driver, "save_invalid");
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:addAdditionalPropertyForm:keyMessage")
            .getText()).contains("Value is required"));
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:addAdditionalPropertyForm:valueMessage")
            .getText()).contains("Value is required"));
    
    driver.findElementById("systemDb:addAdditionalPropertyForm:key").sendKeys("test");
    driver.findElementById("systemDb:addAdditionalPropertyForm:saveProperty").click();
    saveScreenshot(driver, "save_password_invalid");
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:addAdditionalPropertyForm:keyMessage")
            .getText()).isBlank());
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:addAdditionalPropertyForm:valueMessage")
            .getText()).contains("Value is required"));
    
    driver.findElementById("systemDb:addAdditionalPropertyForm:value").sendKeys("testValue");
    driver.findElementById("systemDb:addAdditionalPropertyForm:saveProperty").click();
    saveScreenshot(driver, "save");
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:addAdditionalPropertyDialog").isDisplayed())
            .isFalse());
    webAssertThat(() -> assertThat(table.getFirstColumnEntriesForSpanClass("property_key")).containsOnly("test"));
    webAssertThat(() -> assertThat(table.getValueForEntry("test", 2)).isEqualTo("testValue"));
    
    table.clickButtonForEntry("test", "removeAdditionalProperty");
    saveScreenshot(driver, "remove");
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:systemDbForm:additionalPropertiesTable").getText())
            .contains("No records found."));
  }

  public static void assertDefaultValues(RemoteWebDriver driver)
  {
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:systemDbForm:connectionPanel").getText())
            .contains("Connection state unknown"));
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:systemDbForm:databaseType_label").getText())
            .isEqualTo("Hypersonic SQL Db"));
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:systemDbForm:databaseDriver_label").getText())
            .isEqualTo("HSQL Db Memory"));
    webAssertThat(() -> assertThat(driver.findElementByCssSelector(".sysdb-dynamic-form-host").getAttribute("value"))
            .isEqualTo(""));
    webAssertThat(() -> assertThat(driver.findElementByCssSelector(".sysdb-dynamic-form-port input").getAttribute("value"))
            .isEqualTo(""));
    webAssertThat(() -> assertThat(driver.findElementByCssSelector(".sysdb-dynamic-form-port-default-checkbox").getAttribute("class"))
            .contains("ui-state-active"));
    webAssertThat(() -> assertThat(driver.findElementByCssSelector(".sysdb-dynamic-form-databasename").getAttribute("value"))
            .isEqualTo("AxonIvySystemDatabase"));
    webAssertThat(() -> assertThat(driver.findElementByCssSelector(".sysdb-dynamic-form-user").getAttribute("value"))
            .isEqualTo(""));
    webAssertThat(() -> assertThat(driver.findElementByCssSelector(".sysdb-dynamic-form-password").getAttribute("value"))
            .isEqualTo(""));
  }
  
  public static void assertDefaultPortSwitch(RemoteWebDriver driver)
  {
    PrimeUi primeUi = new PrimeUi(driver);
    SelectOneMenu dbType = primeUi.selectOne(By.id("systemDb:systemDbForm:databaseType"));
    dbType.selectItemByLabel("MySQL");
    webAssertThat(() -> assertThat(driver.findElementByCssSelector(".sysdb-dynamic-form-port input").isDisplayed()).isTrue());
    saveScreenshot(driver, "mysql");
    
    SelectBooleanCheckbox defaultPort = primeUi.selectBooleanCheckbox(By.cssSelector(".sysdb-dynamic-form-port-default-checkbox"));
    webAssertThat(() -> assertThat(defaultPort.isChecked()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementByCssSelector(".sysdb-dynamic-form-port input").isEnabled()).isFalse());
    
    defaultPort.removeChecked();
    saveScreenshot(driver, "disable_default");
    webAssertThat(() -> assertThat(defaultPort.isChecked()).isFalse());
    webAssertThat(() -> assertThat(driver.findElementByCssSelector(".sysdb-dynamic-form-port input").isEnabled()).isTrue());
    
    defaultPort.setChecked();
    saveScreenshot(driver, "enable_default");
    webAssertThat(() -> assertThat(defaultPort.isChecked()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementByCssSelector(".sysdb-dynamic-form-port input").isEnabled()).isFalse());
  }
  
  public static void assertDatabaseTypeSwitch(RemoteWebDriver driver)
  {
    PrimeUi primeUi = new PrimeUi(driver);
    SelectOneMenu dbType = primeUi.selectOne(By.id("systemDb:systemDbForm:databaseType"));
    SelectOneMenu dbDriver = primeUi.selectOne(By.id("systemDb:systemDbForm:databaseDriver"));
    webAssertThat(() -> assertThat(dbType.getSelectedItem()).isEqualTo("Hypersonic SQL Db"));
    webAssertThat(() -> assertThat(dbDriver.getSelectedItem()).isEqualTo("HSQL Db Memory"));
    webAssertThat(() -> assertThat(elementNotAvailable(driver, By.cssSelector(".sysdb-dynamic-form-databasename"))).isTrue());
    
    dbType.selectItemByLabel("Oracle");
    saveScreenshot(driver, "oracle");
    webAssertThat(() -> assertThat(dbType.getSelectedItem()).isEqualTo("Oracle"));
    webAssertThat(() -> assertThat(dbDriver.getSelectedItem()).isEqualTo("Oracle Thin"));
    webAssertThat(() -> assertThat(driver.findElementByCssSelector(".sysdb-dynamic-form-oracleservicename")
            .getText()).isEqualTo(""));
    
    dbType.selectItemByLabel("MySQL");
    saveScreenshot(driver, "mysql");
    webAssertThat(() -> assertThat(dbType.getSelectedItem()).isEqualTo("MySQL"));
    webAssertThat(() -> assertThat(dbDriver.getSelectedItem()).isEqualTo("mySQL"));
    webAssertThat(() -> assertThat(driver.findElementByCssSelector(".sysdb-dynamic-form-host")
            .getText()).isEqualTo("localhost"));
  }
  
  private void navigateToSystemDb()
  {
    login();
    Navigation.toSystemDb(driver);
    saveScreenshot("systemdb");
  }
}
