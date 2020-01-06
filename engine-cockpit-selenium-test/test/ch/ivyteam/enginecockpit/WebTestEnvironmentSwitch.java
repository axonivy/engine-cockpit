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
    Navigation.toExternalDatabases();
    assertThat(EnvironmentSwitch.getAvailableEnvs()).hasSize(2).contains("Default", "test");
  }
  
  @Test
  void testEnvironmentSwitchAndHoldState()
  {
    login();
    Navigation.toExternalDatabases();
    assertThat(EnvironmentSwitch.getEnv()).isEqualTo("Default");
    EnvironmentSwitch.switchToEnv("test");
    assertThat(EnvironmentSwitch.getEnv()).isEqualTo("test");
    
    Navigation.toRestClients();
    assertThat(EnvironmentSwitch.getEnv()).isEqualTo("test");
    EnvironmentSwitch.switchToEnv("Default");
    assertThat(EnvironmentSwitch.getEnv()).isEqualTo("Default");
    
    Navigation.toWebservices();
    assertThat(EnvironmentSwitch.getEnv()).isEqualTo("Default");
  }
}
