package ch.ivyteam.enginecockpit.util;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

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
  private static final By EDITOR_MENU = By.xpath("//li[@id='menuform:sr_editor']/child::a");

  public static void toDashboard(RemoteWebDriver driver)
  {
    toMenu(driver, DASHBOARD_MENU);
    WebBase.webAssertThat(() -> driver.getCurrentUrl().endsWith("dashboard.xhtml"));
  }
  
  public static void toApplications(RemoteWebDriver driver)
  {
    toMenu(driver, APPLICATIONS_MENU);
    WebBase.webAssertThat(() -> driver.getCurrentUrl().endsWith("applications.xhtml"));
  }
  
  public static void toApplicationDetail(RemoteWebDriver driver, String appName)
  {
    Navigation.toApplications(driver);
    driver.findElementByXPath("//span[@class='activity-name'][text()='" + appName + "']").click();
    WebBase.webAssertThat(() -> driver.getCurrentUrl().endsWith("application-detail.xhtml?appName=" + appName)); 
  }
  
  public static void toSecuritySystem(RemoteWebDriver driver)
  {
    toSubMenu(driver, SECURITY_MENU, SECURITY_SYSTEM_MENU);
    WebBase.webAssertThat(() -> driver.getCurrentUrl().endsWith("securitysystem.xhtml"));
  }
  
  public static void toSecuritySystemDetail(RemoteWebDriver driver, String secSystemName)
  {
    Navigation.toSecuritySystem(driver);
    waitBeforeClick(driver, By.xpath("//span[@class='security-name'][text()='" + secSystemName + "']"));
    WebBase.webAssertThat(() -> driver.getCurrentUrl().endsWith("security-detail.xhtml?securitySystemName=" + secSystemName)); 
  }

  public static void toVariables(RemoteWebDriver driver)
  {
    toSubMenu(driver, CONFIGURATION_MENU, VARIABLES_MENU);
    WebBase.webAssertThat(() -> driver.getCurrentUrl().endsWith("global-variables.xhtml"));
  }
  
  public static void toBusinessCalendar(RemoteWebDriver driver)
  {
    toSubMenu(driver, CONFIGURATION_MENU, BUSINESS_CALENDAR_MENU);
    WebBase.webAssertThat(() -> driver.getCurrentUrl().endsWith("businesscalendar.xhtml"));
  }
  
  public static void toBusinessCalendarDetail(RemoteWebDriver driver, String calendarName)
  {
    Navigation.toBusinessCalendar(driver);
    driver.findElementByXPath("//div[contains(@class, 'ui-tabs-panel')]//a[contains(@id, 'calendarNode')][text()='" + calendarName + "']").click();
    WebBase.webAssertThat(() -> driver.getCurrentUrl().endsWith("businesscalendar-detail.xhtml?calendarName=" + calendarName));
  }
  
  public static void toUsers(RemoteWebDriver driver)
  {
    toSubMenu(driver, SECURITY_MENU, SECURITY_USER_MENU);
    WebBase.webAssertThat(() -> driver.getCurrentUrl().endsWith("users.xhtml"));
  }
  
  public static void toUserDetail(RemoteWebDriver driver, String userName)
  {
    Navigation.toUsers(driver);
    driver.findElementsByXPath("//div[contains(@class, 'ui-tabs-panel')]//span[@class='user-name'][text()='" + userName + "']").stream()
            .filter(e -> checkIfCorrectElement(e))
            .forEach(e -> e.click());
    WebBase.webAssertThat(() -> driver.getCurrentUrl().endsWith("userdetail.xhtml?userName=" + userName)); 
  }
  
  public static void toRoles(RemoteWebDriver driver)
  {
    toSubMenu(driver, SECURITY_MENU, SECURITY_ROLES_MENU);
    WebBase.webAssertThat(() -> driver.getCurrentUrl().endsWith("roles.xhtml"));
  }
  
  public static void toRoleDetail(RemoteWebDriver driver, String roleName)
  {
    Navigation.toRoles(driver);
    driver.findElementsByXPath("//div[contains(@class, 'ui-tabs-panel')]//a[@class='role-name'][text()='" + roleName + "']").stream()
            .filter(e -> checkIfCorrectElement(e))
            .forEach(e -> e.click());
    WebBase.webAssertThat(() -> driver.getCurrentUrl().endsWith("roledetail.xhtml?roleName=" + roleName)); 
  }
  
  public static void toSearchEngine(RemoteWebDriver driver)
  {
    toSubMenu(driver, SERVICES_MENU, SERVICES_SEARCH_ENGINE);
    WebBase.webAssertThat(() -> driver.getCurrentUrl().endsWith("searchengine.xhtml"));
  }
  
  public static void toEmail(RemoteWebDriver driver)
  {
    toSubMenu(driver, SERVICES_MENU, SERVICES_EMAIL_MENU);
    WebBase.webAssertThat(() -> driver.getCurrentUrl().endsWith("email.xhtml"));
  }
  
  public static void toExternalDatabases(RemoteWebDriver driver)
  {
    toSubMenu(driver, SERVICES_MENU, SERVICES_DATABASES_MENU);
    WebBase.webAssertThat(() -> driver.getCurrentUrl().endsWith("externaldatabases.xhtml"));
  }
  
  public static void toExternalDatabaseDetail(RemoteWebDriver driver, String databaseName)
  {
    Navigation.toExternalDatabases(driver);
    driver.findElementsByXPath("//div[contains(@class, 'ui-tabs-panel')]//span[@class='database-name'][text()='" + databaseName + "']").stream()
            .filter(e -> checkIfCorrectElement(e))
            .forEach(e -> e.click());
    WebBase.webAssertThat(() -> driver.getCurrentUrl().endsWith("externaldatabasedetail.xhtml?databaseName=" + databaseName)); 
  }
  
  public static void toRestClients(RemoteWebDriver driver)
  {
    toSubMenu(driver, SERVICES_MENU, SERVICES_RESTCLIENTS_MENU);
    WebBase.webAssertThat(() -> driver.getCurrentUrl().endsWith("restclients.xhtml"));
  }
  
  public static void toRestClientDetail(RemoteWebDriver driver, String restClientName)
  {
    Navigation.toRestClients(driver);
    driver.findElementsByXPath("//div[contains(@class, 'ui-tabs-panel')]//span[@class='restclient-name'][text()='" + restClientName + "']").stream()
            .filter(e -> checkIfCorrectElement(e))
            .forEach(e -> e.click());
    WebBase.webAssertThat(() -> driver.getCurrentUrl().endsWith("restclientdetail.xhtml?restClientName=" + restClientName)); 
  }
  
  public static void toWebservices(RemoteWebDriver driver)
  {
    toSubMenu(driver, SERVICES_MENU, SERVICES_WEBSERVICES_MENU);
    WebBase.webAssertThat(() -> driver.getCurrentUrl().endsWith("webservices.xhtml"));
  }
  
  public static void toWebserviceDetail(RemoteWebDriver driver, String webserviceName)
  {
    Navigation.toWebservices(driver);
    driver.findElementsByXPath("//div[contains(@class, 'ui-tabs-panel')]//span[@class='webservice-name'][text()='" + webserviceName + "']").stream()
            .filter(e -> checkIfCorrectElement(e))
            .forEach(e -> e.click());
    WebBase.webAssertThat(() -> driver.getCurrentUrl().contains("webservicedetail.xhtml?webserviceId=")); 
  }
  
  public static void toSystemConfig(RemoteWebDriver driver)
  {
    toMenu(driver, SYSTEM_CONFIG_MENU);
    WebBase.webAssertThat(() -> driver.getCurrentUrl().endsWith("systemconfig.xhtml"));
  }
  
  public static void toMonitor(RemoteWebDriver driver)
  {
    toMenu(driver, MONITOR_MENU);
    WebBase.webAssertThat(() -> driver.getCurrentUrl().endsWith("monitor.xhtml"));
  }
  
  public static void toLogs(RemoteWebDriver driver)
  {
    toMenu(driver, LOGS_MENU);
    WebBase.webAssertThat(() -> driver.getCurrentUrl().contains("logs.xhtml"));
  }
  
  public static void toEditor(RemoteWebDriver driver)
  {
    toMenu(driver, EDITOR_MENU);
    WebBase.webAssertThat(() -> driver.getCurrentUrl().endsWith("editor.xhtml"));
  }
  
  private static void toMenu(RemoteWebDriver driver, By menuItemPath)
  {
    driver.findElement(menuItemPath).click();
    WebBase.webAssertThat(() -> driver.findElement(menuItemPath).findElement(By.xpath("./..")).
            getAttribute("class").contains("active-menuitem"));
  }
  
  private static void toSubMenu(RemoteWebDriver driver, By menuItemPath, By subMenuItemPath)
  {
    if(!driver.findElement(subMenuItemPath).isDisplayed()) {
      waitBeforeClick(driver, menuItemPath);
    }
    waitBeforeClick(driver, subMenuItemPath);
    WebBase.webAssertThat(() -> driver.findElement(subMenuItemPath).findElement(By.xpath("./..")).
            getAttribute("class").contains("active-menuitem"));
  }
  
  private static boolean checkIfCorrectElement(WebElement element)
  {
    try
    {
      return element.isDisplayed();
    }
    catch (StaleElementReferenceException e)
    {
      return false;
    }
  }
  
  private static void waitBeforeClick(RemoteWebDriver driver, By element)
  {
    WebBase.webAssertThat(() -> driver.findElement(element).isDisplayed());
    driver.findElement(element).click();
  }

}