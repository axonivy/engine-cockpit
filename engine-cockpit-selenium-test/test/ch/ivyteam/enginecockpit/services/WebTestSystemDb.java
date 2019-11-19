package ch.ivyteam.enginecockpit.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.supplements.primeui.tester.PrimeUi;
import com.axonivy.ivy.supplements.primeui.tester.PrimeUi.SelectBooleanCheckbox;
import com.axonivy.ivy.supplements.primeui.tester.PrimeUi.SelectOneMenu;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestSystemDb extends WebTestBase
{
  @Test
  void testSystemDb()
  {
    navigateToSystemDb();
    webAssertThat(() -> assertThat(driver.findElementByTagName("h1").getText()).contains("System Database"));
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
  
  @Test
  void testDefaultPortSwitch()
  {
    navigateToSystemDb();
    PrimeUi primeUi = new PrimeUi(driver);
    SelectBooleanCheckbox defaultPort = primeUi.selectBooleanCheckbox(By.id("systemDb:systemDbForm:defaultPortCheckbox"));
    webAssertThat(() -> assertThat(defaultPort.isChecked()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:systemDbForm:port").isEnabled()).isFalse());
    
    defaultPort.removeChecked();
    saveScreenshot("remove_default");
    webAssertThat(() -> assertThat(defaultPort.isChecked()).isFalse());
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:systemDbForm:port").isEnabled()).isTrue());
    
    defaultPort.setChecked();
    saveScreenshot("set_default");
    webAssertThat(() -> assertThat(defaultPort.isChecked()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("systemDb:systemDbForm:port").isEnabled()).isFalse());
  }
  
  @Test
  void testDatabaseDropdownSwitch()
  {
    navigateToSystemDb();
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
