package ch.ivyteam.enginecockpit.docu;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUrl.viewUrl;
import static org.awaitility.Awaitility.await;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import ch.ivyteam.enginecockpit.WebBase;
import ch.ivyteam.enginecockpit.util.EngineCockpitUrl;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebDocuScreenshot extends WebBase
{

  private static final int SCREENSHOT_WIDTH = 1500;

  @Test
  void docuScreeshot(FirefoxDriver driver)
  {
    populateBusinessCalendar(driver);
    login(driver);
    takeScreenshot(driver, "engine-cockpit-dashboard", new Dimension(SCREENSHOT_WIDTH, 800));
    Navigation.toApplications(driver);
    takeScreenshot(driver, "engine-cockpit-applications", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toApplicationDetail(driver, EngineCockpitUrl.isDesignerApp() ? "designer" : "test");
    takeScreenshot(driver, "engine-cockpit-application-detail", new Dimension(SCREENSHOT_WIDTH, 900));
    Navigation.toSecuritySystem(driver);
    takeScreenshot(driver, "engine-cockpit-security-system", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toSecuritySystemDetail(driver, "test-ad");
    takeScreenshot(driver, "engine-cockpit-security-system-detail", new Dimension(SCREENSHOT_WIDTH, 900));
    Navigation.toUsers(driver);
    takeScreenshot(driver, "engine-cockpit-users", new Dimension(SCREENSHOT_WIDTH, 600));
    Navigation.toUserDetail(driver, "foo");
    takeScreenshot(driver, "engine-cockpit-user-detail", new Dimension(SCREENSHOT_WIDTH, 1100));
    Navigation.toRoles(driver);
    takeScreenshot(driver, "engine-cockpit-roles", new Dimension(SCREENSHOT_WIDTH, 550));
    Navigation.toRoleDetail(driver, "boss");
    takeScreenshot(driver, "engine-cockpit-role-detail", new Dimension(SCREENSHOT_WIDTH, 1000));
    Navigation.toVariables(driver);
    takeScreenshot(driver, "engine-cockpit-configuration-variables", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toBusinessCalendar(driver);
    takeScreenshot(driver, "engine-cockpit-configuration-businesscalendar", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toBusinessCalendarDetail(driver, "Luzern");
    takeScreenshot(driver, "engine-cockpit-configuration-businesscalendar-detail", new Dimension(SCREENSHOT_WIDTH, 750));
    Navigation.toEmail(driver);
    takeScreenshot(driver, "engine-cockpit-email", new Dimension(SCREENSHOT_WIDTH, 650));
    Navigation.toExternalDatabases(driver);
    takeScreenshot(driver, "engine-cockpit-external-databases", new Dimension(SCREENSHOT_WIDTH, 550));
    Navigation.toExternalDatabaseDetail(driver, "test-db");
    takeScreenshot(driver, "engine-cockpit-external-database-detail", new Dimension(SCREENSHOT_WIDTH, 600));
    Navigation.toWebservices(driver);
    takeScreenshot(driver, "engine-cockpit-webservice", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toWebserviceDetail(driver, "test-web");
    takeScreenshot(driver, "engine-cockpit-webservice-detail", new Dimension(SCREENSHOT_WIDTH, 800));
    Navigation.toRestClients(driver);
    takeScreenshot(driver, "engine-cockpit-rest-clients", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toRestClientDetail(driver, "test-rest");
    takeScreenshot(driver, "engine-cockpit-rest-client-detail", new Dimension(SCREENSHOT_WIDTH, 600));
    Navigation.toSystemConfig(driver);
    takeScreenshot(driver, "engine-cockpit-system-config", new Dimension(SCREENSHOT_WIDTH, 700));
    Navigation.toMonitor(driver);
    waitForNavigationHighlight(2000);
    takeScreenshot(driver, "engine-cockpit-monitor", new Dimension(SCREENSHOT_WIDTH, 1000));
    Navigation.toLogs(driver);
    takeScreenshot(driver, "engine-cockpit-logs", new Dimension(SCREENSHOT_WIDTH, 900));
  }
  
  public void takeScreenshot(RemoteWebDriver driver, String fileName, Dimension size)
  {
    Dimension oldSize = driver.manage().window().getSize();
    resizeBrowser(driver, size);
    scrollToPosition(driver, 0, 0);
    waitForNavigationHighlight(120);
    saveScreenshot(driver, fileName);
    resizeBrowser(driver, oldSize);
  }
  
  private void waitForNavigationHighlight(int millisec)
  {
    try
    {
      Thread.sleep(millisec);
    }
    catch (InterruptedException ex)
    {
      ex.printStackTrace();
    }
  }

  public void saveScreenshot(RemoteWebDriver driver, String name) 
  {
    File source = driver.getScreenshotAs(OutputType.FILE);
    try
    {
      String dir = "target/docu/screenshots/";
      File file = new File(dir, name + ".png");
      if (file.exists())
      {
        file.delete();
      }
      FileUtils.moveFile(source, file);
    }
    catch (IOException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  protected void scrollToPosition(RemoteWebDriver driver, int x, int y)
  {
    driver.executeScript("scroll(" + x + "," + y + ");");
  }

  protected void resizeBrowser(RemoteWebDriver driver, Dimension size)
  {
    driver.manage().window().setSize(size);
  }
  
  public void login(FirefoxDriver driver)
  {
    driver.get(viewUrl("login.xhtml"));
    driver.findElementById("loginForm:userName").sendKeys(getAdminUser());
    driver.findElementById("loginForm:password").sendKeys(getAdminUser());
    driver.findElementById("loginForm:login").click();
    await().until(() -> driver.getCurrentUrl().endsWith("dashboard.xhtml"));
    await().ignoreExceptions().until(() -> driver.findElementById("menuform").isDisplayed());
  }
  
  public static String getAdminUser()
  {
    return EngineCockpitUrl.isDesignerApp() ? "Developer" : "admin";
  }
  
  private void populateBusinessCalendar(FirefoxDriver driver)
  {
    String app = EngineCockpitUrl.isDesignerApp() ? EngineCockpitUrl.DESIGNER_APP : "test";
    driver.get(EngineCockpitUrl.base() + "/pro/" + app + "/engine-cockpit-test-data/16AD3F265FFA55DD/start.ivp");
  }
  
}
