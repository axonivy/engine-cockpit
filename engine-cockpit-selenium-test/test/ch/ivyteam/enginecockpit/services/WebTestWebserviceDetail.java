package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.executeJs;
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

import ch.ivyteam.enginecockpit.util.FeatureEditor;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.PropertyEditor;
import ch.ivyteam.enginecockpit.util.Tab;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestWebserviceDetail {
  private static final String WEBSERVICE_NAME = "test-web";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toWebservices();
    Tab.APP.switchToDefault();
    Navigation.toWebserviceDetail(WEBSERVICE_NAME);
  }

  @Test
  void detailOpen() {
    assertCurrentUrlContains("webservicedetail.xhtml?app=" + Tab.DEFAULT_APP + "&id=");
    $$(".card").shouldHave(size(4));
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

    executeJs("scroll(0,0);");
    $("#webserviceConfigurationForm\\:saveWsConfig").click();
    $("#webserviceConfigurationForm\\:wsConfigMsg_container")
        .shouldBe(text("Web Service configuration saved"));
  }

  private void checkConfiguration(String username) {
    $("#webserviceConfigurationForm\\:username").shouldBe(exactValue(username));
  }

  private void resetConfiguration() {
    $("#webserviceConfigurationForm\\:resetConfig").scrollIntoView(false).click();
    $("#webserviceConfigurationForm\\:resetWsConfirmDialog").shouldBe(visible);
    $("#webserviceConfigurationForm\\:resetWsConfirmYesBtn").click();
    $("#webserviceConfigurationForm\\:wsConfigMsg_container")
        .shouldBe(text("Web Service configuration reset"));
  }

  @Test
  void wsEndpointTestConnection() {
    setEndPoint("http://localhost");
    testAndAssertConnection("Error", "The URL seems to be not correct or contains scripting context (can not be evaluated)");

    setEndPoint("http://test-webservices.ivyteam.io:8080/notfound");
    testAndAssertConnection("Warning", "Status 404 Not Found");

    setEndPoint("http://test-webservices.ivyteam.io:91");
    testAndAssertConnection("Warning", "Status 401 Unauthorized");

    setConfiguration("admin", "nimda");
    testAndAssertConnection("Success", "Status 200 OK");

    resetConfiguration();
  }

  private void testAndAssertConnection(String title, String msg) {
    $("#connResult\\:connectionTestModel").shouldNotBe(visible);
    Table table = new Table(By.id("webservcieEndPointForm:webserviceEndpointTable"), "", "data-rk");
    table.clickButtonForEntry(table.getFirstColumnEntriesForSpanClass("endpoint-entry").get(1),
        "testWsEndpointBtn");
    $("#connResult\\:connectionTestModel").shouldBe(visible);
    $("#connResult\\:connTestForm\\:testConnectionBtn").click();
    $("#connResult\\:connTestForm\\:resultConnect").shouldBe(text(title));
    $("#connResult\\:connTestForm\\:resultConnect").shouldBe(text(msg));
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
    var table = new Table(By.id("webserviceAdditionalConfigForm:webservicePropertiesTable"));
    table.row("password").shouldHave(text("*****"));
    table.row("sensitive").shouldHave(text("*****"));
    table.row("username").shouldHave(text("admin"));
  }

  @Test
  void addProperty() {
    var editor = new PropertyEditor("webserviceAdditionalConfigForm:webservicePropertiesTable");
    editor.addProperty("testProperty", "testValue");
    editor.deleteProperty("testProperty");
  }

  @Test
  void editProperty() {
    var editor = new PropertyEditor("webserviceAdditionalConfigForm:webservicePropertiesTable");
    editor.editProperty("username","editValue");
    editor.editProperty("username","admin");
  }

  @Test
  void addFeature() {
    var editor = new FeatureEditor("webserviceAdditionalConfigForm:webserviceFeaturesTable");
    editor.addFeature("TestFeature");
    editor.deleteFeature("TestFeature");
  }

  @Test
  void editFeature() {
    var editor = new FeatureEditor("webserviceAdditionalConfigForm:webserviceFeaturesTable");
    editor.addFeature("TestFeature");
    editor.editFeatureSave("TestFeature", "EditFeature");
    editor.deleteFeature("EditFeature");
  }

  @Test
  void addExistingFeature() {
    var editor = new FeatureEditor("webserviceAdditionalConfigForm:webserviceFeaturesTable");
    editor.addFeature("TestFeature");
    editor.addFeature("TestFeature");
    $(By.id("webserviceAdditionalConfigForm:msg_container")).shouldHave(text("TestFeature couldn't be created"));
    editor.deleteFeature("TestFeature");
  }

  @Test
  void editFeatureCancel() {
    var editor = new FeatureEditor("webserviceAdditionalConfigForm:webserviceFeaturesTable");
    editor.addFeature("TestFeature");
    editor.editFeatureCancel("TestFeature");
    editor.deleteFeature("TestFeature");
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
