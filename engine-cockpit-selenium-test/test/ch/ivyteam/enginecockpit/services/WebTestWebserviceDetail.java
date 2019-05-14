package ch.ivyteam.enginecockpit.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestWebserviceDetail extends WebTestBase
{
  private static final String WEBSERVICE_NAME = "test-web";
  
  @Test
  void testExternalDatabaseDetailOpen(FirefoxDriver driver)
  {
    navigateToWebserviceDetail(driver);
    webAssertThat(() -> assertThat(driver.getCurrentUrl()).contains("webservicedetail.xhtml?webserviceId="));
    webAssertThat(() -> assertThat(driver.getTitle()).isEqualTo("Web Service Detail"));
    webAssertThat(() -> assertThat(driver.findElementsByClassName("ui-panel")).hasSize(2));
    webAssertThat(() -> assertThat(driver.findElementById("webserviceConfigurationForm:name").getText()).isEqualTo(WEBSERVICE_NAME));
  }
  
  private void navigateToWebserviceDetail(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toWebserviceDetail(driver, WEBSERVICE_NAME);
    saveScreenshot(driver, "webservice_testweb");
  }
}
