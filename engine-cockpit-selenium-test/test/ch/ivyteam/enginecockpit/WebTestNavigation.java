package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestNavigation extends WebTestBase
{
  @Test
  void testNavigation(FirefoxDriver driver)
  {
    login(driver);
    assertViewIsDashboard(driver);
    Navigation.toApplications(driver);
    assertViewIsApplications(driver);
    Navigation.toSecuritySystem(driver);
    assertViewIsSecuritySystem(driver);
    Navigation.toUsers(driver);
    assertViewIsUsers(driver);
    Navigation.toRoles(driver);
    assertViewIsRoles(driver);
    Navigation.toAdvancedConfig(driver);
    assertViewIsAdvancedConfig(driver);
    Navigation.toMonitor(driver);
    assertViewIsMonitor(driver);
    Navigation.toDashboard(driver);
    assertViewIsDashboard(driver);
  }

  private void assertViewIsDashboard(FirefoxDriver driver)
  {
    saveScreenshot(driver, "dashboard");
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).endsWith("dashboard.xhtml"));
    await().untilAsserted(() -> assertThat(driver.getTitle()).isEqualTo("Engine Cockpit"));
    //TODO: after first login there is no navigation hightlighting
    //await().untilAsserted(() -> assertThat(driver.findElementById("menuform:sr_dashboard").getAttribute("class")).contains("active-menuitem"));
  }
  
  private void assertViewIsApplications(FirefoxDriver driver)
  {
    saveScreenshot(driver, "applications");
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).endsWith("applications.xhtml"));
    await().untilAsserted(() -> assertThat(driver.getTitle()).isEqualTo("Applications"));
    await().untilAsserted(() -> assertThat(driver.findElementById("menuform:sr_applications").getAttribute("class")).contains("active-menuitem"));
  }
  
  private void assertViewIsSecuritySystem(FirefoxDriver driver)
  {
    saveScreenshot(driver, "security_system");
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).endsWith("securitysystem.xhtml"));
    await().untilAsserted(() -> assertThat(driver.getTitle()).isEqualTo("Security System"));
    await().untilAsserted(() -> assertThat(driver.findElementById("menuform:sr_security_system").getAttribute("class")).contains("active-menuitem"));
  }
  
  private void assertViewIsUsers(FirefoxDriver driver)
  {
    saveScreenshot(driver, "users");
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).endsWith("users.xhtml"));
    await().untilAsserted(() -> assertThat(driver.getTitle()).isEqualTo("Users"));
    await().untilAsserted(() -> assertThat(driver.findElementById("menuform:sr_users").getAttribute("class")).contains("active-menuitem"));
  }
  
  private void assertViewIsRoles(FirefoxDriver driver)
  {
    saveScreenshot(driver, "roles");
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).endsWith("roles.xhtml"));
    await().untilAsserted(() -> assertThat(driver.getTitle()).isEqualTo("Roles"));
    await().untilAsserted(() -> assertThat(driver.findElementById("menuform:sr_roles").getAttribute("class")).contains("active-menuitem"));
  }
  
  private void assertViewIsAdvancedConfig(FirefoxDriver driver)
  {
    saveScreenshot(driver, "advanced_config");
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).endsWith("advancedconfig.xhtml"));
    await().untilAsserted(() -> assertThat(driver.getTitle()).isEqualTo("Advanced Config"));
    await().untilAsserted(() -> assertThat(driver.findElementById("menuform:sr_advanced_config").getAttribute("class")).contains("active-menuitem"));
  }
  
  private void assertViewIsMonitor(FirefoxDriver driver)
  {
    saveScreenshot(driver, "monitor");
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).endsWith("monitor.xhtml"));
    await().untilAsserted(() -> assertThat(driver.getTitle()).isEqualTo("Monitor"));
    await().untilAsserted(() -> assertThat(driver.findElementById("menuform:sr_monitor").getAttribute("class")).contains("active-menuitem"));
  }
}
