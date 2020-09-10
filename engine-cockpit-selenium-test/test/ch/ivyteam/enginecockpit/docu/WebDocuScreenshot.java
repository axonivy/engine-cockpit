package ch.ivyteam.enginecockpit.docu;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.executeJs;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.axonivy.ivy.webtest.engine.EngineUrl.DESIGNER;
import static com.axonivy.ivy.webtest.engine.EngineUrl.isDesigner;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;

import ch.ivyteam.enginecockpit.monitor.WebTestMBeans;
import ch.ivyteam.enginecockpit.setupwizard.WebTestWizard;
import ch.ivyteam.enginecockpit.system.WebTestSystemDb;
import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
public class WebDocuScreenshot
{

  private static final int SCREENSHOT_WIDTH = 1500;
  private static final int SCREENSHOT_SETUP_WIDTH = 1200;
  
  @BeforeAll
  static void setup()
  {
    EngineCockpitUtil.populateBusinessCalendar();
    EngineCockpitUtil.runExternalDbQuery();
    EngineCockpitUtil.createBusinessData();
    EngineCockpitUtil.addSystemAdmin();
  }
  
  @BeforeEach
  void beforeEach()
  {
    Configuration.reportsFolder = "target/docu/screenshots/";
    Configuration.savePageSource = false;
  }
  
  @AfterAll
  static void cleanUp()
  {
    EngineCockpitUtil.resetConfig();
  }

  @Test
  void docuScreeshot()
  {
    login();
    takeScreenshot("engine-cockpit-dashboard", new Dimension(SCREENSHOT_WIDTH, 800));
    takeDialogScreenshot("engine-cockpit-dialog-test-mail", By.id("mailConfigForm:openTestMailBtn"));
    Navigation.toApplications();
    takeScreenshot("engine-cockpit-applications", new Dimension(SCREENSHOT_WIDTH, 500));
    takeDialogScreenshot("engine-cockpit-dialog-new-app", By.id("card:form:createApplicationBtn"));
    Navigation.toPmvDetail(isDesigner() ? DESIGNER : "demo-portal", "PortalTemplate", "PortalTemplate$1");
    takeScreenshot("engine-cockpit-pmv-detail", new Dimension(SCREENSHOT_WIDTH, 1100));
    Navigation.toApplicationDetail(isDesigner() ? DESIGNER : "test");
    takeScreenshot("engine-cockpit-application-detail", new Dimension(SCREENSHOT_WIDTH, 900));
    takeDialogScreenshot("engine-cockpit-dialog-change-security", By.id("appDetailSecurityForm:changeSecuritySystem"));
    takeDialogScreenshot("engine-cockpit-dialog-deploy-app", By.id("appDetailInfoForm:showDeployment"));
    Navigation.toSecuritySystem();
    takeScreenshot("engine-cockpit-security-system", new Dimension(SCREENSHOT_WIDTH, 500));
    takeDialogScreenshot("engine-cockpit-dialog-new-security", By.id("card:form:createSecuritySystemBtn"));
    Navigation.toSecuritySystemDetail("test-ad");
    takeScreenshot("engine-cockpit-security-system-detail", new Dimension(SCREENSHOT_WIDTH, 900));
    takeDialogScreenshot("engine-cockpit-dialog-ldap-browser", By.id("securitySystemBindingForm:browseDefaultContext"));
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
    takeScreenshot("engine-cockpit-configuration-businesscalendar-detail", new Dimension(SCREENSHOT_WIDTH, 550));
    Navigation.toSearchEngine();
    takeScreenshot("engine-cockpit-search-engine", new Dimension(SCREENSHOT_WIDTH, 800));
    Navigation.toEmail();
    takeScreenshot("engine-cockpit-email", new Dimension(SCREENSHOT_WIDTH, 650));
    Navigation.toExternalDatabases();
    takeScreenshot("engine-cockpit-external-databases", new Dimension(SCREENSHOT_WIDTH, 550));
    Navigation.toExternalDatabaseDetail("realdb");
    takeScreenshot("engine-cockpit-external-database-detail", new Dimension(SCREENSHOT_WIDTH, 800));
    takeLiveStatsScreenshot("engine-cockpit-monitor-external-databases", new Dimension(SCREENSHOT_WIDTH, 800));
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
    takeDialogScreenshot("engine-cockpit-dialog-new-admin", By.id("addAdminForm:newAdminBtn"));
    Navigation.toSystemDb();
    takeScreenshot("engine-cockpit-system-database", new Dimension(SCREENSHOT_WIDTH, 900));
    Navigation.toLicence();
    takeScreenshot("engine-cockpit-licence", new Dimension(SCREENSHOT_WIDTH, 700));
    Navigation.toSystemConfig();
    takeScreenshot("engine-cockpit-system-config", new Dimension(SCREENSHOT_WIDTH, 700));
    Navigation.toCluster();
    takeScreenshot("engine-cockpit-cluster", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toLogs();
    takeScreenshot("engine-cockpit-monitor-logs", new Dimension(SCREENSHOT_WIDTH, 900));
    WebTestMBeans.toMBeans();
    takeScreenshot("engine-cockpit-monitor-mbeans", new Dimension(SCREENSHOT_WIDTH, 1000));
    Navigation.toJvm();
    takeScreenshot("engine-cockpit-monitor-jvm", new Dimension(SCREENSHOT_WIDTH, 1000));
    Navigation.toOs();
    takeScreenshot("engine-cockpit-monitor-os", new Dimension(SCREENSHOT_WIDTH, 1000));
    WebTestWizard.navigateToStep("Licence");
    takeScreenshot("engine-cockpit-setup-licence", new Dimension(SCREENSHOT_SETUP_WIDTH, 550));
    WebTestWizard.navigateToStep("Administrators");
    takeScreenshot("engine-cockpit-setup-admins", new Dimension(SCREENSHOT_SETUP_WIDTH, 550));
    WebTestWizard.navigateToStep("Web Server");
    takeScreenshot("engine-cockpit-setup-webserver", new Dimension(SCREENSHOT_SETUP_WIDTH, 550));
    WebTestWizard.navigateToStep("System Database");
    WebTestSystemDb.assertSystemDbCreationDialog();
    takeDialogScreenshot("engine-cockpit-dialog-setup-systemdb-create");
    takeScreenshot("engine-cockpit-setup-systemdb", new Dimension(SCREENSHOT_SETUP_WIDTH, 900));
    takeDialogScreenshot("engine-cockpit-dialog-setup-finish", By.id("finishWizard"));
  }

  private void takeDialogScreenshot(String screenshotName)
  {
    SelenideElement dialog = $$(".ui-dialog").find(visible);
    Rectangle dialogRect = dialog.getRect();
    Point dialogCoordiantes = dialog.getCoordinates().inViewPort();
    File dialogFile = new File(Selenide.screenshot(screenshotName));
    try
    {
      BufferedImage dialogScreenshot = ImageIO.read(dialogFile).getSubimage(
              dialogCoordiantes.getX(), 
              dialogCoordiantes.getY(), 
              dialogRect.getWidth(), 
              dialogRect.getHeight() + 1);
      ImageIO.write(dialogScreenshot, "png", dialogFile);
    }
    catch (IOException ex)
    {
      throw new RuntimeException("Error while try crop screenshot to dialog size: ", ex);
    }
    $$(".ui-dialog-titlebar-close").find(visible).click();
  }

  private void takeDialogScreenshot(String screenshotName, By dialogOpenBtn)
  {
    $(dialogOpenBtn).shouldBe(visible).click();
    takeDialogScreenshot(screenshotName);
  }

  private void takeScreenshot(String fileName, Dimension size)
  {
    Dimension oldSize = WebDriverRunner.getWebDriver().manage().window().getSize();
    resizeBrowser(size);
    executeJs("scroll(0,0);");
    Selenide.sleep(200); //wait for menu animation
    Selenide.screenshot(fileName);
    resizeBrowser(oldSize);
  }
  
  private void takeLiveStatsScreenshot(String fileName, Dimension size)
  {
    $("#layout-config-button").shouldBe(visible).click();
    $("#layout-config .ui-tabs-selected").shouldBe(visible, text("Live Stats"));
    takeScreenshot(fileName, size);
  }
  
  private void resizeBrowser(Dimension size)
  {
    WebDriverRunner.getWebDriver().manage().window().setSize(size);
  }
  
}
