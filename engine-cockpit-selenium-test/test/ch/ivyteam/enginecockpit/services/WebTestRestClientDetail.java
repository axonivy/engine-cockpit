package ch.ivyteam.enginecockpit.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestRestClientDetail extends WebTestBase
{
  private static final String RESTCLIENT_NAME = "test-rest";
  
  @Test
  void testExternalDatabaseDetailOpen(FirefoxDriver driver)
  {
    navigateToRestClientDetail(driver);
    webAssertThat(() -> 
            assertThat(driver.getCurrentUrl()).endsWith("restclientdetail.xhtml?restClientName=" + RESTCLIENT_NAME));
    webAssertThat(() -> assertThat(driver.findElementsByClassName("ui-panel")).hasSize(2));
    webAssertThat(() -> assertThat(driver.findElementById("restClientConfigurationForm:name").getText()).isEqualTo(RESTCLIENT_NAME));
  }
  
  @Test
  void testOpenRestClientEdit(FirefoxDriver driver)
  {
    navigateToRestClientDetail(driver);
    
    driver.findElementById("restClientConfigurationForm:editRestClientBtn").click();
    saveScreenshot(driver, "edit_modal");
    webAssertThat(() -> assertThat(driver.findElementById("editRestClientDialog:editServicesModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementByClassName("code-block").getText()).contains(RESTCLIENT_NAME));
  }
  
  private void navigateToRestClientDetail(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toRestClientDetail(driver, RESTCLIENT_NAME);
    saveScreenshot(driver, "restclient_testrest");
  }
}
