package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.anyMatch;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

@IvyWebTest
class WebTestHealthWarning {

  @BeforeEach
  void beforeEach() {
    login();
  }

  @Test
  void messages() {
    $(By.id("health-check-badge")).shouldHave(matchText("\\d{1}"));
    var health = $(".health-messages");
    health.shouldBe(visible).click();
    var messages= health.$$("li");
    messages.shouldHave(
            anyMatch("message contains", e -> e.getText().contains("Release Candidate")),
            anyMatch("message contains", e -> e.getText().contains("Demo Mode")),
            anyMatch("message contains", e -> e.getText().contains("Show health details")));
  }

  @Test
  void messageDemo() {
    var health = $(".health-messages");
    health.shouldBe(visible).click();
    var message = health.$(By.partialLinkText("Demo Mode"));
    message.$("i").shouldHave(cssClass("health-low"));
    message.click();
    assertCurrentUrlContains("setup.xhtml");
  }

  @Test
  void messageDetails() {
    var health = $(".health-messages");
    health.shouldBe(visible).click();
    var message = health.$(By.partialLinkText("Show health details"));
    message.click();
    assertCurrentUrlContains("monitor-health.xhtml");
  }
}
