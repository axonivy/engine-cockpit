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
class WebTestRestClientHistory {
  private static final String RESTCLIENT_NAME = "engine-rest";

  @BeforeAll
  static void setup() {
    EngineCockpitUtil.runRestClient();
  }

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toRestClients();
    Tab.APP.switchToDefault();
    Navigation.toRestClientDetail(RESTCLIENT_NAME);
  }

  @Test
  void liveStats() {
    EngineCockpitUtil.assertLiveStats(List.of("REST Client Connections", "REST Client Calls",
            "REST Client Execution Time"), "engine-rest", false);
  }

  @Test
  void restExecHistory() {
    Selenide.executeJavaScript("window.scrollTo(0,document.body.scrollHeight);");
    $(By.id("restClientHistory:execHistoryForm:execHistoryTable_data")).shouldHave(text("test/info"));
  }
}
