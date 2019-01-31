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
    takeScreenshot(driver, "Dashboard", new Dimension(1062, 800));
    Navigation.toApplications(driver);
    takeScreenshot(driver, "Applications", new Dimension(1062, 600));
    Navigation.toApplicationDetail(driver, EngineCockpitUrl.isDesignerApp() ? "designer" : "test");
    takeScreenshot(driver, "Applicationdetail", new Dimension(1062, 800));
    Navigation.toSecuritySystem(driver);
    takeScreenshot(driver, "SecuritySystem", new Dimension(1062, 500));
    Navigation.toUsers(driver);
    takeScreenshot(driver, "Users", new Dimension(1062, 600));
    Navigation.toUserDetail(driver, "foo");
    takeScreenshot(driver, "Userdetail", new Dimension(1062, 1000));
    Navigation.toRoles(driver);
    takeScreenshot(driver, "Roles", new Dimension(1062, 600));
    Navigation.toRoleDetail(driver, "boss");
    takeScreenshot(driver, "Roledetail", new Dimension(1062, 1300));
    Navigation.toEmail(driver);
    takeScreenshot(driver, "Email", new Dimension(1062, 800));
    Navigation.toAdvancedConfig(driver);
    takeScreenshot(driver, "AdvancedConfig", new Dimension(1062, 800));
    Navigation.toMonitor(driver);
    takeScreenshot(driver, "Monitor", new Dimension(1062, 1000));
  }
  
  public void takeScreenshot(RemoteWebDriver driver, String fileName, Dimension size)
  {
    resizeBrowser(driver, size);
    scrollToPosition(driver, 0, 0);
    saveScreenshot(driver, fileName);
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
