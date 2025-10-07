package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.FeatureEditor;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.PropertyEditor;
import ch.ivyteam.enginecockpit.util.Tab;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestRestClientDetail {
  private static final String RESTCLIENT_NAME = "test-rest";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toRestClients();
    Tab.APP.switchToDefault();
    Navigation.toRestClientDetail(RESTCLIENT_NAME);
  }

  @Test
  void detailOpen() {
    assertCurrentUrlContains("restclientdetail.xhtml?app=" + Tab.DEFAULT_APP + "&name=" + RESTCLIENT_NAME);
    $$(".card").shouldHave(size(3));
    $("#restClientConfigurationForm\\:name").shouldBe(exactText(RESTCLIENT_NAME));

    $(".layout-topbar-actions .help-dialog").shouldBe(visible).click();
    $(By.id("helpRestClientDialog:helpServicesModal")).shouldBe(visible);
    $(By.id("helpRestClientDialog:helpServicesForm:codeBlock")).shouldBe(value(RESTCLIENT_NAME), value("sensitive: \"${encrypt:*****}\""));
  }

  @Test
  void restTestConnection() {
    setUrl("http://localhost");
    setUserName("");
    testAndAssertConnection("Error", "Invalid Url");

    setUrl("http://test-webservices.ivyteam.io:8080/testnotfound");
    testAndAssertConnection("Error", "Status 404 Not Found");

    setUrl("http://test-webservices.ivyteam.io:8080/test-rest-service/webapi/status/400");
    testAndAssertConnection("Warning", "Status 400 Bad Request");

    setUrl("http://test-webservices.ivyteam.io:91/");
    testAndAssertConnection("Warning", "Status 401 Unauthorized");

    setUserName("admin");
    setPassword("nimda");
    testAndAssertConnection("Success", "Status 200 OK");

    setUrl("http://{host}:{port}/");
    testAndAssertConnection("Success", "Status 200 OK");

    resetConfiguration();
  }

  @Test
  void restTestSecondRestConnection() {
    Navigation.toRestClientDetail("second-rest");
    $(By.id("restClientAdditionalConfigForm:restClientFeaturesTable")).shouldHave(text("MyFakeOAuthFeature"));
    testAndAssertConnection("Error", "Invalid Url");
    Navigation.toRestClientDetail(RESTCLIENT_NAME);
  }

  private void testAndAssertConnection(String title, String msg) {
    $("#connResult\\:connectionTestModel").shouldNotBe(visible);
    $("#restClientConfigurationForm\\:testRestBtn").shouldBe(visible).click();
    $("#connResult\\:connectionTestModel").shouldBe(visible);
    $("#connResult\\:connTestForm\\:testConnectionBtn").click();
    $("#connResult\\:connTestForm\\:resultConnect").shouldBe(text(title));
    $("#connResult\\:connTestForm\\:resultConnect").shouldBe(text(msg));
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
    var table = new Table(By.id("restClientAdditionalConfigForm:restClientPropertiesTable"));
    table.row("PATH.host").shouldHave(text("test-webservices.ivyteam.io"));
    table.row("password").shouldHave(text("*****"));
    table.row("JSON.Deserialization.FAIL_ON_UNKNOWN_PROPERTIES").shouldHave(text("false"));
    table.row("sensitive").shouldHave(text("*****"));
    table.row("PATH.port").shouldHave(text("91"));
    table.row("username").shouldHave(text("admin"));
  }

  @Test
  void addProperty() {
    var editor = new PropertyEditor("restClientAdditionalConfigForm:restClientPropertiesTable");
    editor.addProperty("testProperty", "testValue");
    editor.deleteProperty("testProperty");
  }

  @Test
  void editProperty() {
    var editor = new PropertyEditor("restClientAdditionalConfigForm:restClientPropertiesTable");
    editor.editProperty("username", "editValue");
    editor.editProperty("username", "admin");
  }

  @Test
  void addFeature() {
    var editor = new FeatureEditor("restClientAdditionalConfigForm:restClientFeaturesTable");
    editor.addFeature("TestFeature");
    editor.deleteFeature("TestFeature");
  }

  @Test
  void editFeature() {
    var editor = new FeatureEditor("restClientAdditionalConfigForm:restClientFeaturesTable");
    editor.addFeature("TestFeature");
    editor.editFeatureSave("TestFeature", "EditFeature");
    editor.deleteFeature("EditFeature");
  }

  @Test
  void addExistingFeature() {
    var editor = new FeatureEditor("restClientAdditionalConfigForm:restClientFeaturesTable");
    editor.addFeature("TestFeature");
    editor.addFeature("TestFeature");
    $(By.id("restClientAdditionalConfigForm:msg_container")).shouldHave(text("TestFeature couldn't be created"));
    editor.deleteFeature("TestFeature");
  }

  @Test
  void editFeatureCancel() {
    var editor = new FeatureEditor("restClientAdditionalConfigForm:restClientFeaturesTable");
    editor.addFeature("TestFeature");
    editor.editFeatureCancel("TestFeature");
    editor.deleteFeature("TestFeature");
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
