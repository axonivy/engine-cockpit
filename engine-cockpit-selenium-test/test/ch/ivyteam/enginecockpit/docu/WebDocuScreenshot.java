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

  @Test
  void docuScreeshot(FirefoxDriver driver)
  {
    login(driver);
    takeScreenshot(driver, "engine-cockpit-dashboard", new Dimension(1062, 800));
    Navigation.toApplications(driver);
    takeScreenshot(driver, "engine-cockpit-applications", new Dimension(1062, 600));
    Navigation.toApplicationDetail(driver, EngineCockpitUrl.isDesignerApp() ? "designer" : "test");
    takeScreenshot(driver, "engine-cockpit-application-detail", new Dimension(1062, 900));
    Navigation.toSecuritySystem(driver);
    takeScreenshot(driver, "engine-cockpit-security-system", new Dimension(1062, 500));
    Navigation.toSecuritySystemDetail(driver, "test-ad");
    takeScreenshot(driver, "engine-cockpit-security-system-detail", new Dimension(1062, 900));
    Navigation.toUsers(driver);
    takeScreenshot(driver, "engine-cockpit-users", new Dimension(1062, 600));
    Navigation.toUserDetail(driver, "foo");
    takeScreenshot(driver, "engine-cockpit-user-detail", new Dimension(1062, 1000));
    Navigation.toRoles(driver);
    takeScreenshot(driver, "engine-cockpit-roles", new Dimension(1062, 600));
    Navigation.toRoleDetail(driver, "boss");
    takeScreenshot(driver, "engine-cockpit-role-detail", new Dimension(1062, 900));
    Navigation.toVariables(driver);
    takeScreenshot(driver, "engine-cockpit-configuration-variables", new Dimension(1062, 900));
    Navigation.toEmail(driver);
    takeScreenshot(driver, "engine-cockpit-email", new Dimension(1062, 800));
    Navigation.toExternalDatabases(driver);
    takeScreenshot(driver, "engine-cockpit-external-databases", new Dimension(1062, 500));
    Navigation.toExternalDatabaseDetail(driver, "test-db");
    takeScreenshot(driver, "engine-cockpit-external-database-detail", new Dimension(1062, 800));
    Navigation.toWebservices(driver);
    takeScreenshot(driver, "engine-cockpit-webservice", new Dimension(1062, 500));
    Navigation.toWebserviceDetail(driver, "test-web");
    takeScreenshot(driver, "engine-cockpit-webservice-detail", new Dimension(1062, 900));
    Navigation.toRestClients(driver);
    takeScreenshot(driver, "engine-cockpit-rest-clients", new Dimension(1062, 500));
    Navigation.toRestClientDetail(driver, "test-rest");
    takeScreenshot(driver, "engine-cockpit-rest-client-detail", new Dimension(1062, 800));
    Navigation.toSystemConfig(driver);
    takeScreenshot(driver, "engine-cockpit-system-config", new Dimension(1062, 700));
    Navigation.toMonitor(driver);
    takeScreenshot(driver, "engine-cockpit-monitor", new Dimension(1062, 1000));
    Navigation.toLogs(driver);
    takeScreenshot(driver, "engine-cockpit-logs", new Dimension(1062, 1000));
  }
  
  public void takeScreenshot(RemoteWebDriver driver, String fileName, Dimension size)
  {
    Dimension oldSize = driver.manage().window().getSize();
    resizeBrowser(driver, size);
    scrollToPosition(driver, 0, 0);
    saveScreenshot(driver, fileName);
    resizeBrowser(driver, oldSize);
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
  
}
