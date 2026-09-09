package ch.ivyteam.enginecockpit.application;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.Wait;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

class WebTestApplicationVersion {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toApplicationVersion("demo-portal", "1");
  }

  @Test
  void projectsAreListed() {
    $(By.id("versionProjectsForm:projectsTable")).shouldBe(visible);
    var table = new Table(By.id("versionProjectsForm:projectsTable"));
    table.rows().shouldHave(size(3));
  }

  @Test
  void stateIsShown() {
    $(By.id("activityState")).shouldHave(text("ACTIVE"));
    $(By.id("operationState")).shouldHave(text("STARTED"));
    $(By.id("state:appDetailStateForm:activateApplication")).shouldBe(disabled);
    $(By.id("state:appDetailStateForm:deActivateApplication")).shouldBe(visible).click();
    try {
      $(By.id("activityState")).shouldHave(text("INACTIVE"));
      waitForOperationState("STOPPED");
    } finally {
      $(By.id("state:appDetailStateForm:activateApplication")).shouldBe(visible, enabled).click();
      $(By.id("activityState")).shouldHave(text("ACTIVE"));
      waitForOperationState("STARTED");
    }
  }

  private void waitForOperationState(String state) {
    Wait()
        .withTimeout(Duration.ofSeconds(10))
        .withMessage("Application operation state should be " + state)
        .until(webDriver -> {
          webDriver.navigate().refresh();
          return $(By.id("operationState")).is(exactText(state));
        });
  }
}
