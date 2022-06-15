package ch.ivyteam.enginecockpit.docu;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.executeJs;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.axonivy.ivy.webtest.engine.EngineUrl.DESIGNER;
import static com.axonivy.ivy.webtest.engine.EngineUrl.isDesigner;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;

import ch.ivyteam.enginecockpit.monitor.WebTestMBeans;
import ch.ivyteam.enginecockpit.monitor.WebTestProcessExecution;
import ch.ivyteam.enginecockpit.monitor.WebTestSlowRequests;
import ch.ivyteam.enginecockpit.monitor.WebTestTrafficGraph;
import ch.ivyteam.enginecockpit.setup.WebTestWizard;
import ch.ivyteam.enginecockpit.system.WebTestSystemDb;
import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

@IvyWebTest
public class WebDocuScreenshot {

  private static final int SCREENSHOT_WIDTH = 1500;
  private static final int SCREENSHOT_SETUP_WIDTH = 1200;

  @BeforeAll
  static void setup() {
    EngineCockpitUtil.populateBusinessCalendar();
    EngineCockpitUtil.runExternalDbQuery();
    EngineCockpitUtil.createBusinessData();
    EngineCockpitUtil.addSystemAdmin();
  }

  @BeforeEach
  void beforeEach() {
    Configuration.reportsFolder = "target/docu/screenshots/";
    Configuration.savePageSource = false;
    login();
  }

  @AfterAll
  static void cleanUp() {
    EngineCockpitUtil.resetConfig();
  }

  @Test
  void screenshotSetup() {
    login("setup-intro.xhtml");
    takeScreenshot("setup-intro", new Dimension(SCREENSHOT_SETUP_WIDTH, 600));

    login("migrate.xhtml");
    takeScreenshot("migrate", new Dimension(SCREENSHOT_SETUP_WIDTH, 550));

    WebTestWizard.navigateToStep("Licence");
    takeScreenshot("setup-licence", new Dimension(SCREENSHOT_SETUP_WIDTH, 550));
    WebTestWizard.navigateToStep("Administrators");
    takeScreenshot("setup-admins", new Dimension(SCREENSHOT_SETUP_WIDTH, 550));
    WebTestWizard.navigateToStep("Web Server");
    takeScreenshot("setup-webserver", new Dimension(SCREENSHOT_SETUP_WIDTH, 550));
    WebTestWizard.navigateToStep("Storage");
    takeScreenshot("setup-storage", new Dimension(SCREENSHOT_SETUP_WIDTH, 550));
    WebTestWizard.navigateToStep("System Database");
    WebTestSystemDb.assertSystemDbCreationDialog();
    takeDialogScreenshot("dialog-setup-systemdb-create");
    takeScreenshot("setup-systemdb", new Dimension(SCREENSHOT_SETUP_WIDTH, 900));
    takeDialogScreenshot("dialog-setup-finish", By.id("finishWizard"));
  }

  @Test
  void screenshotMonitor() {
    Navigation.toLogs();
    takeScreenshot("monitor-logs", new Dimension(SCREENSHOT_WIDTH, 900));
    WebTestMBeans.toMBeans();
    takeScreenshot("monitor-mbeans", new Dimension(SCREENSHOT_WIDTH, 1000));
    Navigation.toJvm();
    takeScreenshot("monitor-jvm", new Dimension(SCREENSHOT_WIDTH, 1000));
    Navigation.toOs();
    takeScreenshot("monitor-os", new Dimension(SCREENSHOT_WIDTH, 1000));
    Navigation.toCache();
    takeScreenshot("monitor-cache", new Dimension(SCREENSHOT_WIDTH, 1000));
    WebTestProcessExecution.prepareScreenshot();
    takeScreenshot("monitor-performance", new Dimension(SCREENSHOT_WIDTH, 800));
    WebTestSlowRequests.prepareScreenshot();
    takeScreenshot("monitor-slow-requests", new Dimension(SCREENSHOT_WIDTH, 800));
    WebTestTrafficGraph.prepareScreenshot();
    takeScreenshot("monitor-system-overview", new Dimension(SCREENSHOT_WIDTH, 800));
  }

  @Test
  void screenshotSystem() {
    Navigation.toAdmins();
    takeScreenshot("system-admins", new Dimension(SCREENSHOT_WIDTH, 500));
    takeDialogScreenshot("dialog-new-admin", By.id("addAdminForm:newAdminBtn"));
    Navigation.toSystemDb();
    takeScreenshot("system-database", new Dimension(SCREENSHOT_WIDTH, 900));
    Navigation.toLicence();
    takeScreenshot("licence", new Dimension(SCREENSHOT_WIDTH, 700));
    Navigation.toWebServer();
    takeScreenshot("web-server", new Dimension(SCREENSHOT_WIDTH, 600));
    Navigation.toSystemConfig();
    takeScreenshot("system-config", new Dimension(SCREENSHOT_WIDTH, 700));
    Navigation.toCluster();
    takeScreenshot("cluster", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toEditor();
    takeScreenshot("yaml-editor", new Dimension(SCREENSHOT_WIDTH, 700));
  }

  @Test
  void screenshotServices() {
    Navigation.toEmail();
    Tab.SECURITY_SYSTEM.switchToDefault();
    takeScreenshot("email", new Dimension(SCREENSHOT_WIDTH, 650));

    Navigation.toDatabases();
    Tab.APP.switchToDefault();
    takeScreenshot("databases", new Dimension(SCREENSHOT_WIDTH, 550));
    Navigation.toDatabaseDetail("realdb");
    takeScreenshot("database-detail", new Dimension(SCREENSHOT_WIDTH, 800));
    takeLiveStatsScreenshot("monitor-databases", new Dimension(SCREENSHOT_WIDTH, 800));
    Navigation.toWebservices();
    takeScreenshot("webservice", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toWebserviceDetail("test-web");
    takeScreenshot("webservice-detail", new Dimension(SCREENSHOT_WIDTH, 800));
    Navigation.toRestClients();
    takeScreenshot("rest-clients", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toRestClientDetail("test-rest");
    takeScreenshot("rest-client-detail", new Dimension(SCREENSHOT_WIDTH, 600));
    Navigation.toSearchEngine();
    takeScreenshot("search-engine", new Dimension(SCREENSHOT_WIDTH, 800));
  }

  @Test
  void screenshotConfiguration() {
    Navigation.toVariables();
    Tab.APP.switchToDefault();
    takeScreenshot("configuration-variables", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toBusinessCalendar();
    takeScreenshot("configuration-businesscalendar", new Dimension(SCREENSHOT_WIDTH, 500));
    Navigation.toBusinessCalendarDetail("Luzern");
    takeScreenshot("configuration-businesscalendar-detail",
            new Dimension(SCREENSHOT_WIDTH, 550));
    Navigation.toBranding();
    takeScreenshot("branding", new Dimension(SCREENSHOT_WIDTH, 800));
    takeDialogScreenshot("branding-custom-css", By.id("apps:applicationTabView:" + Tab.APP.getSelectedTabIndex() + ":form:editCustomCssBtn"));
  }

  @Test
  void screenshotSecuritySystem() {
    Navigation.toSecuritySystem();
    takeScreenshot("security-system", new Dimension(SCREENSHOT_WIDTH, 500));
    takeDialogScreenshot("dialog-new-security", By.id("form:createSecuritySystemBtn"));
    Navigation.toSecuritySystemDetail("test-ad");
    takeScreenshot("security-system-detail", new Dimension(SCREENSHOT_WIDTH, 900));
    takeDialogScreenshot("dialog-ldap-browser",
            By.id("securitySystemBindingForm:browseDefaultContext"));
    Navigation.toUsers();
    Tab.SECURITY_SYSTEM.switchToDefault();
    takeScreenshot("users", new Dimension(SCREENSHOT_WIDTH, 600));
    Navigation.toUserDetail("foo");
    takeScreenshot("user-detail", new Dimension(SCREENSHOT_WIDTH, 1100));
    Navigation.toRoles();
    takeScreenshot("roles", new Dimension(SCREENSHOT_WIDTH, 550));
    Navigation.toRoleDetail("boss");
    takeScreenshot("role-detail", new Dimension(SCREENSHOT_WIDTH, 1000));
  }

  @Test
  void screenshotApplications() {
    Navigation.toApplications();
    takeScreenshot("applications", new Dimension(SCREENSHOT_WIDTH, 500));
    takeDialogScreenshot("dialog-new-app", By.id("form:createApplicationBtn"));
    Navigation.toPmvDetail(isDesigner() ? DESIGNER : "demo-portal", "PortalTemplate", "PortalTemplate$1");
    takeScreenshot("pmv-detail", new Dimension(SCREENSHOT_WIDTH, 1100));
    Navigation.toApplicationDetail(isDesigner() ? DESIGNER : "test");
    takeScreenshot("application-detail", new Dimension(SCREENSHOT_WIDTH, 900));
    takeDialogScreenshot("dialog-deploy-app", By.id("appDetailInfoForm:showDeployment"));
  }

  @Test
  void screenshotDashboard() {
    takeScreenshot("dashboard", new Dimension(SCREENSHOT_WIDTH, 800));
    takeDialogScreenshot("dialog-test-mail", By.id("openTestMailBtn"));
  }

  private void takeDialogScreenshot(String screenshotName, By dialogOpenBtn) {
    Dimension oldSize = WebDriverRunner.getWebDriver().manage().window().getSize();
    resizeBrowser(new Dimension(SCREENSHOT_WIDTH, 2000));
    executeJs("scroll(0,0);");
    $(dialogOpenBtn).shouldBe(visible).click();
    takeDialogScreenshot(screenshotName);
    resizeBrowser(oldSize);
  }

  private void takeDialogScreenshot(String screenshotName) {
    var dialogScreenshot = $$(".ui-dialog").find(visible).screenshot().toPath();
    var screenshot = dialogScreenshot.getParent().resolve("engine-cockpit-" + screenshotName + ".png");
    try {
      Files.copy(dialogScreenshot, screenshot);
      Files.delete(dialogScreenshot);
    } catch (IOException ex) {
      throw new RuntimeException("Error while try crop screenshot to dialog size: ", ex);
    }
    $$(".ui-dialog-titlebar-close").find(visible).click();
  }

  private void takeLiveStatsScreenshot(String fileName, Dimension size) {
    $("#layout-config-button").shouldBe(visible).click();
    $(".layout-config h3").shouldBe(visible, text("Live Stats"));
    takeScreenshot(fileName, size);
  }

  private void takeScreenshot(String fileName, Dimension size) {
    Dimension oldSize = WebDriverRunner.getWebDriver().manage().window().getSize();
    resizeBrowser(size);
    executeJs("scroll(0,0);");
    Selenide.sleep(200); // wait for menu animation
    Selenide.screenshot("engine-cockpit-" + fileName);
    resizeBrowser(oldSize);
  }

  private void resizeBrowser(Dimension size) {
    WebDriverRunner.getWebDriver().manage().window().setSize(size);
  }

}
