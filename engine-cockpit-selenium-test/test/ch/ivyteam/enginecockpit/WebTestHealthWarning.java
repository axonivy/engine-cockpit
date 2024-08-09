package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

@IvyWebTest
public class WebTestHealthWarning {

  @BeforeEach
  void beforeEach() {
    login();
  }

  @Test
  void badge() {
    $(By.id("health-check-badge")).shouldHave(text("2"));  
  }
  
  @Test
  void messages() {
    var health = $(".health-messages");
    health.shouldBe(visible).click();
    var messages= health.$$("li");
    messages.shouldHave(size(3));
    
    var first = messages.get(0);
    first.$("i").shouldHave(cssClass("health-high"));
    first.$("span").shouldHave(text("Release Candidate"));

    var second = messages.get(1);
    second.$("i").shouldHave(cssClass("health-low"));
    second.$("span").shouldHave(text("Demo Mode"));

    var third = messages.get(2);
    third.shouldHave(text("Show health details ..."));
  }

  @Test
  void messageLink() {
    var health = $(".health-messages");
    health.shouldBe(visible).click();
    var messages= health.$$("li");
    var demoMode = messages.get(1);
    demoMode.click();
    assertCurrentUrlContains("setup-intro.xhtml");
  }

  @Test
  void showDetails() {
    var health = $(".health-messages");
    health.shouldBe(visible).click();
    var messages= health.$$("li");
    var showDetails = messages.get(2);
    showDetails.click();
    assertCurrentUrlContains("monitor-health.xhtml");
  }

}
