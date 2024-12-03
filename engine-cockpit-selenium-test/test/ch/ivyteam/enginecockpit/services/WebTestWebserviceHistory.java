package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

@IvyWebTest
class WebTestWebserviceHistory {
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
  void liveStats() {
    EngineCockpitUtil.assertLiveStats(List.of("Web Service Calls", "Web Service Execution Time"), "test-web", false);
  }

  @Test
  void webServiceExecHistory() {
    Selenide.executeJavaScript("window.scrollTo(0,document.body.scrollHeight);");
    $(By.id("webServiceHistory:execHistoryForm:execHistoryTable_data")).shouldHave(text("http://secure.smartbearsoftware.com:80/samples/testcomplete12/webservices/Service.asmx"));
  }
}
