package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestRestServices {

  private static final String APP = "test";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toApplicationDetail(APP);
    Navigation.toRestServices();
  }

  @Test
  void restSwaggerUi() {
    Selenide.switchTo().frame("apiBrowser");
    $("#select").shouldBe(visible, value(APP));
    $$(".opblock-summary")
        .shouldBe(sizeGreaterThanOrEqual(1));

    Selenide.switchTo().defaultContent();
    $("#configRestBackend").shouldBe(visible).click();
    var configs = new Table(By.id("config:form:configTable"));
    configs.firstColumnShouldBe(CollectionCondition.size(5));
  }
}
