package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestBackendApi {

  private static final String APP = "test";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toApplicationDetail(APP);
    Navigation.toBackendApi();
  }

  @Test
  void restSwaggerUi() {
    Selenide.switchTo().frame("apiBrowser");
    $("#select").shouldBe(visible, value(APP));
    $$(".opblock-summary")
      .shouldBe(size(2))
      .first()
      .shouldHave(text("GET"), text("/engine​/info"));

    Selenide.switchTo().defaultContent();
    $("#configRestBackend").shouldBe(visible).click();
    var configs = new Table(By.id("config:form:configTable"));
    configs.firstColumnShouldBe(CollectionCondition.size(5));
  }

  @Test
  void webServices() {
    // Service is temporary removed from the engine-cockpit-test-data project,
    // as this will cause another test to fail because of the bug XIVY-5040
    // $$("#webServicesTable_data tr a").shouldBe(size(1));
    var appSwitch = PrimeUi.selectOne(By.id("appSwitch:appSelect"));
    appSwitch.selectedItemShould(text(APP));
    appSwitch.selectItemByLabel("test-ad");
    Selenide.refresh();
    appSwitch.selectedItemShould(text("test-ad"));
  }
}
