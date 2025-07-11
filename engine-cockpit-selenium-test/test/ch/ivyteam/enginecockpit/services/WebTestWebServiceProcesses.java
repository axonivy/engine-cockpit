package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

@IvyWebTest
class WebTestWebServiceProcesses {

  private static final String APP = "test";
  private static final String SERVICE_NAME = "TestWebServiceProcessCockpit";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toApplicationDetail(APP);
    Navigation.toWebServiceProcesses();
  }

  @Test
  void webServices() {
    Tab.APP.switchToDefault();
    $("h2").shouldHave(text("Web Service Processes"));

    $$(Tab.APP.activePanelCss + " .webServiceProcessesTable tr a").shouldHave(size(1));

    var table = PrimeUi.table(By.cssSelector(Tab.APP.activePanelCss + " .webServiceProcessesTable"));
    table.contains(SERVICE_NAME);
  }
}
