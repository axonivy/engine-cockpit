package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.axonivy.ivy.supplements.IvySelenide;
import com.codeborne.selenide.WebDriverRunner;

import ch.ivyteam.enginecockpit.util.EngineCockpitUrl;

@IvySelenide
public class WebBase
{
  public static RemoteWebDriver driver;
  
  @BeforeEach
  void initDriver()
  {
    WebBase.driver = (RemoteWebDriver) WebDriverRunner.getWebDriver();
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
