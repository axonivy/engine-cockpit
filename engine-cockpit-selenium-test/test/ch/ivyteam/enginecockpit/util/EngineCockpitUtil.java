package ch.ivyteam.enginecockpit.util;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUrl.viewUrl;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.remote.RemoteWebDriver;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.WebDriverRunner;

public class EngineCockpitUtil
{
  public static final String DESIGNER_APP = "designer";
  
  public static String applicationName()
  {
    return System.getProperty("test.engine.app", DESIGNER_APP);
  }
  
  public static boolean isDesignerApp()
  {
    return System.getProperty("test.engine.app", DESIGNER_APP).equals(DESIGNER_APP);
  }

  public static String pmvName()
  {
    return System.getProperty("test.engine.pmv", "engine-cockpit");
  }
  
  public static String getAdminUser()
  {
    return isDesignerApp() ? "Developer" : "admin";
  }
  
  public static void login(String url)
  {
    open(viewUrl(url));
    if (getCurrentUrl().endsWith("login.xhtml"))
    {
      $("h1").shouldHave(text("Engine Cockpit"));
      $("#loginForm\\:userName").sendKeys(getAdminUser());
      $("#loginForm\\:password").sendKeys(getAdminUser());
      $("#loginForm\\:login").click();
    }
    $("#menuform").shouldBe(visible);
    assertCurrentUrlEndsWith(url);
  }
  
  public static void login()
  {
    login("dashboard.xhtml");
  }
  
  public static void waitUntilAjaxIsFinished()
  {
    $("#ajaxLoadingStatus_start").shouldNotBe(visible);
  }
  
  public static void assertCurrentUrlContains(String contains)
  {
    assertThat(getCurrentUrl()).contains(contains);
  }
  
  public static void assertCurrentUrlEndsWith(String endsWith)
  {
    String url = getCurrentUrl();
    if (url.contains(";jsessionid"))
    {
      url = url.substring(0, url.indexOf(";jsessionid"));
    }
    assertThat(url).endsWith(endsWith);
  }
  
  public static void executeJs(String js)
  {
    ((RemoteWebDriver) WebDriverRunner.getWebDriver()).executeScript(js);
  }
  
  public static String escapeSelector(String selector)
  {
    return "#" + selector.replace(":", "\\:");
  }
  
  public static void addSystemAdmin()
  {
    runTestProcess("/engine-cockpit-test-data/16E88DD61E825E70/addAdministrator.ivp");
  }
  
  public static void populateBusinessCalendar()
  {
    runTestProcess("/engine-cockpit-test-data/16E88DD61E825E70/createBusinessCalendar.ivp");
  }
  
  public static void runExternalDbQuery()
  {
    runTestProcess("/engine-cockpit-test-data/16E88DD61E825E70/runDbExecution.ivp");
  }

  public static void createBusinessData()
  {
    runTestProcess("/engine-cockpit-test-data/16E88DD61E825E70/createBusinessData.ivp");
  }
  
  public static void createLicenceEvents()
  {
    runTestProcess("/engine-cockpit-test-data/16E84204B7FE6C91/addLicenceEvents.ivp");
  }
  
  public static void resetLicence()
  {
    runTestProcess("/engine-cockpit-test-data/16E84204B7FE6C91/resetLicence.ivp");
  }
  
  public static void resetConfig()
  {
    runTestProcess("/engine-cockpit-test-data/16E881C7DC458C7D/cleanupAdmins.ivp");
    runTestProcess("/engine-cockpit-test-data/16E881C7DC458C7D/cleanupConnectors.ivp");
    runTestProcess("/engine-cockpit-test-data/16E881C7DC458C7D/cleanupSystemDb.ivp");
  }
  
  public static void createOldDb()
  {
    runTestProcess("/engine-cockpit-test-data/16E8EAD7CC77A0A3/createOldDatabase.ivp");
  }
  
  public static void deleteOldDb()
  {
    runTestProcess("/engine-cockpit-test-data/16E8EAD7CC77A0A3/deleteOldDatabase.ivp");
  }
  
  public static void deleteTempDb()
  {
    runTestProcess("/engine-cockpit-test-data/16E8EAD7CC77A0A3/deleteTempDatabase.ivp");
  }

  private static void runTestProcess(String processLink)
  {
    open(EngineCockpitUrl.base() + "/pro/" + getAppName() + processLink);
    if (isDesignerApp())
    {
      $("h2").shouldBe(Condition.text("Personal Task List"));
    }
    else
    {
      $("h3").shouldBe(Condition.text("Task End"));
    }
    assertCurrentUrlContains(isDesignerApp() ? "index.jsp" : "end");
  }
  
  private static String getAppName()
  {
    return isDesignerApp() ? DESIGNER_APP : "test";
  }
  
  private static String getCurrentUrl()
  {
    return WebDriverRunner.getWebDriver().getCurrentUrl();
  }
}
