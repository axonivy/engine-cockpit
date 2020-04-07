package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestWebserviceDetail
{
  private static final String WEBSERVICE_NAME = "test-web";
  
  @BeforeEach
  void beforeEach()
  {
    login();
    Navigation.toWebserviceDetail(WEBSERVICE_NAME);
  }
  
  @Test
  void testExternalDatabaseDetailOpen()
  {
    assertCurrentUrlContains("webservicedetail.xhtml?webserviceId=");
    $$(".ui-panel").shouldHave(size(3));
    $("#webserviceConfigurationForm\\:name").shouldBe(exactText(WEBSERVICE_NAME));
  }
  
  @Test
  void testOpenWebserviceHelp()
  {
    $("#breadcrumbOptions > a").shouldBe(visible).click();
    $("#helpWebserviceDialog\\:helpServicesModal").shouldBe(Condition.visible);
    $(".code-block").shouldBe(text(WEBSERVICE_NAME));
  }
  
  @Test
  void testSaveAndResetChanges()
  {
    setConfiguration("testUser");
    Selenide.refresh();
    checkConfiguration("testUser");
    resetConfiguration();
    Selenide.refresh();
    checkConfiguration("admin");
  }
  
  private void setConfiguration(String username, String password)
  {
    $("#webserviceConfigurationForm\\:password").sendKeys(password);
    setConfiguration(username);
  }

  private void setConfiguration(String username)
  {
    $("#webserviceConfigurationForm\\:username").clear();
    $("#webserviceConfigurationForm\\:username").sendKeys(username);
    
    $("#webserviceConfigurationForm\\:saveWsConfig").click();
    $("#webserviceConfigurationForm\\:wsConfigMsg_container").shouldBe(text("Web Service configuration saved"));
  }
  
  private void checkConfiguration(String username)
  {
    $("#webserviceConfigurationForm\\:username").shouldBe(exactValue(username));
  }
  
  private void resetConfiguration()
  {
    $("#webserviceConfigurationForm\\:resetConfig").click();
    $("#webserviceConfigurationForm\\:resetWsConfirmDialog").shouldBe(visible);
    $("#webserviceConfigurationForm\\:resetWsConfirmYesBtn").click();
    $("#webserviceConfigurationForm\\:wsConfigMsg_container").shouldBe(text("Web Service configuration reset"));
  }
  
  @Test
  void testWsEndpointTestConnection()
  {
    setEndPoint("http://zugtstweb:80/notfound");
    Selenide.refresh();
    testAndAssertConnection("Status 404");

    setEndPoint("http://zugtstweb:81");
    Selenide.refresh();
    testAndAssertConnection("Status 401");
    
    setConfiguration("admin", "nimda");
    Selenide.refresh();
    testAndAssertConnection("Status 405");
    
    resetEndPoint();
    resetConfiguration();
  }

  private void testAndAssertConnection(String msg)
  {
    $("#connResult\\:connectionTestModel").shouldNotBe(visible);
    Table table = new Table(By.id("webservcieEndPointForm:webserviceEndpointTable"), "data-rk");
    table.clickButtonForEntry(table.getFirstColumnEntriesForSpanClass("endpoint-entry").get(1), "testWsEndpointBtn");
    $("#connResult\\:connectionTestModel").shouldBe(visible);
    $("#connResult\\:connTestForm\\:testConnectionBtn").click();
    $("#connResult\\:connTestForm\\:resultLog_content").shouldBe(text(msg));
    $("#connResult\\:connTestForm\\:closeConTesterDialog").click();
    $("#connResult\\:connectionTestModel").shouldNotBe(visible);
  }
  
  @Test
  void testEditEndpointsInvalid()
  {
    new Table(By.id("webservcieEndPointForm:webserviceEndpointTable"), "data-rk")
            .clickButtonForEntry("SampleWebServiceSoap", "editEndpointBtn");
    $("#webservcieEndPointForm\\:defaultInput").clear();
    $("#webservcieEndPointForm\\:saveEndpoint").click();
    $("#webservcieEndPointForm\\:defaultInputMessage").shouldBe(text("Value is required"));
  }
  
  @Test
  void testSetAndResetEndpoints()
  {
    setEndPoint("default", "first", "second");
    Selenide.refresh();
    checkEndPoint("default", "first", "second");
    checkEndPointDoesNotContain("localhost", "localhost/test");
    resetEndPoint();
    Selenide.refresh();
    checkEndPoint("localhost", "localhost/test");
    checkEndPointDoesNotContain("default", "first", "second");
  }
  
  @Test
  void testSetAndResetEndpoints_noFallbacks()
  {
    setEndPoint("default");
    Selenide.refresh();
    checkEndPoint("default");
    checkEndPointDoesNotContain("localhost", "localhost/test");
    resetEndPoint();
    Selenide.refresh();
    checkEndPoint("localhost", "localhost/test");
    checkEndPointDoesNotContain("default");
  }

  private void setEndPoint(String defaultLink, String... fallbacks)
  {
    new Table(By.id("webservcieEndPointForm:webserviceEndpointTable"), "data-rk")
            .clickButtonForEntry("SampleWebServiceSoap", "editEndpointBtn");
    $("#webservcieEndPointForm\\:editEndpointModal").shouldBe(visible);
    
    $("#webservcieEndPointForm\\:defaultInput").clear();
    $("#webservcieEndPointForm\\:defaultInput").sendKeys(defaultLink);
    $("#webservcieEndPointForm\\:fallBackInput").clear();

    $("#webservcieEndPointForm\\:fallBackInput").sendKeys(Arrays.stream(fallbacks).collect(Collectors.joining("\n")));

    $("#webservcieEndPointForm\\:saveEndpoint").click();
    $("#webserviceConfigurationForm\\:wsConfigMsg_container").shouldBe(text("EndPoint saved"));
  }
  
  private void resetEndPoint()
  {
    new Table(By.id("webservcieEndPointForm:webserviceEndpointTable"), "data-rk")
            .clickButtonForEntry("SampleWebServiceSoap", "resetEndpointConfig");
    $("#webservcieEndPointForm\\:resetEndpointConfirmDialog").shouldBe(visible);
    
    $("#webservcieEndPointForm\\:resetEndpointConfirmYesBtn").click();
    $("#webserviceConfigurationForm\\:wsConfigMsg_container").shouldBe(text("EndPoint reset"));
  }
  
  private void checkEndPoint(String... links)
  {
    assertThat(new Table(By.id("webservcieEndPointForm:webserviceEndpointTable"), "data-rk")
            .getFirstColumnEntriesForSpanClass("endpoint-entry"))
            .containsAll(Arrays.asList(links));
  }
  
  private void checkEndPointDoesNotContain(String... links)
  {
    assertThat(new Table(By.id("webservcieEndPointForm:webserviceEndpointTable"), "data-rk")
            .getFirstColumnEntriesForSpanClass("endpoint-entry"))
            .doesNotContainAnyElementsOf(Arrays.asList(links));
  }
  
}
