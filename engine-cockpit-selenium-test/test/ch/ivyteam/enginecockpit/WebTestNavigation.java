package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestNavigation extends WebTestBase
{
  @Test
  void testNavigation()
  {
    login();
    assertView("dashboard.xhtml", "menuform:sr_dashboard");
    Navigation.toApplications(driver);
    assertView("applications.xhtml", "menuform:sr_applications");
    Navigation.toSecuritySystem(driver);
    assertView("securitysystem.xhtml", "menuform:sr_security_system");
    Navigation.toUsers(driver);
    assertView("users.xhtml", "menuform:sr_users");
    Navigation.toRoles(driver);
    assertView("roles.xhtml", "menuform:sr_roles");
    Navigation.toVariables(driver);
    assertView("global-variables.xhtml", "menuform:sr_variables");
    Navigation.toBusinessCalendar(driver);
    assertView("businesscalendar.xhtml", "menuform:sr_business_calendar");
    Navigation.toSearchEngine(driver);
    assertView("searchengine.xhtml", "menuform:sr_searchengine");
    Navigation.toEmail(driver);
    assertView("email.xhtml", "menuform:sr_email");
    Navigation.toExternalDatabases(driver);
    assertView("externaldatabases.xhtml", "menuform:sr_database");
    Navigation.toWebservices(driver);
    assertView("webservices.xhtml", "menuform:sr_web_service");
    Navigation.toRestClients(driver);
    assertView("restclients.xhtml", "menuform:sr_rest_client");
    Navigation.toSystemDb(driver);
    assertView("systemdb.xhtml", "menuform:sr_systemdb");
    Navigation.toAdmins(driver);
    assertView("admins.xhtml", "menuform:sr_admins");
    Navigation.toSystemConfig(driver);
    assertView("systemconfig.xhtml", "menuform:sr_system_config");
    Navigation.toResourcesMonitor(driver);
    assertView("monitor.xhtml", "menuform:sr_resources_monitor");
    Navigation.toLogs(driver);
    assertView("logs.xhtml", "menuform:sr_logs");
    Navigation.toDashboard(driver);
    assertView("dashboard.xhtml", "menuform:sr_dashboard");
  }
  
  private void assertView(String page, String id)
  {
    saveScreenshot(page);
    webAssertThat(() -> assertThat(driver.getCurrentUrl()).contains(page));
    webAssertThat(() -> assertThat(driver.findElementById(id).getAttribute("class")).contains("active-menuitem"));
  }
}
