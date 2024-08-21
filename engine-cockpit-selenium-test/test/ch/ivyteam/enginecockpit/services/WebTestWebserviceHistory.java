package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.runWebService;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestWebserviceHistory {

  @BeforeEach
  void beforeEach() {
	runWebService();
    login();
    Navigation.toWebservices();
    Tab.APP.switchToTab("test");
    Navigation.toRestClientDetail("test-web");
  }

  @AfterEach
  void cleanup() {
    EngineCockpitUtil.resetConfig();
  }

  @Test
  void history() {
    Selenide.executeJavaScript("window.scrollTo(0,document.body.scrollHeight);");
    new Table(By.id("webServiceHistory:execHistoryForm:execHistoryTable	"))
            .firstColumnShouldBe(sizeGreaterThan(0));
  }
}
