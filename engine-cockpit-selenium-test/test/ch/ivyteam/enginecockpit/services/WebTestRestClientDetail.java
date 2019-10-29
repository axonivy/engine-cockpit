package ch.ivyteam.enginecockpit.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestRestClientDetail extends WebTestBase
{
  private static final String RESTCLIENT_NAME = "test-rest";
  
  @Test
  void testExternalDatabaseDetailOpen()
  {
    navigateToRestClientDetail();
    webAssertThat(() -> assertThat(driver.getCurrentUrl()).endsWith("restclientdetail.xhtml?restClientName=" + RESTCLIENT_NAME));
    webAssertThat(() -> assertThat(driver.findElementsByClassName("ui-panel")).hasSize(2));
    webAssertThat(() -> assertThat(driver.findElementById("restClientConfigurationForm:name").getText()).isEqualTo(RESTCLIENT_NAME));
  }
  
  @Test
  void testOpenRestClientHelp()
  {
    navigateToRestClientDetail();
    
    driver.findElementByXPath("//div[@id='breadcrumbOptions']/a").click();
    saveScreenshot("help_modal");
    webAssertThat(() -> assertThat(driver.findElementById("helpRestClientDialog:helpServicesModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementByClassName("code-block").getText()).contains(RESTCLIENT_NAME));
  }
  
  @Test
  void testRestTestConnection()
  {
    navigateToRestClientDetail();
    
    setConfiguration("localhost", "");
    driver.navigate().refresh();
    testAndAssertConnection("Invalid Url");

    setConfiguration("http://zugtstweb:80/testnotfound", "");
    driver.navigate().refresh();
    testAndAssertConnection("Status 404");
    
    setConfiguration("http://zugtstweb:81/", "");
    driver.navigate().refresh();
    testAndAssertConnection("Status 401");
    
    setConfiguration("http://zugtstweb:81/", "admin", "nimda");
    driver.navigate().refresh();
    testAndAssertConnection("Status 200");

    resetConfiguration();
  }

  private void testAndAssertConnection(String msg)
  {
    webAssertThat(() -> assertThat(driver.findElementById("connResult:connectionTestModel").isDisplayed()).isFalse());
    driver.findElementById("restClientConfigurationForm:testRestBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("connResult:connectionTestModel").isDisplayed()).isTrue());
    driver.findElementById("connResult:connTestForm:testConnectionBtn").click();
    saveScreenshot("connection_" + StringUtils.replace(msg, " ", "_"));
    webAssertThat(() -> assertThat(driver.findElementById("connResult:connTestForm:resultLog_content").getText()).contains(msg));
    driver.findElementByXPath("//*[@id='connResult:connectionTestModel']/div/a").click();
    webAssertThat(() -> assertThat(driver.findElementById("connResult:connectionTestModel").isDisplayed()).isFalse());
  }
  
  @Test
  void testSaveAndResetChanges()
  {
    navigateToRestClientDetail();
    
    setConfiguration("url", "testUser");
    driver.navigate().refresh();
    checkConfiguration("url", "testUser");
    resetConfiguration();
    driver.navigate().refresh();
    checkConfiguration("http://localhost/", "admin");
  }
  
  private void setConfiguration(String url, String username, String password)
  {
    driver.findElementById("restClientConfigurationForm:password").sendKeys(password);
    
    setConfiguration(url, username);
  }

  private void setConfiguration(String url, String username)
  {
    driver.findElementById("restClientConfigurationForm:url").clear();
    driver.findElementById("restClientConfigurationForm:url").sendKeys(url);
    
    driver.findElementById("restClientConfigurationForm:username").clear();
    driver.findElementById("restClientConfigurationForm:username").sendKeys(username);
    
    saveScreenshot("set");
    
    driver.findElementById("restClientConfigurationForm:saveRestConfig").click();
    webAssertThat(() -> assertThat(driver.findElementById("restClientConfigurationForm:restConfigMsg_container")
            .getText()).contains("Rest configuration saved"));
    saveScreenshot("save");
  }
  
  private void checkConfiguration(String url, String username)
  {
    saveScreenshot("check");
    webAssertThat(() -> assertThat(driver.findElementById("restClientConfigurationForm:url").getAttribute("value"))
            .isEqualTo(url));
    webAssertThat(() -> assertThat(driver.findElementById("restClientConfigurationForm:username").getAttribute("value"))
            .isEqualTo(username));
  }
  
  private void resetConfiguration()
  {
    driver.findElementById("restClientConfigurationForm:resetConfig").click();
    webAssertThat(() -> assertThat(driver.findElementById("restClientConfigurationForm:resetRestConfirmDialog").isDisplayed()).isTrue());
    driver.findElementById("restClientConfigurationForm:resetRestConfirmYesBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("restClientConfigurationForm:restConfigMsg_container")
            .getText()).contains("Rest configuration reset"));
  }
  
  private void navigateToRestClientDetail()
  {
    login();
    Navigation.toRestClientDetail(driver, RESTCLIENT_NAME);
    saveScreenshot("restclient_testrest");
  }
}
