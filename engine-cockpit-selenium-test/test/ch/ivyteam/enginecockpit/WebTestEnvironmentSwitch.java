package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.util.EnvironmentSwitch;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestEnvironmentSwitch extends WebTestBase
{
  @Test
  void testEnvironmentCount(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toExternalDatabases(driver);
    webAssertThat(() -> assertThat(EnvironmentSwitch.getAvailableEnvs(driver)).hasSize(2).contains("Default", "test"));
  }
  
  @Test
  void testEnvironmentSwitch(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toExternalDatabases(driver);
    saveScreenshot(driver, "default");
    EnvironmentSwitch.switchToEnv(driver, "test");
    saveScreenshot(driver, "test");
    webAssertThat(() -> assertThat(EnvironmentSwitch.getEnv(driver)).isEqualTo("test"));
  }
  
  @Test
  void testEnvironmentSwitchAndHoldState(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toExternalDatabases(driver);
    saveScreenshot(driver, "db_default");
    webAssertThat(() -> assertThat(EnvironmentSwitch.getEnv(driver)).isEqualTo("Default"));
    EnvironmentSwitch.switchToEnv(driver, "test");
    saveScreenshot(driver, "db_test");
    webAssertThat(() -> assertThat(EnvironmentSwitch.getEnv(driver)).isEqualTo("test"));
    
    Navigation.toRestClients(driver);
    saveScreenshot(driver, "rest_test");
    webAssertThat(() -> assertThat(EnvironmentSwitch.getEnv(driver)).isEqualTo("test"));
    EnvironmentSwitch.switchToEnv(driver, "Default");
    saveScreenshot(driver, "rest_default");
    webAssertThat(() -> assertThat(EnvironmentSwitch.getEnv(driver)).isEqualTo("Default"));
    
    Navigation.toWebservices(driver);
    saveScreenshot(driver, "web_default");
    webAssertThat(() -> assertThat(EnvironmentSwitch.getEnv(driver)).isEqualTo("Default"));
    EnvironmentSwitch.switchToEnv(driver, "test");
    saveScreenshot(driver, "web_test");
    webAssertThat(() -> assertThat(EnvironmentSwitch.getEnv(driver)).isEqualTo("test"));
  }
}
