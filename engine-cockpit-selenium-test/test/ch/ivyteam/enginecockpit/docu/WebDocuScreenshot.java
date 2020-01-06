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
import ch.ivyteam.enginecockpit.setupwizard.WebTestWizard;
import ch.ivyteam.enginecockpit.util.EngineCockpitUrl;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebDocuScreenshot extends WebTestBase
{

  private static final int SCREENSHOT_WIDTH = 1500;
  private static final int SCREENSHOT_SETUP_WIDTH = 1200;
  
  @BeforeEach
  void setupDocuData()
  {
    populateBusinessCalendar();
    runExternalDbQuery();
    createBusinessData();
    addSystemAdmin();
  }
  
  @AfterEach
  void cleanUpDocuData()
  {
    resetConfig();
  }

  @Test
  void docuScreeshot()
  {
    login();
    takeScreenshot("engine-cockpit-dashboard", new Dimension(SCREENSHOT_WIDTH, 800));
    Navigation.toApplications();
    takeScreenshot("engine-cockpit-applications", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toApplicationDetail(EngineCockpitUrl.isDesignerApp() ? "designer" : "test");
    takeScreenshot("engine-cockpit-application-detail", new Dimension(SCREENSHOT_WIDTH, 900));
    Navigation.toSecuritySystem();
    takeScreenshot("engine-cockpit-security-system", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toSecuritySystemDetail("test-ad");
    takeScreenshot("engine-cockpit-security-system-detail", new Dimension(SCREENSHOT_WIDTH, 900));
    Navigation.toUsers();
    takeScreenshot("engine-cockpit-users", new Dimension(SCREENSHOT_WIDTH, 600));
    Navigation.toUserDetail("foo");
    takeScreenshot("engine-cockpit-user-detail", new Dimension(SCREENSHOT_WIDTH, 1100));
    Navigation.toRoles();
    takeScreenshot("engine-cockpit-roles", new Dimension(SCREENSHOT_WIDTH, 550));
    Navigation.toRoleDetail("boss");
    takeScreenshot("engine-cockpit-role-detail", new Dimension(SCREENSHOT_WIDTH, 1000));
    Navigation.toVariables();
    takeScreenshot("engine-cockpit-configuration-variables", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toBusinessCalendar();
    takeScreenshot("engine-cockpit-configuration-businesscalendar", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toBusinessCalendarDetail("Luzern");
    takeScreenshot("engine-cockpit-configuration-businesscalendar-detail", new Dimension(SCREENSHOT_WIDTH, 750));
    Navigation.toSearchEngine();
    takeScreenshot("engine-cockpit-search-engine", new Dimension(SCREENSHOT_WIDTH, 800));
    Navigation.toEmail();
    takeScreenshot("engine-cockpit-email", new Dimension(SCREENSHOT_WIDTH, 650));
    Navigation.toExternalDatabases();
    takeScreenshot("engine-cockpit-external-databases", new Dimension(SCREENSHOT_WIDTH, 550));
    Navigation.toExternalDatabaseDetail("realdb");
    takeScreenshot("engine-cockpit-external-database-detail", new Dimension(SCREENSHOT_WIDTH, 800));
    Navigation.toWebservices();
    takeScreenshot("engine-cockpit-webservice", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toWebserviceDetail("test-web");
    takeScreenshot("engine-cockpit-webservice-detail", new Dimension(SCREENSHOT_WIDTH, 800));
    Navigation.toRestClients();
    takeScreenshot("engine-cockpit-rest-clients", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toRestClientDetail("test-rest");
    takeScreenshot("engine-cockpit-rest-client-detail", new Dimension(SCREENSHOT_WIDTH, 600));
    Navigation.toAdmins();
    takeScreenshot("engine-cockpit-system-admins", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toSystemDb();
    takeScreenshot("engine-cockpit-system-database", new Dimension(SCREENSHOT_WIDTH, 900));
    Navigation.toSystemConfig();
    takeScreenshot("engine-cockpit-system-config", new Dimension(SCREENSHOT_WIDTH, 700));
    Navigation.toCluster();
    takeScreenshot("engine-cockpit-cluster", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toResourcesMonitor();
    takeScreenshot("engine-cockpit-monitor", new Dimension(SCREENSHOT_WIDTH, 1000));
    Navigation.toLogs();
    takeScreenshot("engine-cockpit-logs", new Dimension(SCREENSHOT_WIDTH, 900));
    
    WebTestWizard.navigateToStep("Licence");
    takeScreenshot("engine-cockpit-setup-licence", new Dimension(SCREENSHOT_SETUP_WIDTH, 550));
    WebTestWizard.navigateToStep("Administrators");
    takeScreenshot("engine-cockpit-setup-admins", new Dimension(SCREENSHOT_SETUP_WIDTH, 550));
    WebTestWizard.navigateToStep("Web Server");
    takeScreenshot("engine-cockpit-setup-webserver", new Dimension(SCREENSHOT_SETUP_WIDTH, 550));
    WebTestWizard.navigateToStep("System Database");
    takeScreenshot("engine-cockpit-setup-systemdb", new Dimension(SCREENSHOT_SETUP_WIDTH, 900));
  }

  public void takeScreenshot(String fileName, Dimension size)
  {
    Dimension oldSize = driver.manage().window().getSize();
    resizeBrowser(size);
    scrollToPosition(0, 0);
    saveDocuScreenshot(fileName);
    resizeBrowser(oldSize);
  }
  
  public void saveDocuScreenshot(String name) 
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
