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
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
@TestMethodOrder(MethodOrderer.MethodName.class)
class WebTestWebserviceDetail {
  private static final String WEBSERVICE_NAME = "test-web";

  @BeforeAll
  static void setup() {
    EngineCockpitUtil.runWebService();
  }

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toWebservices();
    Tab.APP.switchToDefault();
    Navigation.toWebserviceDetail(WEBSERVICE_NAME);
  }

  @Test
  void detailOpen() {
    assertCurrentUrlContains("webservicedetail.xhtml?app=" + Tab.DEFAULT_APP + "&env=Default&id=");
    $$(".card").shouldHave(size(3));
    $("#webserviceConfigurationForm\\:name").shouldBe(exactText(WEBSERVICE_NAME));

    $(".layout-topbar-actions .help-dialog").shouldBe(visible).click();
    $("#helpWebserviceDialog\\:helpServicesModal").shouldBe(Condition.visible);
    $(".code-block").shouldBe(text(WEBSERVICE_NAME), text("sensitive: \"${encrypt:*****}\""));
  }

  @Test
  void saveAndResetChanges() {
    setConfiguration("testUser");
    Selenide.refresh();
    checkConfiguration("testUser");
    resetConfiguration();
    Selenide.refresh();
    checkConfiguration("admin");
  }

  private void setConfiguration(String username, String password) {
    $("#webserviceConfigurationForm\\:password").shouldBe(visible).sendKeys(password);
    setConfiguration(username);
  }

  private void setConfiguration(String username) {
    $("#webserviceConfigurationForm\\:username").clear();
    $("#webserviceConfigurationForm\\:username").sendKeys(username);

    $("#webserviceConfigurationForm\\:saveWsConfig").click();
    $("#webserviceConfigurationForm\\:wsConfigMsg_container")
            .shouldBe(text("Web Service configuration saved"));
  }

  private void checkConfiguration(String username) {
    $("#webserviceConfigurationForm\\:username").shouldBe(exactValue(username));
  }

  private void resetConfiguration() {
    $("#webserviceConfigurationForm\\:resetConfig").click();
    $("#webserviceConfigurationForm\\:resetWsConfirmDialog").shouldBe(visible);
    $("#webserviceConfigurationForm\\:resetWsConfirmYesBtn").click();
    $("#webserviceConfigurationForm\\:wsConfigMsg_container")
            .shouldBe(text("Web Service configuration reset"));
  }

  @Test
  void wsEndpointTestConnection() {
    setEndPoint("http://test-webservices.ivyteam.io:8080/notfound");
    testAndAssertConnection("Status 404");

    setEndPoint("http://test-webservices.ivyteam.io:91");
    testAndAssertConnection("Status 401");

    setConfiguration("admin", "nimda");
    testAndAssertConnection("Status 200");

    resetConfiguration();
  }

  private void testAndAssertConnection(String msg) {
    $("#connResult\\:connectionTestModel").shouldNotBe(visible);
    Table table = new Table(By.id("webservcieEndPointForm:webserviceEndpointTable"), "", "data-rk");
    table.clickButtonForEntry(table.getFirstColumnEntriesForSpanClass("endpoint-entry").get(1),
            "testWsEndpointBtn");
    $("#connResult\\:connectionTestModel").shouldBe(visible);
    $("#connResult\\:connTestForm\\:testConnectionBtn").click();
    $("#connResult\\:connTestForm\\:resultLog_content").shouldBe(text(msg));
    $("#connResult\\:connTestForm\\:closeConTesterDialog").click();
    $("#connResult\\:connectionTestModel").shouldNotBe(visible);
  }

  @Test
  void editEndpointsInvalid() {
    new Table(By.id("webservcieEndPointForm:webserviceEndpointTable"), "", "data-rk")
            .clickButtonForEntry("SampleWebServiceSoap", "editEndpointBtn");
    $("#editEndpointModalForm\\:defaultInput").clear();
    $("#editEndpointModalForm\\:saveEndpoint").click();
    $("#editEndpointModalForm\\:defaultInputMessage").shouldBe(text("Value is required"));
  }

  @Test
  void setAndResetEndpoints() {
    setEndPoint("default", "first", "second");
    checkEndPoint("default", "first", "second");
    checkEndPointDoesNotContain("localhost", "localhost/test");

    Selenide.refresh();
    checkEndPoint("default", "first", "second");
    checkEndPointDoesNotContain("localhost", "localhost/test");

    resetConfiguration();    
    checkEndPoint("localhost", "localhost/test");
    checkEndPointDoesNotContain("default", "first", "second");
  }

  @Test
  void setAndResetEndpoints_noFallbacks() {
    setEndPoint("default");
    checkEndPoint("default");
    checkEndPointDoesNotContain("localhost", "localhost/test");

    Selenide.refresh();
    checkEndPoint("default");
    checkEndPointDoesNotContain("localhost", "localhost/test");

    resetConfiguration();
    checkEndPoint("localhost", "localhost/test");
    checkEndPointDoesNotContain("default");
  }

  @Test
  void properties() {
    var table = PrimeUi.table(By.id("webservcieAdditionalConfigForm:webservicePropertiesTable"));
    table.row(0).shouldHave(text("password"), text("*****"));
    table.row(1).shouldHave(text("sensitive"), text("*****"));
    table.row(2).shouldHave(text("username"), text("admin"));
  }

  @Test
  void liveStats() {
    EngineCockpitUtil.assertLiveStats(List.of("Web Service Calls", "Web Service Execution Time"),
            "Default > test-web", false);
  }

  private void setEndPoint(String defaultLink, String... fallbacks) {
    new Table(By.id("webservcieEndPointForm:webserviceEndpointTable"), "", "data-rk")
            .clickButtonForEntry("SampleWebServiceSoap", "editEndpointBtn");
    $("#editEndpointModal").shouldBe(visible);

    $("#editEndpointModalForm\\:defaultInput").clear();
    $("#editEndpointModalForm\\:defaultInput").sendKeys(defaultLink);
    $("#editEndpointModalForm\\:fallBackInput").clear();

    $("#editEndpointModalForm\\:fallBackInput")
            .sendKeys(Arrays.stream(fallbacks).collect(Collectors.joining("\n")));

    $("#editEndpointModalForm\\:saveEndpoint").click();
    $("#webserviceConfigurationForm\\:wsConfigMsg_container").shouldBe(text("EndPoint saved"));
  }

  private void checkEndPoint(String... links) {
    assertThat(new Table(By.id("webservcieEndPointForm:webserviceEndpointTable"), "", "data-rk")
            .getFirstColumnEntriesForSpanClass("endpoint-entry"))
                    .containsAll(Arrays.asList(links));
  }

  private void checkEndPointDoesNotContain(String... links) {
    assertThat(new Table(By.id("webservcieEndPointForm:webserviceEndpointTable"), "", "data-rk")
            .getFirstColumnEntriesForSpanClass("endpoint-entry"))
                    .doesNotContainAnyElementsOf(Arrays.asList(links));
  }

}
