package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.util.EnvironmentSwitch;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestEnvironmentSwitch extends WebTestBase
{
  @Test
  void testEnvironmentCount()
  {
    login();
    Navigation.toExternalDatabases(driver);
    webAssertThat(() -> assertThat(EnvironmentSwitch.getAvailableEnvs(driver)).hasSize(2).contains("Default", "test"));
  }
  
  @Test
  void testEnvironmentSwitch()
  {
    login();
    Navigation.toExternalDatabases(driver);
    saveScreenshot("default");
    EnvironmentSwitch.switchToEnv(driver, "test");
    saveScreenshot("test");
    webAssertThat(() -> assertThat(EnvironmentSwitch.getEnv(driver)).isEqualTo("test"));
  }
  
  @Test
  void testEnvironmentSwitchAndHoldState()
  {
    login();
    Navigation.toExternalDatabases(driver);
    saveScreenshot("db_default");
    webAssertThat(() -> assertThat(EnvironmentSwitch.getEnv(driver)).isEqualTo("Default"));
    EnvironmentSwitch.switchToEnv(driver, "test");
    saveScreenshot("db_test");
    webAssertThat(() -> assertThat(EnvironmentSwitch.getEnv(driver)).isEqualTo("test"));
    
    Navigation.toRestClients(driver);
    saveScreenshot("rest_test");
    webAssertThat(() -> assertThat(EnvironmentSwitch.getEnv(driver)).isEqualTo("test"));
    EnvironmentSwitch.switchToEnv(driver, "Default");
    saveScreenshot("rest_default");
    webAssertThat(() -> assertThat(EnvironmentSwitch.getEnv(driver)).isEqualTo("Default"));
    
    Navigation.toWebservices(driver);
    saveScreenshot("web_default");
    webAssertThat(() -> assertThat(EnvironmentSwitch.getEnv(driver)).isEqualTo("Default"));
    EnvironmentSwitch.switchToEnv(driver, "test");
    saveScreenshot("web_test");
    webAssertThat(() -> assertThat(EnvironmentSwitch.getEnv(driver)).isEqualTo("test"));
  }
}
