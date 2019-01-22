package ch.ivyteam.enginecockpit.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestSecuritySystem extends WebTestBase
{
  @Test
  void testSecuritySystem(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toSecuritySystem(driver);
    saveScreenshot(driver);
    assertThat(driver.findElementByTagName("h1").getText()).contains("Security Systems");
    assertThat(driver.findElementsByXPath("//tbody/tr")).isNotEmpty();
    
    //TODO: test sync (ad needed)
  }
}
