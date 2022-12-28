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

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

@IvyWebTest
class WebTestRestClientDetail {
  private static final String RESTCLIENT_NAME = "test-rest";

  @BeforeEach
  void beforeEach() {
    navigateToRestDetail();
  }

  private void navigateToRestDetail() {
    login();
    Navigation.toRestClients();
    Tab.APP.switchToDefault();
    Navigation.toRestClientDetail(RESTCLIENT_NAME);
  }

  @Test
  void detailOpen() {
    assertCurrentUrlContains("restclientdetail.xhtml?app=" + Tab.DEFAULT_APP + "&name=" + RESTCLIENT_NAME);
    $$(".card").shouldHave(size(2));
    $("#restClientConfigurationForm\\:name").shouldBe(exactText(RESTCLIENT_NAME));

    $(".layout-topbar-actions .help-dialog").shouldBe(visible).click();
    $("#helpRestClientDialog\\:helpServicesModal").shouldBe(visible);
    $(".code-block").shouldBe(text(RESTCLIENT_NAME), text("sensitive: \"${encrypt:*****}\""));
  }

  @Test
  void restTestConnection() {
    setUrl("localhost");
    setUserName("");
    testAndAssertConnection("Invalid Url");

    setUrl("http://test-webservices.ivyteam.io:8080/testnotfound");
    testAndAssertConnection("Status 404");

    setUrl("http://test-webservices.ivyteam.io:91/");
    testAndAssertConnection("Status 401");

    setUserName("admin");
    setPassword("nimda");
    testAndAssertConnection("Status 200");

    setUrl("http://{host}:{port}/");
    testAndAssertConnection("Status 200");

    resetConfiguration();
  }

  private void testAndAssertConnection(String msg) {
    $("#connResult\\:connectionTestModel").shouldNotBe(visible);
    $("#restClientConfigurationForm\\:testRestBtn").shouldBe(visible).click();
    $("#connResult\\:connectionTestModel").shouldBe(visible);
    $("#connResult\\:connTestForm\\:testConnectionBtn").click();
    $("#connResult\\:connTestForm\\:resultLog_content").shouldBe(text(msg));
    $("#connResult\\:connectionTestModel > div > a").click();
    $("#connResult\\:connectionTestModel").shouldNotBe(visible);
  }

  @Test
  void saveAndResetChanges() {
    setConfiguration("url", "testUser");
    Selenide.refresh();
    checkConfiguration("url", "testUser");
    resetConfiguration();
    Selenide.refresh();
    checkConfiguration("http://test-webservices.ivyteam.io:8090/api/v3", "admin");
  }

  @Test
  void properties() {
    var table = PrimeUi.table(By.id("restClientAdditionalConfigForm:restClientPropertiesTable"));
    table.row(0).shouldHave(text("PATH.host"), text("test-webservices.ivyteam.io"));
    table.row(1).shouldHave(text("password"), text("*****"));
    table.row(2).shouldHave(text("JSON.Deserialization.FAIL_ON_UNKNOWN_PROPERTIES"), text("false"));
    table.row(3).shouldHave(text("sensitive"), text("*****"));
    table.row(4).shouldHave(text("PATH.port"), text("91"));
    table.row(5).shouldHave(text("username"), text("admin"));
  }

  @Test
  void liveStats() {
    EngineCockpitUtil.runRestClient();
    navigateToRestDetail();
    EngineCockpitUtil.assertLiveStats(List.of("REST Client Connections", "REST Client Calls",
            "REST Client Execution Time"), "test-rest", false);
  }

  private void setConfiguration(String url, String username) {
    setUrl(url);
    setUserName(username);
    $("#restClientConfigurationForm\\:saveRestConfig").click();
    $("#restClientConfigurationForm\\:restConfigMsg_container").shouldBe(text("Rest configuration saved"));
  }

  private void setUrl(String url) {
    $("#restClientConfigurationForm\\:url").shouldBe(visible).clear();
    $("#restClientConfigurationForm\\:url").sendKeys(url);
  }

  private void setUserName(String username) {
    $("#restClientConfigurationForm\\:username").clear();
    $("#restClientConfigurationForm\\:username").sendKeys(username);
  }

  private void setPassword(String password) {
    $("#restClientConfigurationForm\\:password").shouldBe(visible).sendKeys(password);
  }

  private void checkConfiguration(String url, String username) {
    $("#restClientConfigurationForm\\:url").shouldBe(exactValue(url));
    $("#restClientConfigurationForm\\:username").shouldBe(exactValue(username));
  }

  private void resetConfiguration() {
    $("#restClientConfigurationForm\\:resetConfig").click();
    $("#restClientConfigurationForm\\:resetRestConfirmDialog").shouldBe(visible);
    $("#restClientConfigurationForm\\:resetRestConfirmYesBtn").click();
    $("#restClientConfigurationForm\\:restConfigMsg_container").shouldBe(text("Rest configuration reset"));
  }
}
