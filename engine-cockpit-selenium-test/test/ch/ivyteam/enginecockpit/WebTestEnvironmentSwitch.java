package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.supplements.IvySelenide;

import ch.ivyteam.enginecockpit.util.EnvironmentSwitch;
import ch.ivyteam.enginecockpit.util.Navigation;

@IvySelenide
public class WebTestEnvironmentSwitch
{
  @BeforeEach
  void beforeEach()
  {
    login();
    Navigation.toExternalDatabases();
  }
  
  @Test
  void testEnvironmentCount()
  {
    assertThat(EnvironmentSwitch.getAvailableEnvs()).hasSize(2).contains("Default", "test");
  }
  
  @Test
  void testEnvironmentSwitchAndHoldState()
  {
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
