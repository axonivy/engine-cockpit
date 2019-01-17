package ch.ivyteam.enginecockpit;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

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
  
  public static void saveScreenshot(RemoteWebDriver driver)
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
}
