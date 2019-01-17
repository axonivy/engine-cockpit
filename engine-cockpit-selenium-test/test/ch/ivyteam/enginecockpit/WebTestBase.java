package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUrl.viewUrl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import ch.ivyteam.enginecockpit.util.EngineCockpitUrl;
import io.github.bonigarcia.seljup.Options;
import io.github.bonigarcia.seljup.SeleniumExtension;

@ExtendWith(SeleniumExtension.class)
public class WebTestBase
{

  @Options
  FirefoxOptions firefoxOptions = new FirefoxOptions();
  {
    FirefoxBinary binary = new FirefoxBinary();
    binary.addCommandLineOptions("--headless");
    firefoxOptions.setBinary(binary);
  }
  
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
  }
  
  public static String getAdminUser()
  {
    return EngineCockpitUrl.isDesignerApp() ? "Developer" : "admin";
  }
}
