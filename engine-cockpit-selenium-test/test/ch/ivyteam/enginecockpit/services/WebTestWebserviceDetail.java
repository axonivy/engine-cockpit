package ch.ivyteam.enginecockpit.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

public class WebTestWebserviceDetail extends WebTestBase
{
  private static final String WEBSERVICE_NAME = "test-web";
  
  @Test
  void testExternalDatabaseDetailOpen()
  {
    navigateToWebserviceDetail();
    webAssertThat(() -> assertThat(driver.getCurrentUrl()).contains("webservicedetail.xhtml?webserviceId="));
    webAssertThat(() -> assertThat(driver.findElementsByClassName("ui-panel")).hasSize(3));
    webAssertThat(() -> assertThat(driver.findElementById("webserviceConfigurationForm:name").getText()).isEqualTo(WEBSERVICE_NAME));
  }
  
  @Test
  void testOpenWebserviceHelp()
  {
    navigateToWebserviceDetail();
    
    driver.findElementByXPath("//div[@id='breadcrumbOptions']/a").click();
    saveScreenshot("help_modal");
    webAssertThat(() -> assertThat(driver.findElementById("helpWebserviceDialog:helpServicesModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementByClassName("code-block").getText()).contains(WEBSERVICE_NAME));
  }
  
  @Test
  void testSaveAndResetChanges()
  {
    navigateToWebserviceDetail();
    
    setConfiguration("testUser");
    driver.navigate().refresh();
    checkConfiguration("testUser");
    resetConfiguration();
    driver.navigate().refresh();
    checkConfiguration("admin");
  }
  
  private void setConfiguration(String username, String password)
  {
    driver.findElementById("webserviceConfigurationForm:password").sendKeys(password);
    setConfiguration(username);
  }

  private void setConfiguration(String username)
  {
    driver.findElementById("webserviceConfigurationForm:username").clear();
    driver.findElementById("webserviceConfigurationForm:username").sendKeys(username);
    
    saveScreenshot("set");
    
    driver.findElementById("webserviceConfigurationForm:saveWsConfig").click();
    webAssertThat(() -> assertThat(driver.findElementById("webserviceConfigurationForm:wsConfigMsg_container")
            .getText()).contains("Web Service configuration saved"));
    saveScreenshot("save");
  }
  
  private void checkConfiguration(String username)
  {
    saveScreenshot("check");
    webAssertThat(() -> assertThat(driver.findElementById("webserviceConfigurationForm:username").getAttribute("value"))
            .isEqualTo(username));
  }
  
  private void resetConfiguration()
  {
    driver.findElementById("webserviceConfigurationForm:resetConfig").click();
    webAssertThat(() -> assertThat(driver.findElementById("webserviceConfigurationForm:resetWsConfirmDialog").isDisplayed()).isTrue());
    driver.findElementById("webserviceConfigurationForm:resetWsConfirmYesBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("webserviceConfigurationForm:wsConfigMsg_container")
            .getText()).contains("Web Service configuration reset"));
  }
  
  @Test
  void testWsEndpointTestConnection()
  {
    navigateToWebserviceDetail();
    setEndPoint("http://zugtstweb:80/notfound");
    driver.navigate().refresh();
    testAndAssertConnection("Status 404");

    setEndPoint("http://zugtstweb:81");
    driver.navigate().refresh();
    testAndAssertConnection("Status 401");
    
    setConfiguration("admin", "nimda");
    driver.navigate().refresh();
    testAndAssertConnection("Status 405");
    
    resetEndPoint();
    resetConfiguration();
  }

  private void testAndAssertConnection(String msg)
  {
    webAssertThat(() -> assertThat(driver.findElementById("connResult:connectionTestModel").isDisplayed()).isFalse());
    Table table = new Table(driver, By.id("webservcieEndPointForm:webserviceEndpointTable"), "data-rk");
    table.clickButtonForEntry(table.getFirstColumnEntriesForSpanClass("endpoint-entry").get(1), "testWsEndpointBtn");
    webAssertThat(() -> assertThat(driver.findElementById("connResult:connectionTestModel").isDisplayed()).isTrue());
    driver.findElementById("connResult:connTestForm:testConnectionBtn").click();
    saveScreenshot("connection_" + StringUtils.replace(msg, " ", "_"));
    webAssertThat(() -> assertThat(driver.findElementById("connResult:connTestForm:resultLog_content").getText()).contains(msg));
    driver.findElementById("connResult:connTestForm:closeConTesterDialog").click();
    webAssertThat(() -> assertThat(driver.findElementById("connResult:connectionTestModel").isDisplayed()).isFalse());
  }
  
  @Test
  void testEditEndpointsInvalid()
  {
    navigateToWebserviceDetail();
    Table endPointTable = new Table(driver, By.id("webservcieEndPointForm:webserviceEndpointTable"), "data-rk");
    endPointTable.clickButtonForEntry("SampleWebServiceSoap", "editEndpointBtn");
    driver.findElementById("webservcieEndPointForm:defaultInput").clear();
    driver.findElementById("webservcieEndPointForm:saveEndpoint").click();
    webAssertThat(() -> assertThat(driver.findElementById("webservcieEndPointForm:defaultInputMessage").getText())
            .contains("Value is required"));
    saveScreenshot("invalid");
  }
  
  @Test
  void testSetAndResetEndpoints()
  {
    navigateToWebserviceDetail();
    
    setEndPoint("default", "first", "second");
    driver.navigate().refresh();
    checkEndPoint("default", "first", "second");
    checkEndPointDoesNotContain("localhost", "localhost/test");
    resetEndPoint();
    driver.navigate().refresh();
    checkEndPoint("localhost", "localhost/test");
    checkEndPointDoesNotContain("default", "first", "second");
  }
  
  @Test
  void testSetAndResetEndpoints_noFallbacks()
  {
    navigateToWebserviceDetail();
    
    setEndPoint("default");
    driver.navigate().refresh();
    checkEndPoint("default");
    checkEndPointDoesNotContain("localhost", "localhost/test");
    resetEndPoint();
    driver.navigate().refresh();
    checkEndPoint("localhost", "localhost/test");
    checkEndPointDoesNotContain("default");
  }

  private void setEndPoint(String defaultLink, String... fallbacks)
  {
    Table endPointTable = new Table(driver, By.id("webservcieEndPointForm:webserviceEndpointTable"), "data-rk");
    endPointTable.clickButtonForEntry("SampleWebServiceSoap", "editEndpointBtn");
    webAssertThat(() -> assertThat(driver.findElementById("webservcieEndPointForm:editEndpointModal").isDisplayed()).isTrue());
    saveScreenshot("edit_modal");
    
    driver.findElementById("webservcieEndPointForm:defaultInput").clear();
    driver.findElementById("webservcieEndPointForm:defaultInput").sendKeys(defaultLink);
    driver.findElementById("webservcieEndPointForm:fallBackInput").clear();

    driver.findElementById("webservcieEndPointForm:fallBackInput")
            .sendKeys(Arrays.stream(fallbacks).collect(Collectors.joining("\n")));

    driver.findElementById("webservcieEndPointForm:saveEndpoint").click();
    webAssertThat(() -> assertThat(driver.findElementById("webserviceConfigurationForm:wsConfigMsg_container").getText())
            .contains("EndPoint saved"));
    saveScreenshot("save_edit");
  }
  
  private void resetEndPoint()
  {
    Table endPointTable = new Table(driver, By.id("webservcieEndPointForm:webserviceEndpointTable"), "data-rk");
    endPointTable.clickButtonForEntry("SampleWebServiceSoap", "resetEndpointConfig");
    webAssertThat(() -> assertThat(driver.findElementById("webservcieEndPointForm:resetEndpointConfirmDialog").isDisplayed()).isTrue());
    saveScreenshot("reset_dialog");
    
    driver.findElementById("webservcieEndPointForm:resetEndpointConfirmYesBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("webserviceConfigurationForm:wsConfigMsg_container").getText())
            .contains("EndPoint reset"));
    saveScreenshot("reset");
  }
  
  private void checkEndPoint(String... links)
  {
    Table endPointTable = new Table(driver, By.id("webservcieEndPointForm:webserviceEndpointTable"), "data-rk");
    System.out.println(endPointTable.getFirstColumnEntriesForSpanClass("endpoint-entry"));
    webAssertThat(() -> assertThat(endPointTable.getFirstColumnEntriesForSpanClass("endpoint-entry"))
            .containsAll(Arrays.asList(links)));
  }
  
  private void checkEndPointDoesNotContain(String... links)
  {
    Table endPointTable = new Table(driver, By.id("webservcieEndPointForm:webserviceEndpointTable"), "data-rk");
    System.out.println(endPointTable.getFirstColumnEntriesForSpanClass("endpoint-entry"));
    webAssertThat(() -> assertThat(endPointTable.getFirstColumnEntriesForSpanClass("endpoint-entry"))
            .doesNotContainAnyElementsOf(Arrays.asList(links)));
  }
  
  private void navigateToWebserviceDetail()
  {
    login();
    Navigation.toWebserviceDetail(driver, WEBSERVICE_NAME);
    saveScreenshot("webservice_testweb");
  }
}
