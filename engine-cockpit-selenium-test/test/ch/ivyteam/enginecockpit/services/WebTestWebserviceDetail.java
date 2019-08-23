package ch.ivyteam.enginecockpit.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

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
  
  private void setConfiguration(FirefoxDriver driver, String username, String password)
  {
    driver.findElementById("webserviceConfigurationForm:password").sendKeys(password);
    setConfiguration(driver, username);
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
            .getText()).contains("Web Service configuration reset"));
  }
  
  @Test
  void testWsEndpointTestConnection(FirefoxDriver driver)
  {
    navigateToWebserviceDetail(driver);
    setEndPoint(driver, "http://zugtstweb:80/notfound");
    driver.navigate().refresh();
    testAndAssertConnection(driver, "Status 404");

    setEndPoint(driver, "http://zugtstweb:81");
    driver.navigate().refresh();
    testAndAssertConnection(driver, "Status 401");
    
    setConfiguration(driver, "admin", "nimda");
    driver.navigate().refresh();
    testAndAssertConnection(driver, "Status 405");
    
    resetEndPoint(driver);
    resetConfiguration(driver);
  }

  private void testAndAssertConnection(FirefoxDriver driver, String msg)
  {
    webAssertThat(() -> assertThat(driver.findElementById("connResult:connectionTestModel").isDisplayed()).isFalse());
    Table table = new Table(driver, By.id("webservcieEndPointForm:webserviceEndpointTable"), "data-rk");
    table.clickButtonForEntry(table.getFirstColumnEntriesForSpanClass("endpoint-entry").get(1), "testWsEndpointBtn");
    webAssertThat(() -> assertThat(driver.findElementById("connResult:connectionTestModel").isDisplayed()).isTrue());
    driver.findElementById("connResult:connTestForm:testConnectionBtn").click();
    saveScreenshot(driver, "connection_" + StringUtils.replace(msg, " ", "_"));
    webAssertThat(() -> assertThat(driver.findElementById("connResult:connTestForm:resultLog_content").getText()).contains(msg));
    driver.findElementById("connResult:connTestForm:closeConTesterDialog").click();
    webAssertThat(() -> assertThat(driver.findElementById("connResult:connectionTestModel").isDisplayed()).isFalse());
  }
  
  @Test
  void testEditEndpointsInvalid(FirefoxDriver driver)
  {
    navigateToWebserviceDetail(driver);
    Table endPointTable = new Table(driver, By.id("webservcieEndPointForm:webserviceEndpointTable"), "data-rk");
    endPointTable.clickButtonForEntry("SampleWebServiceSoap", "editEndpointBtn");
    driver.findElementById("webservcieEndPointForm:defaultInput").clear();
    driver.findElementById("webservcieEndPointForm:saveEndpoint").click();
    webAssertThat(() -> assertThat(driver.findElementById("webservcieEndPointForm:defaultInputMessage").getText())
            .contains("Value is required"));
    saveScreenshot(driver, "invalid");
  }
  
  @Test
  void testSetAndResetEndpoints(FirefoxDriver driver)
  {
    navigateToWebserviceDetail(driver);
    
    setEndPoint(driver, "default", "first", "second");
    driver.navigate().refresh();
    checkEndPoint(driver, "default", "first", "second");
    checkEndPointDoesNotContain(driver, "localhost", "localhost/test");
    resetEndPoint(driver);
    driver.navigate().refresh();
    checkEndPoint(driver, "localhost", "localhost/test");
    checkEndPointDoesNotContain(driver, "default", "first", "second");
  }
  
  @Test
  void testSetAndResetEndpoints_noFallbacks(FirefoxDriver driver)
  {
    navigateToWebserviceDetail(driver);
    
    setEndPoint(driver, "default");
    driver.navigate().refresh();
    checkEndPoint(driver, "default");
    checkEndPointDoesNotContain(driver, "localhost", "localhost/test");
    resetEndPoint(driver);
    driver.navigate().refresh();
    checkEndPoint(driver, "localhost", "localhost/test");
    checkEndPointDoesNotContain(driver, "default");
  }

  private void setEndPoint(FirefoxDriver driver, String defaultLink, String... fallbacks)
  {
    Table endPointTable = new Table(driver, By.id("webservcieEndPointForm:webserviceEndpointTable"), "data-rk");
    endPointTable.clickButtonForEntry("SampleWebServiceSoap", "editEndpointBtn");
    webAssertThat(() -> assertThat(driver.findElementById("webservcieEndPointForm:editEndpointModal").isDisplayed()).isTrue());
    saveScreenshot(driver, "edit_modal");
    
    driver.findElementById("webservcieEndPointForm:defaultInput").clear();
    driver.findElementById("webservcieEndPointForm:defaultInput").sendKeys(defaultLink);
    driver.findElementById("webservcieEndPointForm:fallBackInput").clear();

    driver.findElementById("webservcieEndPointForm:fallBackInput")
            .sendKeys(Arrays.stream(fallbacks).collect(Collectors.joining("\n")));

    driver.findElementById("webservcieEndPointForm:saveEndpoint").click();
    webAssertThat(() -> assertThat(driver.findElementById("webserviceConfigurationForm:wsConfigMsg_container").getText())
            .contains("EndPoint saved"));
    saveScreenshot(driver, "save_edit");
  }
  
  private void resetEndPoint(FirefoxDriver driver)
  {
    Table endPointTable = new Table(driver, By.id("webservcieEndPointForm:webserviceEndpointTable"), "data-rk");
    endPointTable.clickButtonForEntry("SampleWebServiceSoap", "resetEndpointConfig");
    webAssertThat(() -> assertThat(driver.findElementById("webservcieEndPointForm:resetEndpointConfirmDialog").isDisplayed()).isTrue());
    saveScreenshot(driver, "reset_dialog");
    
    driver.findElementById("webservcieEndPointForm:resetEndpointConfirmYesBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("webserviceConfigurationForm:wsConfigMsg_container").getText())
            .contains("EndPoint reset"));
    saveScreenshot(driver, "reset");
  }
  
  private void checkEndPoint(FirefoxDriver driver, String... links)
  {
    Table endPointTable = new Table(driver, By.id("webservcieEndPointForm:webserviceEndpointTable"), "data-rk");
    System.out.println(endPointTable.getFirstColumnEntriesForSpanClass("endpoint-entry"));
    webAssertThat(() -> assertThat(endPointTable.getFirstColumnEntriesForSpanClass("endpoint-entry"))
            .containsAll(Arrays.asList(links)));
  }
  
  private void checkEndPointDoesNotContain(FirefoxDriver driver, String... links)
  {
    Table endPointTable = new Table(driver, By.id("webservcieEndPointForm:webserviceEndpointTable"), "data-rk");
    System.out.println(endPointTable.getFirstColumnEntriesForSpanClass("endpoint-entry"));
    webAssertThat(() -> assertThat(endPointTable.getFirstColumnEntriesForSpanClass("endpoint-entry"))
            .doesNotContainAnyElementsOf(Arrays.asList(links)));
  }
  
  private void navigateToWebserviceDetail(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toWebserviceDetail(driver, WEBSERVICE_NAME);
    saveScreenshot(driver, "webservice_testweb");
  }
}
