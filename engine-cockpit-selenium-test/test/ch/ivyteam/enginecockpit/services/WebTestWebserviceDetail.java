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
    webAssertThat(() -> assertThat(driver.findElementsByClassName("ui-panel")).hasSize(3));
    webAssertThat(() -> assertThat(driver.findElementById("webserviceConfigurationForm:name").getText()).isEqualTo(WEBSERVICE_NAME));
  }
  
  @Test
  void testOpenWebserviceHelp(FirefoxDriver driver)
  {
    navigateToWebserviceDetail(driver);
    
    driver.findElementByXPath("//div[@id='breadcrumbOptions']/a").click();
    saveScreenshot(driver, "help_modal");
    webAssertThat(() -> assertThat(driver.findElementById("helpWebserviceDialog:helpServicesModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementByClassName("code-block").getText()).contains(WEBSERVICE_NAME));
  }
  
  @Test
  void testSaveAndResetChanges(FirefoxDriver driver)
  {
    navigateToWebserviceDetail(driver);
    
    setConfiguration(driver, "testUser");
    driver.navigate().refresh();
    checkConfiguration(driver, "testUser");
    resetConfiguration(driver);
    driver.navigate().refresh();
    checkConfiguration(driver, "admin");
  }

  private void setConfiguration(FirefoxDriver driver, String username)
  {
    driver.findElementById("webserviceConfigurationForm:username").clear();
    driver.findElementById("webserviceConfigurationForm:username").sendKeys(username);
    
    saveScreenshot(driver, "set");
    
    driver.findElementById("webserviceConfigurationForm:saveWsConfig").click();
    webAssertThat(() -> assertThat(driver.findElementById("webserviceConfigurationForm:wsConfigMsg_container")
            .getText()).contains("Web Service configuration saved"));
    saveScreenshot(driver, "save");
  }
  
  private void checkConfiguration(FirefoxDriver driver, String username)
  {
    saveScreenshot(driver, "check");
    webAssertThat(() -> assertThat(driver.findElementById("webserviceConfigurationForm:username").getAttribute("value"))
            .isEqualTo(username));
  }
  
  private void resetConfiguration(FirefoxDriver driver)
  {
    driver.findElementById("webserviceConfigurationForm:resetConfig").click();
    webAssertThat(() -> assertThat(driver.findElementById("webserviceConfigurationForm:resetWsConfirmDialog").isDisplayed()).isTrue());
    driver.findElementById("webserviceConfigurationForm:resetWsConfirmYesBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("webserviceConfigurationForm:wsConfigMsg_container")
            .getText()).contains("Web Service configuration reseted"));
  }
  
  private void navigateToWebserviceDetail(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toWebserviceDetail(driver, WEBSERVICE_NAME);
    saveScreenshot(driver, "webservice_testweb");
  }
}
