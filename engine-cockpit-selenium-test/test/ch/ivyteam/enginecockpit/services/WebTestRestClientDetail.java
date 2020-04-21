package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlEndsWith;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

@IvyWebTest
public class WebTestRestClientDetail
{
  private static final String RESTCLIENT_NAME = "test-rest";
  
  @BeforeEach
  void beforeEach()
  {
    login();
    Navigation.toRestClients();
    Tab.switchToDefault();
    Navigation.toRestClientDetail(RESTCLIENT_NAME);
  }
  
  @Test
  void testDetailOpen()
  {
    assertCurrentUrlEndsWith("restclientdetail.xhtml?restClientName=" + RESTCLIENT_NAME);
    $$(".ui-panel").shouldHave(size(2));
    $("#restClientConfigurationForm\\:name").shouldBe(exactText(RESTCLIENT_NAME));
    
    $("#breadcrumbOptions > a[href='#']").shouldBe(visible).click();
    $("#helpRestClientDialog\\:helpServicesModal").shouldBe(visible);
    $(".code-block").shouldBe(text(RESTCLIENT_NAME));
  }
  
  @Test
  void testRestTestConnection()
  {
    setConfiguration("localhost", "");
    Selenide.refresh();
    testAndAssertConnection("Invalid Url");

    setConfiguration("http://test-webservices.ivyteam.io:8080/testnotfound", "");
    Selenide.refresh();
    testAndAssertConnection("Status 404");
    
    setConfiguration("http://test-webservices.ivyteam.io:91/", "");
    Selenide.refresh();
    testAndAssertConnection("Status 401");
    
    setConfiguration("http://test-webservices.ivyteam.io:91/", "admin", "nimda");
    Selenide.refresh();
    testAndAssertConnection("Status 200");

    resetConfiguration();
  }

  private void testAndAssertConnection(String msg)
  {
    $("#connResult\\:connectionTestModel").shouldNotBe(visible);
    $("#restClientConfigurationForm\\:testRestBtn").shouldBe(visible).click();
    $("#connResult\\:connectionTestModel").shouldBe(visible);
    $("#connResult\\:connTestForm\\:testConnectionBtn").click();
    $("#connResult\\:connTestForm\\:resultLog_content").shouldBe(text(msg));
    $("#connResult\\:connectionTestModel > div > a").click();
    $("#connResult\\:connectionTestModel").shouldNotBe(visible);
  }
  
  @Test
  void testSaveAndResetChanges()
  {
    setConfiguration("url", "testUser");
    Selenide.refresh();
    checkConfiguration("url", "testUser");
    resetConfiguration();
    Selenide.refresh();
    checkConfiguration("http://localhost/", "admin");
  }
  
  private void setConfiguration(String url, String username, String password)
  {
    $("#restClientConfigurationForm\\:password").shouldBe(visible).sendKeys(password);
    setConfiguration(url, username);
  }

  private void setConfiguration(String url, String username)
  {
    $("#restClientConfigurationForm\\:url").shouldBe(visible).clear();
    $("#restClientConfigurationForm\\:url").sendKeys(url);
    
    $("#restClientConfigurationForm\\:username").clear();
    $("#restClientConfigurationForm\\:username").sendKeys(username);
    
    $("#restClientConfigurationForm\\:saveRestConfig").click();
    $("#restClientConfigurationForm\\:restConfigMsg_container").shouldBe(text("Rest configuration saved"));
  }
  
  private void checkConfiguration(String url, String username)
  {
    $("#restClientConfigurationForm\\:url").shouldBe(exactValue(url));
    $("#restClientConfigurationForm\\:username").shouldBe(exactValue(username));
  }
  
  private void resetConfiguration()
  {
    $("#restClientConfigurationForm\\:resetConfig").click();
    $("#restClientConfigurationForm\\:resetRestConfirmDialog").shouldBe(visible);
    $("#restClientConfigurationForm\\:resetRestConfirmYesBtn").click();
    $("#restClientConfigurationForm\\:restConfigMsg_container").shouldBe(text("Rest configuration reset"));
  }
  
}
