package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUrl.viewUrl;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.remote.RemoteWebDriver;

import ch.ivyteam.enginecockpit.util.EngineCockpitUrl;

public class WebTestBase extends WebBase
{
  
  private static String className;
  private static String methodName;
  private static int screenshotCounter;
  
  @BeforeEach
  void init(TestInfo testInfo)
  {
    className = testInfo.getTestClass().map(c -> c.getName()).orElse("unknownClass");
    methodName = testInfo.getTestMethod().map(m -> m.getName()).orElse("unknownMethod");
    screenshotCounter = 0;
  }
  
  public static void saveScreenshot(RemoteWebDriver driver, String name)
  {
    File source = driver.getScreenshotAs(OutputType.FILE);
    System.out.println("Source: " + source);
    try
    {
      String dir = "target/surefire-reports/" + className + "/" + methodName + "/";
      FileUtils.moveFile(source, new File(dir, String.valueOf(screenshotCounter) + "_" + name + "_" + source.getName()));
      screenshotCounter ++;
    }
    catch (IOException ex)
    {
      throw new RuntimeException(ex);
    }
  }
  
  public void saveScreenshot(String name) 
  {
    saveScreenshot(driver, name);
  }
  
  public void saveScreenshot()
  {
    saveScreenshot("");
  }
  
  public void scrollYBy(int value)
  {
    driver.executeScript("window.scrollBy(0, " + value + ")");
  }
  
  public void scrollYToBottom()
  {
    driver.executeScript("window.scrollTo(0, document.body.scrollHeight)");
  }
  
  public void scrollYToElement(By element)
  {
    scrollYBy(driver.findElement(element).getLocation().getY() - 64);
  }
  
  public void login(String url)
  {
    driver.get(viewUrl(url));
    if (driver.getCurrentUrl().endsWith("login.xhtml"))
    {
      webAssertThat(() -> assertThat(driver.findElementByTagName("h1").getText()).contains("Engine Cockpit"));
      driver.findElementById("loginForm:userName").sendKeys(getAdminUser());
      driver.findElementById("loginForm:password").sendKeys(getAdminUser());
      driver.findElementById("loginForm:login").click();
    }
    webAssertThat(() -> assertThat(driver.getCurrentUrl()).endsWith(url));
    webAssertThat(() -> assertThat(driver.findElementById("menuform").isDisplayed()).isTrue());
  }
  
  public void login()
  {
    login("dashboard.xhtml");
  }
  
  public static String getAdminUser()
  {
    return EngineCockpitUrl.isDesignerApp() ? "Developer" : "admin";
  }
  
  public static void addSystemAdmin(RemoteWebDriver driver)
  {
    runTestProcess(driver, "/engine-cockpit-test-data/16E88DD61E825E70/addAdministrator.ivp");
  }
  
  public static void populateBusinessCalendar(RemoteWebDriver driver)
  {
    runTestProcess(driver, "/engine-cockpit-test-data/16E88DD61E825E70/createBusinessCalendar.ivp");
  }
  
  public static void runExternalDbQuery(RemoteWebDriver driver)
  {
    runTestProcess(driver, "/engine-cockpit-test-data/16E88DD61E825E70/runDbExecution.ivp");
  }

  public static void createBusinessData(RemoteWebDriver driver)
  {
    runTestProcess(driver, "/engine-cockpit-test-data/16E88DD61E825E70/createBusinessData.ivp");
  }
  
  public static void createLicenceEvents(RemoteWebDriver driver)
  {
    runTestProcess(driver, "/engine-cockpit-test-data/16E84204B7FE6C91/addLicenceEvents.ivp");
  }
  
  public static void resetLicence(RemoteWebDriver driver)
  {
    runTestProcess(driver, "/engine-cockpit-test-data/16E84204B7FE6C91/resetLicence.ivp");
  }
  
  public static void resetConfig(RemoteWebDriver driver)
  {
    runTestProcess(driver, "/engine-cockpit-test-data/16E881C7DC458C7D/cleanupAdmins.ivp");
    runTestProcess(driver, "/engine-cockpit-test-data/16E881C7DC458C7D/cleanupConnectors.ivp");
    runTestProcess(driver, "/engine-cockpit-test-data/16E881C7DC458C7D/cleanupSystemDb.ivp");
  }
  
  public static void createOldDb(RemoteWebDriver driver)
  {
    runTestProcess(driver, "/engine-cockpit-test-data/16E8EAD7CC77A0A3/createOldDatabase.ivp");
  }
  
  public static void deleteOldDb(RemoteWebDriver driver)
  {
    runTestProcess(driver, "/engine-cockpit-test-data/16E8EAD7CC77A0A3/deleteOldDatabase.ivp");
  }
  
  public static void deleteTempDb(RemoteWebDriver driver)
  {
    runTestProcess(driver, "/engine-cockpit-test-data/16E8EAD7CC77A0A3/deleteTempDatabase.ivp");
  }

  private static void runTestProcess(RemoteWebDriver driver, String processLink)
  {
    driver.get(EngineCockpitUrl.base() + "/pro/" + getAppName() + processLink);
    assertEndPage(driver);
  }
  
  private static String getAppName()
  {
    return EngineCockpitUrl.isDesignerApp() ? EngineCockpitUrl.DESIGNER_APP : "test";
  }
  
  private static void assertEndPage(RemoteWebDriver driver)
  {
    webAssertThat(() -> assertThat(driver.getCurrentUrl()).contains(
            EngineCockpitUrl.isDesignerApp() ? "index.jsp" : "end"));
  }
}
