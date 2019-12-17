package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.axonivy.ivy.supplements.primeui.tester.PrimeUi;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;

import ch.ivyteam.enginecockpit.util.EngineCockpitUrl;

public class WebBase
{
  public static RemoteWebDriver driver;
  public static PrimeUi primeUi;
  
  @BeforeEach
  void initDriver()
  {
    Configuration.browser = "firefox";
    Configuration.headless = true;
    Configuration.reportsFolder = "target/senenide/reports";
    Selenide.open();
    WebBase.driver = (RemoteWebDriver) WebDriverRunner.getWebDriver();
    primeUi = new PrimeUi(driver);
  }
  
  public static String getAdminUser()
  {
    return EngineCockpitUrl.isDesignerApp() ? "Developer" : "admin";
  }
  
  public static void assertCurrentUrlContains(String contains)
  {
    assertThat(driver.getCurrentUrl()).contains(contains);
  }
  
  public static void assertCurrentUrlEndsWith(String endsWith)
  {
    String url = driver.getCurrentUrl();
    if (url.contains(";jsessionid"))
    {
      url = url.substring(0, url.indexOf(";jsessionid"));
    }
    assertThat(url).endsWith(endsWith);
  }
  
  public static String escapeSelector(String selector)
  {
    return "#" + selector.replace(":", "\\:");
  }
}
