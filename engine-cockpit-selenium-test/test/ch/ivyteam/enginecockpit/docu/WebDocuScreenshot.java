package ch.ivyteam.enginecockpit.docu;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.setupwizard.WebTestWizardAdmins;
import ch.ivyteam.enginecockpit.setupwizard.WebTestWizardLicence;
import ch.ivyteam.enginecockpit.setupwizard.WebTestWizardWebServer;
import ch.ivyteam.enginecockpit.util.EngineCockpitUrl;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebDocuScreenshot extends WebTestBase
{

  private static final int SCREENSHOT_WIDTH = 1500;
  
  @BeforeEach
  void setupDocuData()
  {
    populateBusinessCalendar(driver);
    runExternalDbQuery(driver);
    createBusinessData(driver);
    addSystemAdmin(driver);
  }
  
  @AfterEach
  void cleanUpDocuData()
  {
    resetConfig(driver);
  }

  @Test
  void docuScreeshot()
  {
    login();
    takeScreenshot("engine-cockpit-dashboard", new Dimension(SCREENSHOT_WIDTH, 800));
    Navigation.toApplications(driver);
    takeScreenshot("engine-cockpit-applications", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toApplicationDetail(driver, EngineCockpitUrl.isDesignerApp() ? "designer" : "test");
    takeScreenshot("engine-cockpit-application-detail", new Dimension(SCREENSHOT_WIDTH, 900));
    Navigation.toSecuritySystem(driver);
    takeScreenshot("engine-cockpit-security-system", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toSecuritySystemDetail(driver, "test-ad");
    takeScreenshot("engine-cockpit-security-system-detail", new Dimension(SCREENSHOT_WIDTH, 900));
    Navigation.toUsers(driver);
    takeScreenshot("engine-cockpit-users", new Dimension(SCREENSHOT_WIDTH, 600));
    Navigation.toUserDetail(driver, "foo");
    takeScreenshot("engine-cockpit-user-detail", new Dimension(SCREENSHOT_WIDTH, 1100));
    Navigation.toRoles(driver);
    takeScreenshot("engine-cockpit-roles", new Dimension(SCREENSHOT_WIDTH, 550));
    Navigation.toRoleDetail(driver, "boss");
    takeScreenshot("engine-cockpit-role-detail", new Dimension(SCREENSHOT_WIDTH, 1000));
    Navigation.toVariables(driver);
    takeScreenshot("engine-cockpit-configuration-variables", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toBusinessCalendar(driver);
    takeScreenshot("engine-cockpit-configuration-businesscalendar", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toBusinessCalendarDetail(driver, "Luzern");
    takeScreenshot("engine-cockpit-configuration-businesscalendar-detail", new Dimension(SCREENSHOT_WIDTH, 750));
    Navigation.toSearchEngine(driver);
    takeScreenshot("engine-cockpit-search-engine", new Dimension(SCREENSHOT_WIDTH, 800));
    Navigation.toEmail(driver);
    takeScreenshot("engine-cockpit-email", new Dimension(SCREENSHOT_WIDTH, 650));
    Navigation.toExternalDatabases(driver);
    takeScreenshot("engine-cockpit-external-databases", new Dimension(SCREENSHOT_WIDTH, 550));
    Navigation.toExternalDatabaseDetail(driver, "realdb");
    takeScreenshot("engine-cockpit-external-database-detail", new Dimension(SCREENSHOT_WIDTH, 800));
    Navigation.toWebservices(driver);
    takeScreenshot("engine-cockpit-webservice", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toWebserviceDetail(driver, "test-web");
    takeScreenshot("engine-cockpit-webservice-detail", new Dimension(SCREENSHOT_WIDTH, 800));
    Navigation.toRestClients(driver);
    takeScreenshot("engine-cockpit-rest-clients", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toRestClientDetail(driver, "test-rest");
    takeScreenshot("engine-cockpit-rest-client-detail", new Dimension(SCREENSHOT_WIDTH, 600));
    Navigation.toAdmins(driver);
    takeScreenshot("engine-cockpit-system-admins", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toSystemDb(driver);
    takeScreenshot("engine-cockpit-system-database", new Dimension(SCREENSHOT_WIDTH, 900));
    Navigation.toSystemConfig(driver);
    takeScreenshot("engine-cockpit-system-config", new Dimension(SCREENSHOT_WIDTH, 700));
    Navigation.toCluster(driver);
    takeScreenshot("engine-cockpit-cluster", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toMonitor(driver);
    takeScreenshot("engine-cockpit-monitor", new Dimension(SCREENSHOT_WIDTH, 1000));
    Navigation.toLogs(driver);
    takeScreenshot("engine-cockpit-logs", new Dimension(SCREENSHOT_WIDTH, 900));
    
    login("setup.xhtml");
    takeScreenshot("engine-cockpit-setup-licence", new Dimension(SCREENSHOT_WIDTH, 550));
    WebTestWizardLicence.skipLicStep(driver);
    takeScreenshot("engine-cockpit-setup-admins", new Dimension(SCREENSHOT_WIDTH, 550));
    WebTestWizardAdmins.skipAdminStep(driver);
    takeScreenshot("engine-cockpit-setup-webserver", new Dimension(SCREENSHOT_WIDTH, 550));
    WebTestWizardWebServer.skipWebserverStep(driver);
    takeScreenshot("engine-cockpit-setup-systemdb", new Dimension(SCREENSHOT_WIDTH, 900));
  }

  public void takeScreenshot(String fileName, Dimension size)
  {
    Dimension oldSize = driver.manage().window().getSize();
    resizeBrowser(size);
    scrollToPosition(0, 0);
    waitForNavigationHighlight(250);
    saveScreenshot(fileName);
    resizeBrowser(oldSize);
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

  @Override
  public void saveScreenshot(String name) 
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

  protected void scrollToPosition(int x, int y)
  {
    driver.executeScript("scroll(" + x + "," + y + ");");
  }

  protected void resizeBrowser(Dimension size)
  {
    driver.manage().window().setSize(size);
  }
  
}
