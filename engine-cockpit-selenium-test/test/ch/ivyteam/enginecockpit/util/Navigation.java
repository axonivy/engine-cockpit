package ch.ivyteam.enginecockpit.util;

import org.openqa.selenium.By;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Navigation
{
  private static final ByXPath DASHBOARD_MENU = new By.ByXPath("//li[@id='menuform:sr_dashboard']/child::a");
  private static final ByXPath SECURITY_MENU = new By.ByXPath("//li[@id='menuform:sr_security']/child::a");
  private static final ByXPath SECURITY_SYSTEM_MENU = new By.ByXPath("//li[@id='menuform:sr_security_system']/child::a");
  private static final ByXPath SECURITY_USER_MENU = new By.ByXPath("//li[@id='menuform:sr_users']/child::a");
  private static final ByXPath SECURITY_ROLES_MENU = new By.ByXPath("//li[@id='menuform:sr_roles']/child::a");
  private static final ByXPath ADVANCED_CONFIG_MENU = new By.ByXPath("//li[@id='menuform:sr_advanced_config']/child::a");

  public static void toDashboard(FirefoxDriver driver)
  {
    toMenu(driver, DASHBOARD_MENU);
  }
  
  public static void toSecuritySystem(FirefoxDriver driver)
  {
    toSubMenu(driver, SECURITY_MENU, SECURITY_SYSTEM_MENU);
  }
  
  public static void toUsers(FirefoxDriver driver)
  {
    toSubMenu(driver, SECURITY_MENU, SECURITY_USER_MENU);
  }
  
  public static void toRoles(FirefoxDriver driver)
  {
    toSubMenu(driver, SECURITY_MENU, SECURITY_ROLES_MENU);
  }
  
  public static void toAdvancedConfig(FirefoxDriver driver)
  {
    toMenu(driver, ADVANCED_CONFIG_MENU);
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