package ch.ivyteam.enginecockpit.util;

import static org.awaitility.Awaitility.await;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

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
    await().until(() -> driver.getCurrentUrl().endsWith("dashboard.xhtml"));
  }
  
  public static void toApplications(FirefoxDriver driver)
  {
    toMenu(driver, APPLICATIONS_MENU);
    await().until(() -> driver.getCurrentUrl().endsWith("applications.xhtml"));
  }
  
  public static void toApplicationDetail(FirefoxDriver driver, String appName)
  {
    Navigation.toApplications(driver);
    driver.findElementByXPath("//span[@class='activity-name'][text()='" + appName + "']").click();
    await().until(() -> driver.getCurrentUrl().endsWith("application-detail.xhtml?appName=" + appName)); 
  }
  
  public static void toSecuritySystem(FirefoxDriver driver)
  {
    toSubMenu(driver, SECURITY_MENU, SECURITY_SYSTEM_MENU);
    await().until(() -> driver.getCurrentUrl().endsWith("securitysystem.xhtml"));
  }
  
  public static void toSecuritySystemDetail(FirefoxDriver driver, String secSystemName)
  {
    Navigation.toSecuritySystem(driver);
    waitBeforeClick(driver, By.xpath("//span[@class='security-name'][text()='" + secSystemName + "']"));
    await().until(() -> driver.getCurrentUrl().endsWith("security-detail.xhtml?securitySystemName=" + secSystemName)); 
  }

  public static void toVariables(FirefoxDriver driver)
  {
    toSubMenu(driver, CONFIGURATION_MENU, VARIABLES_MENU);
    await().until(() -> driver.getCurrentUrl().endsWith("global-variables.xhtml"));
  }
  
  public static void toBusinessCalendar(FirefoxDriver driver)
  {
    toSubMenu(driver, CONFIGURATION_MENU, BUSINESS_CALENDAR_MENU);
    await().until(() -> driver.getCurrentUrl().endsWith("businesscalendar.xhtml"));
  }
  
  public static void toBusinessCalendarDetail(FirefoxDriver driver, String calendarName)
  {
    Navigation.toBusinessCalendar(driver);
    driver.findElementByXPath("//*[@id=\"calendarTree\"]//a[contains(@id, 'calendarNode')][text()='" + calendarName + "']").click();
    await().until(() -> driver.getCurrentUrl().endsWith("businesscalendar-detail.xhtml?calendarName=" + calendarName));
  }
  
  public static void toUsers(FirefoxDriver driver)
  {
    toSubMenu(driver, SECURITY_MENU, SECURITY_USER_MENU);
    await().until(() -> driver.getCurrentUrl().endsWith("users.xhtml"));
  }
  
  public static void toUserDetail(FirefoxDriver driver, String userName)
  {
    Navigation.toUsers(driver);
    driver.findElementsByXPath("//div[contains(@class, 'ui-tabs-panel')]//span[@class='user-name'][text()='" + userName + "']").stream()
            .filter(e -> checkIfCorrectElement(e))
            .forEach(e -> e.click());
    await().until(() -> driver.getCurrentUrl().endsWith("userdetail.xhtml?userName=" + userName)); 
  }
  
  public static void toRoles(FirefoxDriver driver)
  {
    toSubMenu(driver, SECURITY_MENU, SECURITY_ROLES_MENU);
    await().until(() -> driver.getCurrentUrl().endsWith("roles.xhtml"));
  }
  
  public static void toRoleDetail(FirefoxDriver driver, String roleName)
  {
    Navigation.toRoles(driver);
    driver.findElementsByXPath("//div[contains(@class, 'ui-tabs-panel')]//a[@class='role-name'][text()='" + roleName + "']").stream()
            .filter(e -> checkIfCorrectElement(e))
            .forEach(e -> e.click());
    await().until(() -> driver.getCurrentUrl().endsWith("roledetail.xhtml?roleName=" + roleName)); 
  }
  
  public static void toEmail(FirefoxDriver driver)
  {
    toSubMenu(driver, SERVICES_MENU, SERVICES_EMAIL_MENU);
    await().until(() -> driver.getCurrentUrl().endsWith("email.xhtml"));
  }
  
  public static void toExternalDatabases(FirefoxDriver driver)
  {
    toSubMenu(driver, SERVICES_MENU, SERVICES_DATABASES_MENU);
    await().until(() -> driver.getCurrentUrl().endsWith("externaldatabases.xhtml"));
  }
  
  public static void toExternalDatabaseDetail(FirefoxDriver driver, String databaseName)
  {
    Navigation.toExternalDatabases(driver);
    driver.findElementsByXPath("//div[contains(@class, 'ui-tabs-panel')]//span[@class='database-name'][text()='" + databaseName + "']").stream()
            .filter(e -> checkIfCorrectElement(e))
            .forEach(e -> e.click());
    await().until(() -> driver.getCurrentUrl().endsWith("externaldatabasedetail.xhtml?databaseName=" + databaseName)); 
  }
  
  public static void toRestClients(FirefoxDriver driver)
  {
    toSubMenu(driver, SERVICES_MENU, SERVICES_RESTCLIENTS_MENU);
    await().until(() -> driver.getCurrentUrl().endsWith("restclients.xhtml"));
  }
  
  public static void toRestClientDetail(FirefoxDriver driver, String restClientName)
  {
    Navigation.toRestClients(driver);
    driver.findElementsByXPath("//div[contains(@class, 'ui-tabs-panel')]//span[@class='restclient-name'][text()='" + restClientName + "']").stream()
            .filter(e -> checkIfCorrectElement(e))
            .forEach(e -> e.click());
    await().until(() -> driver.getCurrentUrl().endsWith("restclientdetail.xhtml?restClientName=" + restClientName)); 
  }
  
  public static void toWebservices(FirefoxDriver driver)
  {
    toSubMenu(driver, SERVICES_MENU, SERVICES_WEBSERVICES_MENU);
    await().until(() -> driver.getCurrentUrl().endsWith("webservices.xhtml"));
  }
  
  public static void toWebserviceDetail(FirefoxDriver driver, String webserviceName)
  {
    Navigation.toWebservices(driver);
    driver.findElementsByXPath("//div[contains(@class, 'ui-tabs-panel')]//span[@class='webservice-name'][text()='" + webserviceName + "']").stream()
            .filter(e -> checkIfCorrectElement(e))
            .forEach(e -> e.click());
    await().until(() -> driver.getCurrentUrl().contains("webservicedetail.xhtml?webserviceId=")); 
  }
  
  public static void toSystemConfig(FirefoxDriver driver)
  {
    toMenu(driver, SYSTEM_CONFIG_MENU);
    await().until(() -> driver.getCurrentUrl().endsWith("systemconfig.xhtml"));
  }
  
  public static void toMonitor(FirefoxDriver driver)
  {
    toMenu(driver, MONITOR_MENU);
    await().until(() -> driver.getCurrentUrl().endsWith("monitor.xhtml"));
  }
  
  public static void toLogs(FirefoxDriver driver)
  {
    toMenu(driver, LOGS_MENU);
    await().until(() -> driver.getCurrentUrl().contains("logs.xhtml"));
  }
  
  private static void toMenu(FirefoxDriver driver, By menuItemPath)
  {
    driver.findElement(menuItemPath).click();
    await().until(() -> driver.findElement(menuItemPath).findElement(By.xpath("./..")).
            getAttribute("class").contains("active-menuitem"));
  }
  
  private static void toSubMenu(FirefoxDriver driver, By menuItemPath, By subMenuItemPath)
  {
    if(!driver.findElement(subMenuItemPath).isDisplayed()) {
      waitBeforeClick(driver, menuItemPath);
    }
    waitBeforeClick(driver, subMenuItemPath);
    await().until(() -> driver.findElement(subMenuItemPath).findElement(By.xpath("./..")).
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
  
  private static void waitBeforeClick(FirefoxDriver driver, By element)
  {
    await().until(() -> driver.findElement(element).isDisplayed());
    driver.findElement(element).click();
  }
  
}