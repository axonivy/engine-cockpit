package ch.ivyteam.enginecockpit.setup;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;

@IvyWebTest
class WebTestMigration {

  @BeforeEach
  void beforeEach() {
    EngineCockpitUtil.disableRestart();
    login("migrate.xhtml");
  }

  @AfterEach
  void afterEach() {
    EngineCockpitUtil.removeRestart();
    login("migrate.xhtml");
  }

  @Test
  void oldEngineInput_empty() {
    $(By.id("locationForm:oldEngineInput")).clear();
    $(By.id("locationForm:oldEngineInput")).shouldHave(exactValue(""));
    $(By.id("locationForm:checkLocation")).shouldBe(visible).click();
    $(By.id("locationForm:oldEngineInputMessage")).shouldHave(text("Value is required"));
    $(By.id("migrateGrowl_container")).shouldBe(empty, not(visible));
  }

  @Test
  void oldEngineInput_notValid() {
    $(By.id("locationForm:oldEngineInput")).clear();
    $(By.id("locationForm:oldEngineInput")).sendKeys("/tmp/notValidPath");
    $(By.id("locationForm:checkLocation")).shouldBe(visible).click();
    $(By.id("locationForm:oldEngineInputMessage")).shouldBe(empty);
    $(By.id("locationForm:migrationChecks")).shouldHave(text("Location does not exist: /tmp/notvalid"));
  }
}
