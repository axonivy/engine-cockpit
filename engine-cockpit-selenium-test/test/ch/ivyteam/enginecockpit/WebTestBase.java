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
  
  private String className;
  private String methodName;
  private int screenshotCounter;
  
  @BeforeEach
  void init(TestInfo testInfo)
  {
    this.className = testInfo.getTestClass().map(c -> c.getName()).orElse("unknownClass");
    this.methodName = testInfo.getTestMethod().map(m -> m.getName()).orElse("unknownMethod");
    screenshotCounter = 0;
  }
  
  public void saveScreenshot(String name) 
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
  
  public void login()
  {
    driver.get(viewUrl("login.xhtml"));
    driver.findElementById("loginForm:userName").sendKeys(getAdminUser());
    driver.findElementById("loginForm:password").sendKeys(getAdminUser());
    driver.findElementById("loginForm:login").click();
    webAssertThat(() -> assertThat(driver.getCurrentUrl()).endsWith("dashboard.xhtml"));
    webAssertThat(() -> assertThat(driver.findElementById("menuform").isDisplayed()).isTrue());
  }
  
  public static String getAdminUser()
  {
    return EngineCockpitUrl.isDesignerApp() ? "Developer" : "admin";
  }
  
  public static void populateBusinessCalendar(RemoteWebDriver driver)
  {
    driver.get(EngineCockpitUrl.base() + "/pro/" + getAppName() + "/engine-cockpit-test-data/16AD3F265FFA55DD/start.ivp");
    assertEndPage(driver);
  }
  
  public static void runExternalDbQuery(RemoteWebDriver driver)
  {
    driver.get(EngineCockpitUrl.base() + "/pro/" + getAppName() + "/engine-cockpit-test-data/16C6B9ADB931DEF8/start.ivp");
    assertEndPage(driver);
  }

  public static void createBusinessData(RemoteWebDriver driver)
  {
    driver.get(EngineCockpitUrl.base() + "/pro/" + getAppName() + "/engine-cockpit-test-data/16D80E7AD6FA8FFB/create.ivp");
    assertEndPage(driver);
  }
  
  public static void createLicenceEvents(RemoteWebDriver driver)
  {
    driver.get(EngineCockpitUrl.base() + "/pro/" + getAppName() + "/engine-cockpit-test-data/16DD9CFD7CAC310B/start.ivp");
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
