package ch.ivyteam.enginecockpit.util;

import static com.axonivy.ivy.webtest.engine.EngineUrl.DESIGNER;
import static com.axonivy.ivy.webtest.engine.EngineUrl.create;
import static com.axonivy.ivy.webtest.engine.EngineUrl.isDesigner;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.axonivy.ivy.webtest.engine.EngineUrl.SERVLET;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;

public class EngineCockpitUtil {
  public static String DASHBOARD = "dashboard.xhtml";
  public static String LOGIN = "login.xhtml";

  public static String getAdminUser() {
    return isDesigner() ? "Developer" : "admin";
  }

  public static void login() {
    login(DASHBOARD);
  }

  public static void login(String url) {
    login(url, getAdminUser(), getAdminUser());
  }

  public static void login(String url, String username, String password) {
    open(viewUrl(url));
    if (webdriver().driver().url().endsWith(LOGIN)) {
      loginUser(username, password);
    }
    $("#menuform").shouldBe(visible);
    assertCurrentUrlContains(LOGIN.equals(url) ? DASHBOARD : url);
  }

  public static void forceLogin() {
    forceLogin(getAdminUser(), getAdminUser());
  }

  public static void forceLogin(String username, String password) {
    open(viewUrl(LOGIN));
    loginUser(username, password);
    open(viewUrl(DASHBOARD));
    assertCurrentUrlContains(DASHBOARD);
  }

  public static void loginUser(String username, String password) {
    $("h4").shouldHave(text("Engine Cockpit"));
    $("#loginForm\\:userName").sendKeys(username);
    $("#loginForm\\:password").sendKeys(password);
    $("#loginForm\\:login").click();
  }

  public static void openDashboard() {
    open(viewUrl(DASHBOARD));
  }

  public static void waitUntilAjaxIsFinished() {
    $("#ajaxLoadingStatus_start").shouldNotBe(visible);
  }

  public static void assertCurrentUrlContains(String contains) {
    webdriver().shouldHave(urlContaining(contains));
  }

  public static void executeJs(String js) {
    ((RemoteWebDriver) WebDriverRunner.getWebDriver()).executeScript(js);
  }

  public static String escapeSelector(String selector) {
    return "#" + selector.replace(":", "\\:");
  }

  public static void addSystemAdmin() {
    runTestProcess("16E88DD61E825E70/addAdministrator.ivp");
  }

  public static void populateBusinessCalendar() {
    runTestProcess("16E88DD61E825E70/createBusinessCalendar.ivp");
  }

  public static void runExternalDbQuery() {
    runTestProcess("16E88DD61E825E70/runDbExecution.ivp");
  }

  public static void runRestClient() {
    runTestProcess("16E88DD61E825E70/executeRest.ivp");
  }

  public static void runWebService() {
    runTestProcess("16E88DD61E825E70/executeWebService.ivp");
  }

  public static void createBusinessData() {
    runTestProcess("16E88DD61E825E70/createBusinessData.ivp");
  }

  public static void createManyDynamicRoles() {
    runTestProcess("16E88DD61E825E70/createManyDynamicRoles.ivp");
  }

  public static void registerDummyIdentityProvider() {
    runTestProcess("18988002A06A4B50/registerDummyIdentityProvider.ivp");
  }

  public static void cleanupDynamicRoles() {
    runTestProcess("16E88DD61E825E70/cleanupDynamicRoles.ivp");
  }

  public static void createLicenceEvents() {
    runTestProcess("16E84204B7FE6C91/addLicenceEvents.ivp");
  }

  public static void resetLicence() {
    runTestProcess("16E84204B7FE6C91/resetLicence.ivp");
  }

  public static void resetConfig() {
    runTestProcess("16E881C7DC458C7D/cleanupAdmins.ivp");
    runTestProcess("16E881C7DC458C7D/cleanupConnectors.ivp");
    runTestProcess("16E881C7DC458C7D/cleanupSystemDb.ivp");
    runTestProcess("16E881C7DC458C7D/cleanupDataDirs.ivp");
  }

  public static void resetNotificationConfig() {
    runTestProcess("16E881C7DC458C7D/cleanupNotification.ivp");
  }

  public static void disableRestServlet() {
    runTestProcess("16E881C7DC458C7D/disableRestServlet.ivp");
  }

  public static void enableRestServlet() {
    runTestProcess("16E881C7DC458C7D/enableRestServlet.ivp");
  }

  public static void createOldDb() {
    runTestProcess("16E8EAD7CC77A0A3/createOldDatabase.ivp");
  }

  public static void deleteOldDb() {
    runTestProcess("16E8EAD7CC77A0A3/deleteOldDatabase.ivp");
  }

  public static void deleteTempDb() {
    runTestProcess("16E8EAD7CC77A0A3/deleteTempDatabase.ivp");
  }

  public static void createDisabledUser() {
    runTestProcess("16E88DD61E825E70/createDisabledUser.ivp");
  }

  public static void performanceData() {
    runTestProcess("17B77E4EAE9AC806/performance.ivp");
  }

  public static void createRunningCase() {
    runTestProcess("17D57ADFD804B24E/runningCase.ivp");
    Selenide.sleep(500);
  }

  public static void destroyRunningCase() {
    runTestProcess("17D57ADFD804B24E/destroyRunningCase.ivp");
  }

  public static void deadlock() {
    runTestProcess("1871451A54CFD978/deadlock.ivp");
  }

  public static void createIntermediateEvent() {
    runTestProcess("188B95440FE25CA6/testIntermediateEventProcess.ivp");
  }

  public static void createNotification() {
    runTestProcess("16E88DD61E825E70/createNotification.ivp");
  }

  public static void createBlob() {
    runTestProcess("16E88DD61E825E70/createBlobs.ivp");
  }

  public static void addSubstitutes() {
    runTestProcess("16E88DD61E825E70/addSubstitutes.ivp");
  }

  public static void cleanupSubstitutes() {
    runTestProcess("16E88DD61E825E70/cleanupSubstitutes.ivp");
  }

  public static void addAbsences() {
    runTestProcess("16E88DD61E825E70/addAbsences.ivp");
  }

  public static void cleanupAbsences() {
    runTestProcess("16E88DD61E825E70/cleanupAbsences.ivp");
  }

  public static void destroyOtherSessions() {
    runTestProcess("18AB33A0B9AA7019/destroyOtherSessions.ivp");
  }

  public static void enableRestart() {
    runTestProcess("18B5CA921CF8441A/enable.ivp");
  }

  public static void disableRestart() {
    runTestProcess("18B5CA921CF8441A/disable.ivp");
  }

  public static void removeRestart() {
    runTestProcess("18B5CA921CF8441A/remove.ivp");
  }

  private static void runTestProcess(String processLink) {
    open(create().app(getAppName()).servlet(SERVLET.PROCESS).path("engine-cockpit-test-data/" + processLink)
        .toUrl());
    assertCurrentUrlContains(isDesigner() ? "/dev-workflow-ui/faces" : "end");
  }

  public static String viewUrl(String page) {
    return viewUrl(page, Map.of());
  }

  public static String viewUrl(String page, Map<String, String> queryParams) {
    var urlBuilder = create();// .staticView(parts.path)
    if (isDesigner()) {
      // test it in designer as PMV
      urlBuilder = urlBuilder.staticView("engine-cockpit/" + page);
    } else {
      // test integrated jar
      urlBuilder = urlBuilder.app("system").path("engine-cockpit/faces/" + page);
    }
    for (var param : queryParams.entrySet()) {
      urlBuilder = urlBuilder.queryParam(param.getKey(), param.getValue());
    }
    return urlBuilder.toUrl();
  }

  public static String testViewUrl(String page) {
    return create().app(getAppName()).staticView("engine-cockpit-test-data/" + page).toUrl();
  }

  public static String getAppName() {
    return isDesigner() ? DESIGNER : "test";
  }

  public static void assertLiveStats(List<String> expectedChartTitles) {
    assertLiveStats(expectedChartTitles, null, false);
  }

  public static void assertLiveStats(List<String> expectedChartTitles, boolean emptyGraphs) {
    assertLiveStats(expectedChartTitles, null, emptyGraphs);
  }

  public static void assertLiveStats(List<String> expectedChartTitles, String jmxSourceMessage, boolean emptyGraphs) {
    waitUntilFreyaJsIsInitialized();
    $("#layout-config-button").shouldBe(visible).click();
    $(".layout-config h3").shouldBe(visible, text("Live Stats"));
    $$(".layout-config h4").shouldHave(
        texts(expectedChartTitles));
    if (!emptyGraphs) {
      $$(".layout-config .ui-chart").shouldBe(
          size(expectedChartTitles.size()));
    }
    if (jmxSourceMessage != null) {
      $(".layout-config .ui-staticmessage").shouldHave(text(jmxSourceMessage));
    }
    $(".layout-config .layout-config-close").click();
    $(".layout-config .layout-config-close").shouldBe(hidden);
  }

  public static void open(String url) {
    open(url, 1);
  }

  private static void open(String url, int retry) {
    if (retry >= 3) {
      throw new RuntimeException("Could not start browser instance.");
    }
    try {
      Selenide.open(url);
    } catch (TimeoutException ex) {
      System.out.println("Browser didn't respond in time, retry: " + retry);
      Selenide.closeWebDriver();
      open(url, retry++);
    }
  }

  public static void waitUntilFreyaJsIsInitialized() {
    webdriver().shouldHave(WebDriverConditions.jsReturnsValue("FreyaEnvironment.isInitialized", "true"));
  }

}
