package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUrl.viewUrl;
import static org.awaitility.Awaitility.await;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.firefox.FirefoxDriver;
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
  
  public void saveScreenshot(RemoteWebDriver driver, String name) 
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
  
  public void saveScreenshot(RemoteWebDriver driver)
  {
    saveScreenshot(driver, "");
  }
  
  public void login(FirefoxDriver driver)
  {
    driver.get(viewUrl("login.xhtml"));
    saveScreenshot(driver, "login");
    driver.findElementById("loginForm:userName").sendKeys(getAdminUser());
    driver.findElementById("loginForm:password").sendKeys(getAdminUser());
    driver.findElementById("loginForm:login").click();
    await().until(() -> driver.getCurrentUrl().endsWith("dashboard.xhtml"));
    saveScreenshot(driver, "dashboard");
    await().ignoreExceptions().until(() -> driver.findElementById("menuform").isDisplayed());
  }
  
  public static String getAdminUser()
  {
    return EngineCockpitUrl.isDesignerApp() ? "Developer" : "admin";
  }
}
