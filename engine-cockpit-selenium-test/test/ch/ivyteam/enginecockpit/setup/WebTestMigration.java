package ch.ivyteam.enginecockpit.setup;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;

@IvyWebTest
public class WebTestMigration {
  @BeforeEach
  void beforeEach() {
    login("migrate.xhtml");
  }

  @Test
  void oldEngineInput_empty() {
    $(By.id("locationForm:oldEngineInput")).shouldHave(exactValue(""));
    $(By.id("locationForm:checkLocation")).shouldBe(visible).click();
    $(By.id("locationForm:oldEngineInputMessage")).shouldHave(text("Value is required"));
    $(By.id("migrateGrowl_container")).shouldBe(empty, not(visible));
  }

  @Test
  void oldEngineInput_notValid() {
    $(By.id("locationForm:oldEngineInput")).shouldHave(exactValue("")).sendKeys("/tmp/notValidPath");
    $(By.id("locationForm:checkLocation")).shouldBe(visible).click();
    $(By.id("locationForm:oldEngineInputMessage")).shouldBe(empty);
    $(By.id("migrateGrowl_container")).shouldHave(text("Failed to detect engine version of /tmp/notvalid"));
  }

  @Test
  void migrateSameEngine() {
    checkSameEngineLocation();
    startMigration();
    checkMigrationFinished();
  }

  private void checkSameEngineLocation() {
    var userDir = System.getProperty("user.dir") + "/../.ivy-engine";
    $(By.id("locationForm:oldEngineInput")).shouldHave(exactValue("")).sendKeys(userDir);
    $(By.id("locationForm:checkLocation")).shouldBe(visible).click();
    $("h3").shouldHave(text("Migrating:"));
    var migrationStep = $$(".migration-step").shouldBe(sizeGreaterThan(1)).first();
    migrationStep.shouldHave(text("Copy Configuration Files"));
    migrationStep.find(".si").shouldHave(Condition.cssClass("si-navigation-menu-horizontal"));
  }

  private void startMigration() {
    $(By.id("form:startMigration")).shouldBe(visible).click();
    $(By.id("form:migrationRunning")).shouldBe(visible, disabled);
    useOriginWhenCopyingFiles();
  }

  private void useOriginWhenCopyingFiles() {
    while (!originRadios().isEmpty()) {
      for (var radio : originRadios()) {
        radio.click();
      }
    }
  }

  private ElementsCollection originRadios() {
    return $$(By.tagName("input")).filter(Condition.value("Origin"));
  }

  private void checkMigrationFinished() {
    $$(".migration-step").last().find(".si").shouldHave(Condition.cssClass("si-check-circle-1"));
    $(By.id("form:finishMigration")).shouldBe(visible).click();
    $(By.id("finishWizardForm:finishWizardYes")).shouldBe(visible).click();
    assertCurrentUrlContains("system");
  }
}
