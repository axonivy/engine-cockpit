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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.FeatureEditor;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.PropertyEditor;
import ch.ivyteam.enginecockpit.util.Tab;

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
    $("#helpRestClientDialog\\:helpServicesModal").shouldBe(visible);
    $(".code-block").shouldBe(text(RESTCLIENT_NAME), text("sensitive: \"${encrypt:*****}\""));
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
    var table = PrimeUi.table(By.id("restClientAdditionalConfigForm:restClientPropertiesTable"));
    table.row(0).shouldHave(text("PATH.host"), text("test-webservices.ivyteam.io"));
    table.row(1).shouldHave(text("password"), text("*****"));
    table.row(2).shouldHave(text("JSON.Deserialization.FAIL_ON_UNKNOWN_PROPERTIES"), text("false"));
    table.row(3).shouldHave(text("sensitive"), text("*****"));
    table.row(4).shouldHave(text("PATH.port"), text("91"));
    table.row(5).shouldHave(text("username"), text("admin"));
  }

  @Test
  void addProperty() {
    var editor = new PropertyEditor("restClientAdditionalConfigForm:restClientPropertiesTable:newPropertyEditor:");
    editor.addProperty("testProperty", "testValue");
    var table = PrimeUi.table(By.id("restClientAdditionalConfigForm:restClientPropertiesTable"));
    table.row(2).shouldHave(text("testProperty"), text("testValue"));
    $(By.id("restClientAdditionalConfigForm:restClientPropertiesTable:2:editPropertyEditor:deletePropertyBtn")).click();
  }

  @Test
  void editProperty() {
    var editor = new PropertyEditor("restClientAdditionalConfigForm:restClientPropertiesTable:5:editPropertyEditor:");
    editor.editProperty("editValue");
    var table = PrimeUi.table(By.id("restClientAdditionalConfigForm:restClientPropertiesTable"));
    table.row(5).shouldHave(text("username"), text("editValue"));
    editor.editProperty("admin");
  }

  @Test
  void addFeature() {
    var editor = new FeatureEditor("restClientAdditionalConfigForm:restClientFeaturesTable:");
    editor.addFeature("ch.ivyteam.ivy.rest.client.feature.AuthFeature");
    var table = PrimeUi.table(By.id("restClientAdditionalConfigForm:restClientFeaturesTable"));
    table.row(2).shouldHave(text("ch.ivyteam.ivy.rest.client.feature.AuthFeature"));
    $(By.id("restClientAdditionalConfigForm:restClientFeaturesTable:2:editFeatureEditor:deleteFeatureBtn")).click();
  }

  @Test
  void editFeature() {
    var editor = new FeatureEditor("restClientAdditionalConfigForm:restClientFeaturesTable:");
    editor.addFeature("ch.ivyteam.ivy.rest.client.feature.AuthFeature");
    editor.editFeature("ch.ivyteam.ivy.rest.client.feature.editFeature", 2, 1);
    var table = PrimeUi.table(By.id("restClientAdditionalConfigForm:restClientFeaturesTable"));
    table.row(2).shouldHave(text("ch.ivyteam.ivy.rest.client.feature.editFeature"));
    $(By.id("restClientAdditionalConfigForm:restClientFeaturesTable:2:editFeatureEditor:deleteFeatureBtn")).click();
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
