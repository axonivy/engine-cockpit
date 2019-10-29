package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ch.ivyteam.enginecockpit.util.EngineCockpitUrl;
import io.github.bonigarcia.seljup.Options;
import io.github.bonigarcia.seljup.SeleniumExtension;

@ExtendWith(SeleniumExtension.class)
public class WebBase
{
  public RemoteWebDriver driver;

  @Options
  FirefoxOptions firefoxOptions = new FirefoxOptions();
  {
    FirefoxBinary binary = new FirefoxBinary();
    binary.addCommandLineOptions("--headless");
    firefoxOptions.setLogLevel(FirefoxDriverLogLevel.WARN);
    firefoxOptions.setBinary(binary);
  }
  
  @BeforeEach
  void initDriver(FirefoxDriver remoteDriver)
  {
    this.driver = remoteDriver;
  }
  
  public static String getAdminUser()
  {
    return EngineCockpitUrl.isDesignerApp() ? "Developer" : "admin";
  }
  
  public static boolean elementNotAvailable(WebDriver driver, By by)
  {
    try
    {
      driver.findElement(by);
      return false;
    }
    catch (NoSuchElementException ex) {
      return true;
    }
  }
  
  public static WebElement waitUntilElementClickable(WebDriver driver, By by)
  {
    webAssertThat(() -> assertThat(driver.findElement(by).isDisplayed()).isTrue());
    return new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(by));
  }
  
  public static void webAssertThat(WebTest test)
  {
    await().ignoreExceptionsInstanceOf(StaleElementReferenceException.class).untilAsserted(test::run);
  }
  
  @FunctionalInterface
  public interface WebTest
  {
    void run();
  }
}
