package ch.ivyteam.enginecockpit.util;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlEndsWith;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.viewUrl;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.Selenide;

public class Navigation
{
  private static final String DASHBOARD_MENU = "#menuform\\:sr_dashboard";
  private static final String APPLICATIONS_MENU = "#menuform\\:sr_applications";
  private static final String SECURITY_MENU = "#menuform\\:sr_security";
  private static final String SECURITY_SYSTEM_MENU = "#menuform\\:sr_security_system";
  private static final String SECURITY_USER_MENU = "#menuform\\:sr_users";
  private static final String SECURITY_ROLES_MENU = "#menuform\\:sr_roles";
  private static final String CONFIGURATION_MENU = "#menuform\\:sr_configuration";
  private static final String VARIABLES_MENU = "#menuform\\:sr_variables";
  private static final String BUSINESS_CALENDAR_MENU = "#menuform\\:sr_business_calendar";
  private static final String SERVICES_MENU = "#menuform\\:sr_services";
  private static final String SERVICES_SEARCH_ENGINE = "#menuform\\:sr_searchengine";
  private static final String SERVICES_EMAIL_MENU = "#menuform\\:sr_email";
  private static final String SERVICES_DATABASES_MENU = "#menuform\\:sr_database";
  private static final String SERVICES_RESTCLIENTS_MENU = "#menuform\\:sr_rest_client";
  private static final String SERVICES_WEBSERVICES_MENU = "#menuform\\:sr_web_service";
  private static final String SYSTEM_MENU = "#menuform\\:sr_system";
  private static final String SYSTEM_ADMINS = "#menuform\\:sr_admins";
  private static final String SYSTEM_SYSTEMDB_MENU = "#menuform\\:sr_systemdb";
  private static final String SYSTEM_WEB_SERVER_MENU = "#menuform\\:sr_web_server";
  private static final String SYSTEM_CONFIG_MENU = "#menuform\\:sr_system_config";
  private static final String SYSTEM_CLUSTER = "#menuform\\:sr_cluster";
  private static final String SYSTEM_EDITOR_MENU = "#menuform\\:sr_editor";
  private static final String MONITOR_MENU = "#menuform\\:sr_monitor";
  private static final String MONITOR_RESOURCES_MENU = "#menuform\\:sr_resources_monitor";
  private static final String MONITOR_LOGS_MENU = "#menuform\\:sr_logs";

  public static void toDashboard()
  {
    toMenu(DASHBOARD_MENU);
    assertCurrentUrlEndsWith("dashboard.xhtml");
    menuShouldBeActive(DASHBOARD_MENU);
  }
  
  public static void toApplications()
  {
    toMenu(APPLICATIONS_MENU);
    assertCurrentUrlEndsWith("applications.xhtml");
    menuShouldBeActive(APPLICATIONS_MENU);
  }
  
  public static void toApplicationDetail(String appName)
  {
    Navigation.toApplications();
    clickAppTreeActivity(appName);
    assertCurrentUrlEndsWith("application-detail.xhtml?appName=" + appName);
    menuShouldBeActive(APPLICATIONS_MENU);
  }
  
  public static void toPmvDetail(String appName, String pmName, String pmvName)
  {
    Navigation.toApplications();
    openAppTreeActivity(appName);
    openAppTreeActivity(pmName);
    clickAppTreeActivity(pmvName);
    menuShouldBeActive(APPLICATIONS_MENU);
  }

  private static void openAppTreeActivity(String appName)
  {
    $$(".activity-name").find(text(appName)).parent().parent().find(".ui-treetable-toggler").shouldBe(visible).click();
  }
  
  private static void clickAppTreeActivity(String appName)
  {
    $$(".activity-name").find(text(appName)).shouldBe(visible).click();
  }
  
  public static void toSecuritySystem()
  {
    toSubMenu(SECURITY_MENU, SECURITY_SYSTEM_MENU);
    assertCurrentUrlEndsWith("securitysystem.xhtml");
    menuShouldBeActive(SECURITY_SYSTEM_MENU);
  }
  
  public static void toSecuritySystemDetail(String secSystemName)
  {
    Navigation.toSecuritySystem();
    $$(".security-name").find(text(secSystemName)).shouldBe(visible).click();
    assertCurrentUrlEndsWith("security-detail.xhtml?securitySystemName=" + secSystemName);
    menuShouldBeActive(SECURITY_SYSTEM_MENU);
  }

  public static void toVariables()
  {
    toSubMenu(CONFIGURATION_MENU, VARIABLES_MENU);
    assertCurrentUrlEndsWith("global-variables.xhtml");
    menuShouldBeActive(VARIABLES_MENU);
  }
  
  public static void toBusinessCalendar()
  {
    toSubMenu(CONFIGURATION_MENU, BUSINESS_CALENDAR_MENU);
    assertCurrentUrlEndsWith("businesscalendar.xhtml");
    menuShouldBeActive(BUSINESS_CALENDAR_MENU);
  }
  
  public static void toBusinessCalendarDetail(String calendarName)
  {
    Navigation.toBusinessCalendar();
    $$(Tab.ACITVE_PANEL_CSS + " .ui-treenode-content a").find(text(calendarName)).shouldBe(visible).click();
    assertCurrentUrlEndsWith("businesscalendar-detail.xhtml?calendarName=" + calendarName);
    menuShouldBeActive(BUSINESS_CALENDAR_MENU);
  }
  
  public static void toUsers()
  {
    toSubMenu(SECURITY_MENU, SECURITY_USER_MENU);
    assertCurrentUrlEndsWith("users.xhtml");
    menuShouldBeActive(SECURITY_USER_MENU);
  }
  
  public static void toUserDetail(String userName)
  {
    Navigation.toUsers();
    $$(Tab.ACITVE_PANEL_CSS + " .user-name").find(text(userName)).shouldBe(visible).click();
    assertCurrentUrlEndsWith("userdetail.xhtml?userName=" + userName);
    menuShouldBeActive(SECURITY_USER_MENU);
  }
  
  public static void toRoles()
  {
    toSubMenu(SECURITY_MENU, SECURITY_ROLES_MENU);
    assertCurrentUrlEndsWith("roles.xhtml");
    menuShouldBeActive(SECURITY_ROLES_MENU);
  }
  
  public static void toRoleDetail(String roleName)
  {
    Navigation.toRoles();
    $$(Tab.ACITVE_PANEL_CSS + " .role-name").find(text(roleName)).shouldBe(visible).click();
    assertCurrentUrlEndsWith("roledetail.xhtml?roleName=" + roleName);
    menuShouldBeActive(SECURITY_ROLES_MENU);
  }
  
  public static void toSearchEngine()
  {
    toSubMenu(SERVICES_MENU, SERVICES_SEARCH_ENGINE);
    assertCurrentUrlEndsWith("searchengine.xhtml");
    menuShouldBeActive(SERVICES_SEARCH_ENGINE);
  }
  
  public static void toEmail()
  {
    toSubMenu(SERVICES_MENU, SERVICES_EMAIL_MENU);
    assertCurrentUrlEndsWith("email.xhtml");
    menuShouldBeActive(SERVICES_EMAIL_MENU);
  }
  
  public static void toExternalDatabases()
  {
    toSubMenu(SERVICES_MENU, SERVICES_DATABASES_MENU);
    assertCurrentUrlEndsWith("externaldatabases.xhtml");
    menuShouldBeActive(SERVICES_DATABASES_MENU);
  }
  
  public static void toExternalDatabaseDetail(String databaseName)
  {
    Navigation.toExternalDatabases();
    $$(Tab.ACITVE_PANEL_CSS + " .database-name").find(text(databaseName)).shouldBe(visible).click();
    assertCurrentUrlEndsWith("externaldatabasedetail.xhtml?databaseName=" + databaseName);
    menuShouldBeActive(SERVICES_DATABASES_MENU);
  }
  
  public static void toRestClients()
  {
    toSubMenu(SERVICES_MENU, SERVICES_RESTCLIENTS_MENU);
    assertCurrentUrlEndsWith("restclients.xhtml");
    menuShouldBeActive(SERVICES_RESTCLIENTS_MENU);
  }
  
  public static void toRestClientDetail(String restClientName)
  {
    Navigation.toRestClients();
    $$(Tab.ACITVE_PANEL_CSS + " .restclient-name").find(text(restClientName)).shouldBe(visible).click();
    assertCurrentUrlEndsWith("restclientdetail.xhtml?restClientName=" + restClientName);
    menuShouldBeActive(SERVICES_RESTCLIENTS_MENU);
  }
  
  public static void toWebservices()
  {
    toSubMenu(SERVICES_MENU, SERVICES_WEBSERVICES_MENU);
    assertCurrentUrlEndsWith("webservices.xhtml");
    menuShouldBeActive(SERVICES_WEBSERVICES_MENU);
  }
  
  public static void toWebserviceDetail(String webserviceName)
  {
    Navigation.toWebservices();
    $$(Tab.ACITVE_PANEL_CSS + " .webservice-name").find(text(webserviceName)).shouldBe(visible).click();
    assertCurrentUrlContains("webservicedetail.xhtml?webserviceId=");
    menuShouldBeActive(SERVICES_WEBSERVICES_MENU);
  }
  
  public static void toAdmins()
  {
    toSubMenu(SYSTEM_MENU, SYSTEM_ADMINS);
    assertCurrentUrlEndsWith("admins.xhtml");
    menuShouldBeActive(SYSTEM_ADMINS);
  }

  public static void toSystemDb()
  {
    toSubMenu(SYSTEM_MENU, SYSTEM_SYSTEMDB_MENU);
    assertCurrentUrlEndsWith("systemdb.xhtml");
    menuShouldBeActive(SYSTEM_SYSTEMDB_MENU);
  }
  
  public static void toWebServer()
  {
    toSubMenu(SYSTEM_MENU, SYSTEM_WEB_SERVER_MENU);
    assertCurrentUrlEndsWith("webserver.xhtml");
    menuShouldBeActive(SYSTEM_WEB_SERVER_MENU);
  }
  
  public static void toSystemConfig()
  {
    toSubMenu(SYSTEM_MENU, SYSTEM_CONFIG_MENU);
    assertCurrentUrlEndsWith("systemconfig.xhtml");
    menuShouldBeActive(SYSTEM_CONFIG_MENU);
  }
  
  public static void toCluster()
  {
    Selenide.open(viewUrl("cluster.xhtml?cluster"));
    assertCurrentUrlContains("cluster.xhtml");
    menuShouldBeActive(SYSTEM_CLUSTER);
  }
  
  public static void toEditor()
  {
    toSubMenu(SYSTEM_MENU, SYSTEM_EDITOR_MENU);
    assertCurrentUrlEndsWith("editor.xhtml");
    menuShouldBeActive(SYSTEM_EDITOR_MENU);
  }
  
  public static void toResourcesMonitor()
  {
    toSubMenu(MONITOR_MENU, MONITOR_RESOURCES_MENU);
    assertCurrentUrlEndsWith("monitor.xhtml");
    menuShouldBeActive(MONITOR_RESOURCES_MENU);
  }
  
  public static void toLogs()
  {
    toSubMenu(MONITOR_MENU, MONITOR_LOGS_MENU);
    assertCurrentUrlContains("logs.xhtml");
    menuShouldBeActive(MONITOR_LOGS_MENU);
  }
  
  private static void toMenu(String menuItemPath)
  {
    $(menuItemPath).find("a").click();
    menuShouldBeActive(menuItemPath);
  }
  
  private static void toSubMenu(String menuItemPath, String subMenuItemPath)
  {
    $(menuItemPath).shouldBe(visible);
    if(!$(subMenuItemPath).isDisplayed()) {
      $(menuItemPath).find("a").click();
    }
    $(subMenuItemPath).find("a").shouldBe(visible).click();
    menuShouldBeActive(subMenuItemPath);
  }
  
  private static void menuShouldBeActive(String menu)
  {
    $(menu).shouldHave(cssClass("active-menuitem"));
  }

}