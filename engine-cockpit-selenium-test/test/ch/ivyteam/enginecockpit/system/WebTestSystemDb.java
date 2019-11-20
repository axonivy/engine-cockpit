package ch.ivyteam.enginecockpit.system;

import static org.assertj.core.api.Assertions.assertThat;

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
  @Test
  void testSystemDb()
  {
    navigateToSystemDb();
    webAssertThat(() -> assertThat(driver.findElementByTagName("h1").getText()).contains("System Database"));
    assertDefaultValues(driver);
    
    //TODO: add connection and assert connection info
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
  
  public static void assertAdditionalProperties(RemoteWebDriver driver)
  {
    Table table = new Table(driver, By.id("systemDb:systemDbForm:additionalPropertiesTable"));
    webAssertThat(() -> assertThat(table.getFirstColumnEntriesForSpanClass("property_key")).isEmpty());
    
    driver.findElementById("systemDb:systemDbForm:newAdditionalPropertyBtn").click();
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
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:addAdditionalPropertyForm:keyMessage")
            .getText()).contains("Value is required"));
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:addAdditionalPropertyForm:valueMessage")
            .getText()).contains("Value is required"));
    
    driver.findElementById("systemDb:addAdditionalPropertyForm:key").sendKeys("test");
    driver.findElementById("systemDb:addAdditionalPropertyForm:saveProperty").click();
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:addAdditionalPropertyForm:keyMessage")
            .getText()).isBlank());
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:addAdditionalPropertyForm:valueMessage")
            .getText()).contains("Value is required"));
    
    driver.findElementById("systemDb:addAdditionalPropertyForm:value").sendKeys("testValue");
    driver.findElementById("systemDb:addAdditionalPropertyForm:saveProperty").click();
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:addAdditionalPropertyDialog").isDisplayed())
            .isFalse());
    webAssertThat(() -> assertThat(table.getFirstColumnEntriesForSpanClass("property_key")).containsOnly("test"));
    webAssertThat(() -> assertThat(table.getValueForEntry("test", 2)).isEqualTo("testValue"));
    
    table.clickButtonForEntry("test", "removeAdditionalProperty");
    webAssertThat(() -> assertThat(table.getFirstColumnEntriesForSpanClass("property_key")).isEmpty());
  }

  public static void assertDefaultValues(RemoteWebDriver driver)
  {
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:systemDbForm:connectionPanel").getText())
            .contains("Connection state unknown"));
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:systemDbForm:databaseType_label").getText())
            .isEqualTo("Hypersonic SQL Db"));
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:systemDbForm:databaseDriver_label").getText())
            .isEqualTo("HSQL Db Memory"));
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:systemDbForm:host").getAttribute("value"))
            .isEqualTo(""));
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:systemDbForm:port").getAttribute("value"))
            .isEqualTo(""));
    webAssertThat(() -> assertThat(driver.findElementByCssSelector("#systemDb\\:systemDbForm\\:defaultPortCheckbox .ui-chkbox-box").getAttribute("class"))
            .contains("ui-state-active"));
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:systemDbForm:databaseName").getAttribute("value"))
            .isEqualTo("AxonIvySystemDatabase"));
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:systemDbForm:username").getAttribute("value"))
            .isEqualTo(""));
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:systemDbForm:password").getAttribute("value"))
            .isEqualTo(""));
  }
  
  public static void assertDefaultPortSwitch(RemoteWebDriver driver)
  {
    PrimeUi primeUi = new PrimeUi(driver);
    SelectBooleanCheckbox defaultPort = primeUi.selectBooleanCheckbox(By.id("systemDb:systemDbForm:defaultPortCheckbox"));
    webAssertThat(() -> assertThat(defaultPort.isChecked()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:systemDbForm:port").isEnabled()).isFalse());
    
    defaultPort.removeChecked();
    webAssertThat(() -> assertThat(defaultPort.isChecked()).isFalse());
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:systemDbForm:port").isEnabled()).isTrue());
    
    defaultPort.setChecked();
    webAssertThat(() -> assertThat(defaultPort.isChecked()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:systemDbForm:port").isEnabled()).isFalse());
  }
  
  public static void assertDatabaseTypeSwitch(RemoteWebDriver driver)
  {
    PrimeUi primeUi = new PrimeUi(driver);
    SelectOneMenu dbType = primeUi.selectOne(By.id("systemDb:systemDbForm:databaseType"));
    SelectOneMenu dbDriver = primeUi.selectOne(By.id("systemDb:systemDbForm:databaseDriver"));
    webAssertThat(() -> assertThat(dbType.getSelectedItem()).isEqualTo("Hypersonic SQL Db"));
    webAssertThat(() -> assertThat(dbDriver.getSelectedItem()).isEqualTo("HSQL Db Memory"));
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:systemDbForm:databaseNameLabel")
            .getText()).isEqualTo("Database Name"));
    
    dbType.selectItemByLabel("Oracle");
    webAssertThat(() -> assertThat(dbType.getSelectedItem()).isEqualTo("Oracle"));
    webAssertThat(() -> assertThat(dbDriver.getSelectedItem()).isEqualTo("Oracle Thin"));
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:systemDbForm:databaseNameLabel")
            .getText()).isEqualTo("Oracle Service ID"));
    
    dbType.selectItemByLabel("MySQL");
    webAssertThat(() -> assertThat(dbType.getSelectedItem()).isEqualTo("MySQL"));
    webAssertThat(() -> assertThat(dbDriver.getSelectedItem()).isEqualTo("mySQL"));
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:systemDbForm:databaseNameLabel")
            .getText()).isEqualTo("Database Name"));
  }
  
  private void navigateToSystemDb()
  {
    login();
    Navigation.toSystemDb(driver);
    saveScreenshot("systemdb");
  }
}
