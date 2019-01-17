package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUrl.viewUrl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
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
public class WebTestDashboard
{

  @Options
  FirefoxOptions firefoxOptions = new FirefoxOptions();
  {
    FirefoxBinary binary = new FirefoxBinary();
    binary.addCommandLineOptions("--headless");
    firefoxOptions.setBinary(binary);
  }

  @Test
  void testLogin(FirefoxDriver driver)
  {
    login(driver);
    saveScreenshot(driver);
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).endsWith("dashboard.xhtml"));
    await().untilAsserted(() -> assertThat(driver.getTitle()).startsWith("Engine Cockpit").doesNotContain("Login"));
  }
  
  private static void saveScreenshot(RemoteWebDriver driver)
  {
    File source = driver.getScreenshotAs(OutputType.FILE);
    System.out.println("Source: " + source);
    try
    {
      String dir = "target/surefire-reports/ch.ivyteam.enginecockpit.WebTestDashboard/";
      FileUtils.moveFile(source, new File(dir, source.getName()));
    }
    catch (IOException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  private void login(FirefoxDriver driver)
  {
    driver.get(viewUrl("login.xhtml"));
    saveScreenshot(driver);
    driver.findElementById("loginForm:userName").sendKeys(getAdminUser());
    driver.findElementById("loginForm:password").sendKeys(getAdminUser());
    driver.findElementById("loginForm:login").click();
  }

  private static String getAdminUser()
  {
    return EngineCockpitUrl.isDesignerApp() ? "Developer" : "admin";
  }
}
