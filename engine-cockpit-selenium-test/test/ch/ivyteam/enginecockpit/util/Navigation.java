package ch.ivyteam.enginecockpit.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.WebBase;

public class Navigation
{
  private static final By DASHBOARD_MENU = By.xpath("//li[@id='menuform:sr_dashboard']/child::a");
  private static final By APPLICATIONS_MENU = By.xpath("//li[@id='menuform:sr_applications']/child::a");
  private static final By SECURITY_MENU = By.xpath("//li[@id='menuform:sr_security']/child::a");
  private static final By SECURITY_SYSTEM_MENU = By.xpath("//li[@id='menuform:sr_security_system']/child::a");
  private static final By SECURITY_USER_MENU = By.xpath("//li[@id='menuform:sr_users']/child::a");
  private static final By SECURITY_ROLES_MENU = By.xpath("//li[@id='menuform:sr_roles']/child::a");
  private static final By CONFIGURATION_MENU = By.xpath("//li[@id='menuform:sr_configuration']/child::a");
  private static final By VARIABLES_MENU = By.xpath("//li[@id='menuform:sr_variables']/child::a");
  private static final By BUSINESS_CALENDAR_MENU = By.xpath("//li[@id='menuform:sr_business_calendar']/child::a");
  private static final By SERVICES_MENU = By.xpath("//li[@id='menuform:sr_services']/child::a");
  private static final By SERVICES_SEARCH_ENGINE = By.xpath("//li[@id='menuform:sr_searchengine']/child::a");
  private static final By SERVICES_EMAIL_MENU = By.xpath("//li[@id='menuform:sr_email']/child::a");
  private static final By SERVICES_DATABASES_MENU = By.xpath("//li[@id='menuform:sr_database']/child::a");
  private static final By SERVICES_RESTCLIENTS_MENU = By.xpath("//li[@id='menuform:sr_rest_client']/child::a");
  private static final By SERVICES_WEBSERVICES_MENU = By.xpath("//li[@id='menuform:sr_web_service']/child::a");
  private static final By SYSTEM_CONFIG_MENU = By.xpath("//li[@id='menuform:sr_system_config']/child::a");
  private static final By MONITOR_MENU = By.xpath("//li[@id='menuform:sr_monitor']/child::a");
  private static final By LOGS_MENU = By.xpath("//li[@id='menuform:sr_logs']/child::a");

  public static void toDashboard(FirefoxDriver driver)
  {
    toMenu(driver, DASHBOARD_MENU);
    assertCurrentUrlEndsWith(driver, "dashboard.xhtml");
  }
  
  public static void toApplications(FirefoxDriver driver)
  {
    toMenu(driver, APPLICATIONS_MENU);
    assertCurrentUrlEndsWith(driver, "applications.xhtml");
  }
  
  public static void toApplicationDetail(FirefoxDriver driver, String appName)
  {
    Navigation.toApplications(driver);
    driver.findElementByXPath("//span[@class='activity-name'][text()='" + appName + "']").click();
    assertCurrentUrlEndsWith(driver, "application-detail.xhtml?appName=" + appName);
  }
  
  public static void toSecuritySystem(FirefoxDriver driver)
  {
    toSubMenu(driver, SECURITY_MENU, SECURITY_SYSTEM_MENU);
    assertCurrentUrlEndsWith(driver, "securitysystem.xhtml");
  }
  
  public static void toSecuritySystemDetail(FirefoxDriver driver, String secSystemName)
  {
    Navigation.toSecuritySystem(driver);
    WebBase.waitUntilElementClickable(driver, By.xpath("//span[@class='security-name'][text()='" + secSystemName + "']")).click();
    assertCurrentUrlEndsWith(driver, "security-detail.xhtml?securitySystemName=" + secSystemName);
  }

  public static void toVariables(FirefoxDriver driver)
  {
    toSubMenu(driver, CONFIGURATION_MENU, VARIABLES_MENU);
    assertCurrentUrlEndsWith(driver, "global-variables.xhtml");
  }
  
  public static void toBusinessCalendar(FirefoxDriver driver)
  {
    toSubMenu(driver, CONFIGURATION_MENU, BUSINESS_CALENDAR_MENU);
    assertCurrentUrlEndsWith(driver, "businesscalendar.xhtml");
  }
  
  public static void toBusinessCalendarDetail(FirefoxDriver driver, String calendarName)
  {
    Navigation.toBusinessCalendar(driver);
    driver.findElementByXPath("//*[@id=\"calendarTree\"]//a[contains(@id, 'calendarNode')][text()='" + calendarName + "']").click();
    assertCurrentUrlEndsWith(driver, "businesscalendar-detail.xhtml?calendarName=" + calendarName);
  }
  
  public static void toUsers(FirefoxDriver driver)
  {
    toSubMenu(driver, SECURITY_MENU, SECURITY_USER_MENU);
    assertCurrentUrlEndsWith(driver, "users.xhtml");
  }
  
  public static void toUserDetail(FirefoxDriver driver, String name)
  {
    Navigation.toUsers(driver);
    WebBase.waitUntilElementClickable(driver, 
            By.xpath("//div[contains(@class, 'ui-tabs-panel')]//span[@class='user-name'][text()='" + name + "']")).click();
    assertCurrentUrlEndsWith(driver, "userdetail.xhtml?userName=" + name);
  }
  
  public static void toRoles(FirefoxDriver driver)
  {
    toSubMenu(driver, SECURITY_MENU, SECURITY_ROLES_MENU);
    assertCurrentUrlEndsWith(driver, "roles.xhtml");
  }
  
  public static void toRoleDetail(FirefoxDriver driver, String name)
  {
    Navigation.toRoles(driver);
    WebBase.waitUntilElementClickable(driver, 
            By.xpath("//div[contains(@class, 'ui-tabs-panel')]//a[@class='role-name'][text()='" + name + "']")).click();
    assertCurrentUrlEndsWith(driver, "roledetail.xhtml?roleName=" + name);
  }
  
  public static void toSearchEngine(FirefoxDriver driver)
  {
    toSubMenu(driver, SERVICES_MENU, SERVICES_SEARCH_ENGINE);
    assertCurrentUrlEndsWith(driver, "searchengine.xhtml");
  }
  
  public static void toEmail(FirefoxDriver driver)
  {
    toSubMenu(driver, SERVICES_MENU, SERVICES_EMAIL_MENU);
    assertCurrentUrlEndsWith(driver, "email.xhtml");
  }
  
  public static void toExternalDatabases(FirefoxDriver driver)
  {
    toSubMenu(driver, SERVICES_MENU, SERVICES_DATABASES_MENU);
    assertCurrentUrlEndsWith(driver, "externaldatabases.xhtml");
  }
  
  public static void toExternalDatabaseDetail(FirefoxDriver driver, String name)
  {
    Navigation.toExternalDatabases(driver);
    WebBase.waitUntilElementClickable(driver, 
            By.xpath("//div[contains(@class, 'ui-tabs-panel')]//span[@class='database-name'][text()='" + name + "']")).click();
    assertCurrentUrlEndsWith(driver, "externaldatabasedetail.xhtml?databaseName=" + name);
  }
  
  public static void toRestClients(FirefoxDriver driver)
  {
    toSubMenu(driver, SERVICES_MENU, SERVICES_RESTCLIENTS_MENU);
    assertCurrentUrlEndsWith(driver, "restclients.xhtml");
  }
  
  public static void toRestClientDetail(FirefoxDriver driver, String name)
  {
    Navigation.toRestClients(driver);
    WebBase.waitUntilElementClickable(driver, 
            By.xpath("//div[contains(@class, 'ui-tabs-panel')]//span[@class='restclient-name'][text()='" + name + "']")).click();
    assertCurrentUrlEndsWith(driver, "restclientdetail.xhtml?restClientName=" + name);
  }
  
  public static void toWebservices(FirefoxDriver driver)
  {
    toSubMenu(driver, SERVICES_MENU, SERVICES_WEBSERVICES_MENU);
    assertCurrentUrlEndsWith(driver, "webservices.xhtml");
  }
  
  public static void toWebserviceDetail(FirefoxDriver driver, String name)
  {
    Navigation.toWebservices(driver);
    WebBase.waitUntilElementClickable(driver, 
            By.xpath("//div[contains(@class, 'ui-tabs-panel')]//span[@class='webservice-name'][text()='" + name + "']")).click();
    assertCurrentUrlContains(driver, "webservicedetail.xhtml?webserviceId=");
  }
  
  public static void toSystemConfig(FirefoxDriver driver)
  {
    toMenu(driver, SYSTEM_CONFIG_MENU);
    assertCurrentUrlEndsWith(driver, "systemconfig.xhtml");
  }
  
  public static void toMonitor(FirefoxDriver driver)
  {
    toMenu(driver, MONITOR_MENU);
    assertCurrentUrlEndsWith(driver, "monitor.xhtml");
  }
  
  public static void toLogs(FirefoxDriver driver)
  {
    toMenu(driver, LOGS_MENU);
    assertCurrentUrlContains(driver, "logs.xhtml");
  }
  
  private static void toMenu(FirefoxDriver driver, By menuItemPath)
  {
    driver.findElement(menuItemPath).click();
    WebBase.webAssertThat(() -> assertThat(driver.findElement(menuItemPath).findElement(By.xpath("./..")).
            getAttribute("class")).contains("active-menuitem"));
  }
  
  private static void toSubMenu(FirefoxDriver driver, By menuItemPath, By subMenuItemPath)
  {
    if(!driver.findElement(subMenuItemPath).isDisplayed()) {
      WebBase.waitUntilElementClickable(driver, menuItemPath).click();
    }
    WebBase.waitUntilElementClickable(driver, subMenuItemPath).click();
    WebBase.webAssertThat(() -> assertThat(driver.findElement(subMenuItemPath).findElement(By.xpath("./..")).
            getAttribute("class")).contains("active-menuitem"));
  }
  
  private static void assertCurrentUrlEndsWith(FirefoxDriver driver, String page)
  {
    WebBase.webAssertThat(() -> assertThat(driver.getCurrentUrl()).endsWith(page));
  }
  
  private static void assertCurrentUrlContains(FirefoxDriver driver, String page)
  {
    WebBase.webAssertThat(() -> assertThat(driver.getCurrentUrl()).contains(page));
  }
  
}
