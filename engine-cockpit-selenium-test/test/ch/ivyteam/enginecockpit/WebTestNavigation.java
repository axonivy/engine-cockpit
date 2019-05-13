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
    Navigation.toEmail(driver);
    assertViewIsEmail(driver);
    Navigation.toExternalDatabases(driver);
    assertViewIsExternalDatabases(driver);
    Navigation.toRestClients(driver);
    assertViewIsRestClients(driver);
    Navigation.toWebservices(driver);
    assertViewIsWebservices(driver);
    Navigation.toSystemConfig(driver);
    assertViewIsSystemConfig(driver);
    Navigation.toMonitor(driver);
    assertViewIsMonitor(driver);
    Navigation.toLogs(driver);
    assertViewIsLogs(driver);
    Navigation.toDashboard(driver);
    assertViewIsDashboard(driver);
  }

  private void assertViewIsDashboard(FirefoxDriver driver)
  {
    saveScreenshot(driver, "dashboard");
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).endsWith("dashboard.xhtml"));
    await().untilAsserted(() -> assertThat(driver.getTitle()).isEqualTo("Engine Cockpit"));
    await().untilAsserted(() -> assertThat(driver.findElementById("menuform:sr_dashboard").getAttribute("class")).contains("active-menuitem"));
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
  
  private void assertViewIsEmail(FirefoxDriver driver)
  {
    saveScreenshot(driver, "email");
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).endsWith("email.xhtml"));
    await().untilAsserted(() -> assertThat(driver.getTitle()).isEqualTo("Email"));
    await().untilAsserted(() -> assertThat(driver.findElementById("menuform:sr_email").getAttribute("class")).contains("active-menuitem"));
  }
  
  private void assertViewIsExternalDatabases(FirefoxDriver driver)
  {
    saveScreenshot(driver, "externaldatabases");
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).endsWith("externaldatabases.xhtml"));
    await().untilAsserted(() -> assertThat(driver.getTitle()).isEqualTo("External Databases"));
    await().untilAsserted(() -> assertThat(driver.findElementById("menuform:sr_database").getAttribute("class")).contains("active-menuitem"));
  }
  
  private void assertViewIsRestClients(FirefoxDriver driver)
  {
    saveScreenshot(driver, "restclients");
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).endsWith("restclients.xhtml"));
    await().untilAsserted(() -> assertThat(driver.getTitle()).isEqualTo("Rest Clients"));
    await().untilAsserted(() -> assertThat(driver.findElementById("menuform:sr_rest_client").getAttribute("class")).contains("active-menuitem"));
  }
  
  private void assertViewIsWebservices(FirefoxDriver driver)
  {
    saveScreenshot(driver, "webservices");
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).endsWith("webservices.xhtml"));
    await().untilAsserted(() -> assertThat(driver.getTitle()).isEqualTo("Web Services"));
    await().untilAsserted(() -> assertThat(driver.findElementById("menuform:sr_web_service").getAttribute("class")).contains("active-menuitem"));
  }

  private void assertViewIsSystemConfig(FirefoxDriver driver)
  {
    saveScreenshot(driver, "system_config");
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).endsWith("systemconfig.xhtml"));
    await().untilAsserted(() -> assertThat(driver.getTitle()).isEqualTo("System Configuration"));
    await().untilAsserted(() -> assertThat(driver.findElementById("menuform:sr_system_config").getAttribute("class")).contains("active-menuitem"));
  }
  
  private void assertViewIsMonitor(FirefoxDriver driver)
  {
    saveScreenshot(driver, "monitor");
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).endsWith("monitor.xhtml"));
    await().untilAsserted(() -> assertThat(driver.getTitle()).isEqualTo("Monitor"));
    await().untilAsserted(() -> assertThat(driver.findElementById("menuform:sr_monitor").getAttribute("class")).contains("active-menuitem"));
  }
  
  private void assertViewIsLogs(FirefoxDriver driver)
  {
    saveScreenshot(driver, "logs");
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).contains("logs.xhtml"));
    await().untilAsserted(() -> assertThat(driver.getTitle()).isEqualTo("Logs"));
    await().untilAsserted(() -> assertThat(driver.findElementById("menuform:sr_logs").getAttribute("class")).contains("active-menuitem"));
  }
}
