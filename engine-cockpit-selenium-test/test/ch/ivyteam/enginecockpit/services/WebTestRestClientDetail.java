package ch.ivyteam.enginecockpit.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.lang3.StringUtils;
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
  void testOpenRestClientHelp(FirefoxDriver driver)
  {
    navigateToRestClientDetail(driver);
    
    driver.findElementByXPath("//div[@id='breadcrumbOptions']/a").click();
    saveScreenshot(driver, "help_modal");
    webAssertThat(() -> assertThat(driver.findElementById("helpRestClientDialog:helpServicesModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementByClassName("code-block").getText()).contains(RESTCLIENT_NAME));
  }
  
  @Test
  void testRestTestConnection(FirefoxDriver driver)
  {
    navigateToRestClientDetail(driver);
    
    setConfiguration(driver, "localhost", "");
    driver.navigate().refresh();
    testAndAssertConnection(driver, "Invalid Url");

    setConfiguration(driver, "http://localhost/testnotfound", "");
    driver.navigate().refresh();
    testAndAssertConnection(driver, "Status 404");
    
    setConfiguration(driver, "http://zugtstweb:81/", "");
    driver.navigate().refresh();
    testAndAssertConnection(driver, "Status 401");
    
    setConfiguration(driver, "http://zugtstweb:81/", "admin", "nimda");
    driver.navigate().refresh();
    testAndAssertConnection(driver, "Status 200");

    resetConfiguration(driver);
  }

  private void testAndAssertConnection(FirefoxDriver driver, String msg)
  {
    webAssertThat(() -> assertThat(driver.findElementById("restClientConfigurationForm:restConfigMsg_container").isDisplayed()).isFalse());
    driver.findElementById("restClientConfigurationForm:testRestBtn").click();
    saveScreenshot(driver, "connection_" + StringUtils.replace(msg, " ", "_"));
    webAssertThat(() -> assertThat(driver.findElementById("restClientConfigurationForm:restConfigMsg_container").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("restClientConfigurationForm:restConfigMsg_container").getText()).contains(msg));
  }
  
  @Test
  void testSaveAndResetChanges(FirefoxDriver driver)
  {
    navigateToRestClientDetail(driver);
    
    setConfiguration(driver, "url", "testUser");
    driver.navigate().refresh();
    checkConfiguration(driver, "url", "testUser");
    resetConfiguration(driver);
    driver.navigate().refresh();
    checkConfiguration(driver, "http://localhost/", "admin");
  }
  
  private void setConfiguration(FirefoxDriver driver, String url, String username, String password)
  {
    driver.findElementById("restClientConfigurationForm:password").sendKeys(password);
    
    setConfiguration(driver, url, username);
  }

  private void setConfiguration(FirefoxDriver driver, String url, String username)
  {
    driver.findElementById("restClientConfigurationForm:url").clear();
    driver.findElementById("restClientConfigurationForm:url").sendKeys(url);
    
    driver.findElementById("restClientConfigurationForm:username").clear();
    driver.findElementById("restClientConfigurationForm:username").sendKeys(username);
    
    saveScreenshot(driver, "set");
    
    driver.findElementById("restClientConfigurationForm:saveRestConfig").click();
    webAssertThat(() -> assertThat(driver.findElementById("restClientConfigurationForm:restConfigMsg_container")
            .getText()).contains("Rest configuration saved"));
    saveScreenshot(driver, "save");
  }
  
  private void checkConfiguration(FirefoxDriver driver, String url, String username)
  {
    saveScreenshot(driver, "check");
    webAssertThat(() -> assertThat(driver.findElementById("restClientConfigurationForm:url").getAttribute("value"))
            .isEqualTo(url));
    webAssertThat(() -> assertThat(driver.findElementById("restClientConfigurationForm:username").getAttribute("value"))
            .isEqualTo(username));
  }
  
  private void resetConfiguration(FirefoxDriver driver)
  {
    driver.findElementById("restClientConfigurationForm:resetConfig").click();
    webAssertThat(() -> assertThat(driver.findElementById("restClientConfigurationForm:resetRestConfirmDialog").isDisplayed()).isTrue());
    driver.findElementById("restClientConfigurationForm:resetRestConfirmYesBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("restClientConfigurationForm:restConfigMsg_container")
            .getText()).contains("Rest configuration reset"));
  }
  
  private void navigateToRestClientDetail(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toRestClientDetail(driver, RESTCLIENT_NAME);
    saveScreenshot(driver, "restclient_testrest");
  }
}
