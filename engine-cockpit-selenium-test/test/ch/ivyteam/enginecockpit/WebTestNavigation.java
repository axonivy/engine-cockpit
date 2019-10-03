package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestNavigation extends WebTestBase
{
  @Test
  void testNavigation(FirefoxDriver driver)
  {
    login(driver);
    assertView(driver, "dashboard.xhtml", "menuform:sr_dashboard");
    Navigation.toApplications(driver);
    assertView(driver, "applications.xhtml", "menuform:sr_applications");
    Navigation.toSecuritySystem(driver);
    assertView(driver, "securitysystem.xhtml", "menuform:sr_security_system");
    Navigation.toUsers(driver);
    assertView(driver, "users.xhtml", "menuform:sr_users");
    Navigation.toRoles(driver);
    assertView(driver, "roles.xhtml", "menuform:sr_roles");
    Navigation.toVariables(driver);
    assertView(driver, "global-variables.xhtml", "menuform:sr_variables");
    Navigation.toBusinessCalendar(driver);
    assertView(driver, "businesscalendar.xhtml", "menuform:sr_business_calendar");
    Navigation.toSearchEngine(driver);
    assertView(driver, "searchengine.xhtml", "menuform:sr_searchengine");
    Navigation.toEmail(driver);
    assertView(driver, "email.xhtml", "menuform:sr_email");
    Navigation.toExternalDatabases(driver);
    assertView(driver, "externaldatabases.xhtml", "menuform:sr_database");
    Navigation.toWebservices(driver);
    assertView(driver, "webservices.xhtml", "menuform:sr_web_service");
    Navigation.toRestClients(driver);
    assertView(driver, "restclients.xhtml", "menuform:sr_rest_client");
    Navigation.toSystemConfig(driver);
    assertView(driver, "systemconfig.xhtml", "menuform:sr_system_config");
    Navigation.toMonitor(driver);
    assertView(driver, "monitor.xhtml", "menuform:sr_monitor");
    Navigation.toLogs(driver);
    assertView(driver, "logs.xhtml", "menuform:sr_logs");
    Navigation.toDashboard(driver);
    assertView(driver, "dashboard.xhtml", "menuform:sr_dashboard");
  }
  
  private void assertView(FirefoxDriver driver, String page, String id)
  {
    saveScreenshot(driver, page);
    webAssertThat(() -> assertThat(driver.getCurrentUrl()).contains(page));
    webAssertThat(() -> assertThat(driver.findElementById(id).getAttribute("class")).contains("active-menuitem"));
  }
}
