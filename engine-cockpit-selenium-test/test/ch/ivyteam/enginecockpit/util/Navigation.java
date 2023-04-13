package ch.ivyteam.enginecockpit.util;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.viewUrl;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.Selenide;

public class Navigation {

  private static final String DASHBOARD_MENU = "#menuform\\:sr_dashboard";
  private static final String APPLICATIONS_MENU = "#menuform\\:sr_applications";
  private static final String SECURITY_MENU = "#menuform\\:sr_security";
  private static final String SECURITY_SYSTEM_MENU = "#menuform\\:sr_security_system";
  private static final String SECURITY_USER_MENU = "#menuform\\:sr_users";
  private static final String SECURITY_ROLES_MENU = "#menuform\\:sr_roles";
  private static final String CONFIGURATION_MENU = "#menuform\\:sr_configuration";
  private static final String VARIABLES_MENU = "#menuform\\:sr_variables";
  private static final String BUSINESS_CALENDAR_MENU = "#menuform\\:sr_business_calendar";
  private static final String BRANDING_MENU = "#menuform\\:sr_branding";
  private static final String SERVICES_MENU = "#menuform\\:sr_services";
  private static final String SERVICES_SEARCH_ENGINE = "#menuform\\:sr_searchengine";
  private static final String SERVICES_EMAIL_MENU = "#menuform\\:sr_email";
  private static final String SERVICES_DATABASES_MENU = "#menuform\\:sr_database";
  private static final String SERVICES_RESTCLIENTS_MENU = "#menuform\\:sr_rest_client";
  private static final String SERVICES_WEBSERVICES_MENU = "#menuform\\:sr_web_service";
  private static final String SERVICES_BACKEND_MENU = "#menuform\\:sr_backend";
  private static final String SYSTEM_MENU = "#menuform\\:sr_system";
  private static final String SYSTEM_ADMINS = "#menuform\\:sr_admins";
  private static final String SYSTEM_SYSTEMDB_MENU = "#menuform\\:sr_systemdb";
  private static final String SYSTEM_LICENCE = "#menuform\\:sr_licence";
  private static final String SYSTEM_WEB_SERVER_MENU = "#menuform\\:sr_web_server";
  private static final String SYSTEM_CONFIG_MENU = "#menuform\\:sr_system_config";
  private static final String SYSTEM_CLUSTER = "#menuform\\:sr_cluster";
  private static final String SYSTEM_EDITOR_MENU = "#menuform\\:sr_editor";
  private static final String MONITOR_MENU = "#menuform\\:sr_monitor";
  private static final String MONITOR_OS_MENU = "#menuform\\:sr_monitor_os";
  private static final String MONITOR_LOGS_MENU = "#menuform\\:sr_logs";
  private static final String MONITOR_ENGINE_MENU = "#menuform\\:sr_monitor_engine";
  private static final String MONITOR_ENGINE_JVM_MENU = "#menuform\\:sr_monitor_engine_jvm";
  private static final String MONITOR_ENGINE_MEMORY_MENU = "#menuform\\:sr_monitor_engine_memory";
  private static final String MONITOR_ENGINE_MBEANS_MENU = "#menuform\\:sr_monitor_engine_mbeans";
  private static final String MONITOR_ENGINE_CACHE_MENU = "#menuform\\:sr_monitor_engine_cache";
  private static final String MONITOR_ENGINE_SESSION_MENU = "#menuform\\:sr_monitor_engine_sessions";
  private static final String MONITOR_PERFORMANCE_MENU = "#menuform\\:sr_monitor_performance";
  private static final String MONITOR_PERFORMANCE_PROCESS_EXECUTION_MENU = "#menuform\\:sr_monitor_performance_process_execution";
  private static final String MONITOR_PERFORMANCE_TRACES_MENU = "#menuform\\:sr_monitor_performance_traces";
  private static final String MONITOR_PERFORMANCE_TRAFFIC_GRAPH = "#menuform\\:sr_monitor_performance_traffic_graph";
  private static final String MONITOR_PERFORMANCE_THREADS = "#menuform\\:sr_monitor_performance_threads";
  private static final String MONITOR_PERFORMANCE_JFR = "#menuform\\:sr_monitor_performance_jfr";

  public static void toDashboard() {
    toMenu(DASHBOARD_MENU);
    assertCurrentUrlContains("dashboard.xhtml");
    menuShouldBeActive(DASHBOARD_MENU);
  }

  public static void toApplications() {
    toMenu(APPLICATIONS_MENU);
    assertCurrentUrlContains("applications.xhtml");
    menuShouldBeActive(APPLICATIONS_MENU);
  }

  public static void toApplicationDetail(String appName) {
    Navigation.toApplications();
    clickAppTreeActivity(appName);
    assertCurrentUrlContains("application-detail.xhtml?appName=" + appName);
    menuShouldBeActive(APPLICATIONS_MENU);
  }

  public static void toPmvDetail(String appName, String pmName, String pmvName) {
    Navigation.toApplications();
    openAppTreeActivity(appName);
    openAppTreeActivity(pmName);
    clickAppTreeActivity(pmvName);
    menuShouldBeActive(APPLICATIONS_MENU);
  }

  private static void openAppTreeActivity(String appName) {
    $$(".activity-name").find(exactText(appName))
            .parent()
            .parent()
            .find(".ui-treetable-toggler").shouldBe(visible)
            .click();
  }

  private static void clickAppTreeActivity(String appName) {
    $$(".activity-name").find(text(appName)).shouldBe(visible).click();
  }

  public static void toSecuritySystem() {
    toSubMenu(SECURITY_MENU, SECURITY_SYSTEM_MENU);
    assertCurrentUrlContains("securitysystem.xhtml");
    menuShouldBeActive(SECURITY_SYSTEM_MENU);
  }

  public static void toSecuritySystemDetail(String secSystemName) {
    Navigation.toSecuritySystem();
    $$(".security-name").find(text(secSystemName)).shouldBe(visible).click();
    assertCurrentUrlContains("security-detail.xhtml?securitySystemName=" + secSystemName);
    menuShouldBeActive(SECURITY_SYSTEM_MENU);
  }

  public static void toSecuritySystemLdap(String secSystemName) {
    Navigation.toSecuritySystem();
    $$(".security-name").find(text(secSystemName)).shouldBe(visible).click();
    assertCurrentUrlContains("security-detail.xhtml?securitySystemName=" + secSystemName);
    menuShouldBeActive(SECURITY_SYSTEM_MENU);
    $("#securityProviderForm\\:editProviderBtnJndi").shouldBe(visible).click();
    assertCurrentUrlContains("security-ldap.xhtml?securitySystemName=" + secSystemName);
    menuShouldBeActive(SECURITY_SYSTEM_MENU);
  }

  public static void toSecuritySystemProvider(String secSystemName) {
    Navigation.toSecuritySystem();
    $$(".security-name").find(text(secSystemName)).shouldBe(visible).click();
    assertCurrentUrlContains("security-detail.xhtml?securitySystemName=" + secSystemName);
    menuShouldBeActive(SECURITY_SYSTEM_MENU);
    $("#securityProviderForm\\:editProviderBtn").shouldBe(visible).click();
    assertCurrentUrlContains("security-provider.xhtml?securitySystemName=" + secSystemName);
    menuShouldBeActive(SECURITY_SYSTEM_MENU);
  }

  public static void toVariables() {
    toSubMenu(CONFIGURATION_MENU, VARIABLES_MENU);
    assertCurrentUrlContains("variables.xhtml");
    menuShouldBeActive(VARIABLES_MENU);
  }

  public static void toBusinessCalendar() {
    toSubMenu(CONFIGURATION_MENU, BUSINESS_CALENDAR_MENU);
    assertCurrentUrlContains("businesscalendar.xhtml");
    menuShouldBeActive(BUSINESS_CALENDAR_MENU);
  }

  public static void toBusinessCalendarDetail(String calendarName) {
    Navigation.toBusinessCalendar();
    $$(Tab.APP.activePanelCss + " .business-calendar").find(text(calendarName)).shouldBe(visible).click();
    assertCurrentUrlContains("businesscalendar-detail.xhtml?calendarName=" + calendarName);
    menuShouldBeActive(BUSINESS_CALENDAR_MENU);
  }

  public static void toBranding() {
    toSubMenu(CONFIGURATION_MENU, BRANDING_MENU);
    assertCurrentUrlContains("branding.xhtml");
    menuShouldBeActive(BRANDING_MENU);
  }

  public static void toUsers() {
    toSubMenu(SECURITY_MENU, SECURITY_USER_MENU);
    assertCurrentUrlContains("users.xhtml");
    menuShouldBeActive(SECURITY_USER_MENU);
  }

  public static void toUserDetail(String userName) {
    toUserDetail("default", userName);
  }

  public static void toUserDetail(String system, String userName) {
    Navigation.toUsers();
    $$(Tab.SECURITY_SYSTEM.activePanelCss + " .user-name").find(text(userName)).shouldBe(visible).click();
    assertCurrentUrlContains("userdetail.xhtml?system=" + system + "&name=" + userName);
    menuShouldBeActive(SECURITY_USER_MENU);
  }

  public static void toRoles() {
    toSubMenu(SECURITY_MENU, SECURITY_ROLES_MENU);
    assertCurrentUrlContains("roles.xhtml");
    menuShouldBeActive(SECURITY_ROLES_MENU);
  }

  public static void toRoleDetail(String roleName) {
    toRoleDetail("default", roleName);
  }

  public static void toRoleDetail(String system, String roleName) {
    Navigation.toRoles();
    $(Tab.SECURITY_SYSTEM.activePanelCss + " .expand-all").shouldBe(visible).click();
    $$(Tab.SECURITY_SYSTEM.activePanelCss + " .role-name").find(text(roleName)).shouldBe(visible).click();
    assertCurrentUrlContains("roledetail.xhtml?system="+system+"&name=" + roleName);
    menuShouldBeActive(SECURITY_ROLES_MENU);
  }

  public static void toSearchEngine() {
    toSubMenu(SERVICES_MENU, SERVICES_SEARCH_ENGINE);
    assertCurrentUrlContains("searchengine.xhtml");
    menuShouldBeActive(SERVICES_SEARCH_ENGINE);
  }

  public static void toEmail() {
    toSubMenu(SERVICES_MENU, SERVICES_EMAIL_MENU);
    assertCurrentUrlContains("email.xhtml");
    menuShouldBeActive(SERVICES_EMAIL_MENU);
  }

  public static void toDatabases() {
    toSubMenu(SERVICES_MENU, SERVICES_DATABASES_MENU);
    assertCurrentUrlContains("databases.xhtml");
    menuShouldBeActive(SERVICES_DATABASES_MENU);
  }

  public static void toDatabaseDetail(String databaseName) {
    Navigation.toDatabases();
    $$(Tab.APP.activePanelCss + " .database-name").find(text(databaseName)).shouldBe(visible).click();
    assertCurrentUrlContains("databasedetail.xhtml?app=" + Tab.DEFAULT_APP + "&env=Default&name=" + databaseName);
    menuShouldBeActive(SERVICES_DATABASES_MENU);
  }

  public static void toRestClients() {
    toSubMenu(SERVICES_MENU, SERVICES_RESTCLIENTS_MENU);
    assertCurrentUrlContains("restclients.xhtml");
    menuShouldBeActive(SERVICES_RESTCLIENTS_MENU);
  }

  public static void toRestClientDetail(String restClientName) {
    Navigation.toRestClients();
    $$(Tab.APP.activePanelCss + " .restclient-name").find(text(restClientName)).shouldBe(visible).click();
    assertCurrentUrlContains("restclientdetail.xhtml?app=" + Tab.DEFAULT_APP + "&env=Default&name=" + restClientName);
    menuShouldBeActive(SERVICES_RESTCLIENTS_MENU);
  }

  public static void toWebservices() {
    toSubMenu(SERVICES_MENU, SERVICES_WEBSERVICES_MENU);
    assertCurrentUrlContains("webservices.xhtml");
    menuShouldBeActive(SERVICES_WEBSERVICES_MENU);
  }

  public static void toWebserviceDetail(String webserviceName) {
    Navigation.toWebservices();
    $$(Tab.APP.activePanelCss + " .webservice-name").find(text(webserviceName)).shouldBe(visible).click();
    assertCurrentUrlContains("webservicedetail.xhtml?app=" + Tab.DEFAULT_APP + "&env=Default&id=");
    menuShouldBeActive(SERVICES_WEBSERVICES_MENU);
  }

  public static void toBackendApi() {
    toSubMenu(SERVICES_MENU, SERVICES_BACKEND_MENU);
    assertCurrentUrlContains("backend-api.xhtml");
    menuShouldBeActive(SERVICES_BACKEND_MENU);
  }

  public static void toAdmins() {
    toSubMenu(SYSTEM_MENU, SYSTEM_ADMINS);
    assertCurrentUrlContains("admins.xhtml");
    menuShouldBeActive(SYSTEM_ADMINS);
  }

  public static void toSystemDb() {
    toSubMenu(SYSTEM_MENU, SYSTEM_SYSTEMDB_MENU);
    assertCurrentUrlContains("systemdb.xhtml");
    menuShouldBeActive(SYSTEM_SYSTEMDB_MENU);
  }

  public static void toLicence() {
    toSubMenu(SYSTEM_MENU, SYSTEM_LICENCE);
    assertCurrentUrlContains("licence.xhtml");
    menuShouldBeActive(SYSTEM_LICENCE);
  }

  public static void toWebServer() {
    toSubMenu(SYSTEM_MENU, SYSTEM_WEB_SERVER_MENU);
    assertCurrentUrlContains("webserver.xhtml");
    menuShouldBeActive(SYSTEM_WEB_SERVER_MENU);
  }

  public static void toSystemConfig() {
    toSubMenu(SYSTEM_MENU, SYSTEM_CONFIG_MENU);
    assertCurrentUrlContains("systemconfig.xhtml");
    menuShouldBeActive(SYSTEM_CONFIG_MENU);
  }

  public static void toCluster() {
    Selenide.open(viewUrl("cluster.xhtml?cluster"));
    assertCurrentUrlContains("cluster.xhtml");
    menuShouldBeActive(SYSTEM_CLUSTER);
  }

  public static void toEditor() {
    toSubMenu(SYSTEM_MENU, SYSTEM_EDITOR_MENU);
    assertCurrentUrlContains("editor.xhtml");
    menuShouldBeActive(SYSTEM_EDITOR_MENU);
  }

  public static void toOs() {
    toSubMenu(MONITOR_MENU, MONITOR_OS_MENU);
    assertCurrentUrlContains("monitorOs.xhtml");
    menuShouldBeActive(MONITOR_OS_MENU);
  }

  public static void toLogs() {
    toSubMenu(MONITOR_MENU, MONITOR_LOGS_MENU);
    assertCurrentUrlContains("logs.xhtml");
    menuShouldBeActive(MONITOR_LOGS_MENU);
  }

  public static void toCache() {
    toSubSubMenu(MONITOR_MENU, MONITOR_ENGINE_MENU, MONITOR_ENGINE_CACHE_MENU);
    assertCurrentUrlContains("monitorCache.xhtml");
    menuShouldBeActive(MONITOR_ENGINE_CACHE_MENU);
  }

  public static void toSessions() {
    toSubSubMenu(MONITOR_MENU, MONITOR_ENGINE_MENU, MONITOR_ENGINE_SESSION_MENU);
    assertCurrentUrlContains("monitor-sessions.xhtml");
    menuShouldBeActive(MONITOR_ENGINE_SESSION_MENU);
  }

  public static void toMBeans() {
    toSubSubMenu(MONITOR_MENU, MONITOR_ENGINE_MENU, MONITOR_ENGINE_MBEANS_MENU);
    assertCurrentUrlContains("mbeans.xhtml");
    menuShouldBeActive(MONITOR_ENGINE_MBEANS_MENU);
  }

  public static void toJvm() {
    toSubSubMenu(MONITOR_MENU, MONITOR_ENGINE_MENU, MONITOR_ENGINE_JVM_MENU);
    assertCurrentUrlContains("monitorJvm.xhtml");
    menuShouldBeActive(MONITOR_ENGINE_JVM_MENU);
  }

  public static void toMemory() {
    toSubSubMenu(MONITOR_MENU, MONITOR_ENGINE_MENU, MONITOR_ENGINE_MEMORY_MENU);
    assertCurrentUrlContains("monitorMemory.xhtml");
    menuShouldBeActive(MONITOR_ENGINE_MEMORY_MENU);
  }

  public static void toProcessExecution() {
    toSubSubMenu(MONITOR_MENU, MONITOR_PERFORMANCE_MENU, MONITOR_PERFORMANCE_PROCESS_EXECUTION_MENU);
  }

  public static void toSlowRequests() {
    toSubSubMenu(MONITOR_MENU, MONITOR_PERFORMANCE_MENU, MONITOR_PERFORMANCE_TRACES_MENU);
  }

  public static void toTrafficGraph() {
    toSubSubMenu(MONITOR_MENU, MONITOR_PERFORMANCE_MENU, MONITOR_PERFORMANCE_TRAFFIC_GRAPH);
  }

  public static void toThreads() {
    toSubSubMenu(MONITOR_MENU, MONITOR_PERFORMANCE_MENU, MONITOR_PERFORMANCE_THREADS);
  }

  public static void toJfr() {
    toSubSubMenu(MONITOR_MENU, MONITOR_PERFORMANCE_MENU, MONITOR_PERFORMANCE_JFR);
  }

  private static void toMenu(String menuItemPath) {
    $(menuItemPath).find("a").scrollIntoView(false).click();
    menuShouldBeActive(menuItemPath);
  }

  private static void toSubMenu(String menuItemPath, String subMenuItemPath) {
    $(menuItemPath).shouldBe(visible);
    if (!$(subMenuItemPath).isDisplayed()) {
      $(menuItemPath).find("a").scrollIntoView(false).click();
    }
    $(subMenuItemPath).find("a").shouldBe(visible).scrollIntoView(false).click();
    menuShouldBeActive(subMenuItemPath);
  }

  private static void toSubSubMenu(String menuItemPath, String subMenuItemPath, String subSubMenuItemPath) {
    $(menuItemPath).shouldBe(visible);
    if (!$(subMenuItemPath).isDisplayed()) {
      $(menuItemPath).find("a").scrollIntoView(false).click();
    }
    $(subMenuItemPath).find("a").shouldBe(visible);
    if (!$(subSubMenuItemPath).isDisplayed()) {
      $(subMenuItemPath).find("a").scrollIntoView(false).click();
    }
    $(subSubMenuItemPath).scrollIntoView(false).click();
    menuShouldBeActive(subSubMenuItemPath);
  }

  private static void menuShouldBeActive(String menu) {
    $(menu).shouldHave(cssClass("active-menuitem"));
  }
}
