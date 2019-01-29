package ch.ivyteam.enginecockpit.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Navigation
{
  private static final ByXPath DASHBOARD_MENU = new By.ByXPath("//li[@id='menuform:sr_dashboard']/child::a");
  private static final ByXPath APPLICATIONS_MENU = new By.ByXPath("//li[@id='menuform:sr_applications']/child::a");
  private static final ByXPath SECURITY_MENU = new By.ByXPath("//li[@id='menuform:sr_security']/child::a");
  private static final ByXPath SECURITY_SYSTEM_MENU = new By.ByXPath("//li[@id='menuform:sr_security_system']/child::a");
  private static final ByXPath SECURITY_USER_MENU = new By.ByXPath("//li[@id='menuform:sr_users']/child::a");
  private static final ByXPath SECURITY_ROLES_MENU = new By.ByXPath("//li[@id='menuform:sr_roles']/child::a");
  private static final ByXPath ADVANCED_CONFIG_MENU = new By.ByXPath("//li[@id='menuform:sr_advanced_config']/child::a");
  private static final ByXPath MONITOR_MENU = new By.ByXPath("//li[@id='menuform:sr_monitor']/child::a");

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
  
  public static void toSecuritySystem(FirefoxDriver driver)
  {
    toSubMenu(driver, SECURITY_MENU, SECURITY_SYSTEM_MENU);
    await().until(() -> driver.getCurrentUrl().endsWith("securitysystem.xhtml"));
  }
  
  public static void toUsers(FirefoxDriver driver)
  {
    toSubMenu(driver, SECURITY_MENU, SECURITY_USER_MENU);
    await().until(() -> driver.getCurrentUrl().endsWith("users.xhtml"));
  }
  
  public static void toUserDetail(FirefoxDriver driver, String userName)
  {
    Navigation.toUsers(driver);
    Optional<WebElement> user = driver.findElements(new By.ByXPath(("//div[contains(@class, 'ui-tabs-panel')]//*[@class='user-name']")))
            .stream()
            .filter(e -> e.getText().equals(userName)).findAny();
    assertThat(user).isPresent();
    user.get().click();
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
    Optional<WebElement> role = driver.findElements(new By.ByXPath(("//div[contains(@class, 'ui-tabs-panel')]//*[@class='role-name']")))
            .stream()
            .filter(e -> e.getText().startsWith(roleName)).findAny();
    assertThat(role).isPresent();
    role.get().click();
    await().until(() -> driver.getCurrentUrl().endsWith("roledetail.xhtml?roleName=" + roleName)); 
  }
  
  public static void toAdvancedConfig(FirefoxDriver driver)
  {
    toMenu(driver, ADVANCED_CONFIG_MENU);
    await().until(() -> driver.getCurrentUrl().endsWith("advancedconfig.xhtml"));
  }
  
  public static void toMonitor(FirefoxDriver driver)
  {
    toMenu(driver, MONITOR_MENU);
    await().until(() -> driver.getCurrentUrl().endsWith("monitor.xhtml"));
  }
  
  private static void toMenu(FirefoxDriver driver, ByXPath menuItemPath)
  {
    driver.findElement(menuItemPath).click();
  }
  
  private static void toSubMenu(FirefoxDriver driver, ByXPath menuItemPath, ByXPath subMenuItemPath)
  {
    WebElement menuItem = driver.findElement(menuItemPath);
    WebElement subMenuItem = driver.findElement(subMenuItemPath);
    if(!subMenuItem.isDisplayed()) {
      menuItem.click();
    }
    subMenuItem.click();
  }
  
}