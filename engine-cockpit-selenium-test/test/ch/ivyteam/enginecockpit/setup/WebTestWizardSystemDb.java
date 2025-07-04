package ch.ivyteam.enginecockpit.setup;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.createOldDb;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.deleteTempDb;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.resetConfig;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.system.WebTestSystemDb;
import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;

@IvyWebTest
class WebTestWizardSystemDb {

  @BeforeAll
  static void setup() {
    createOldDb();
  }

  @BeforeEach
  void beforeEach() {
    EngineCockpitUtil.disableRestart();
    WebTestWizard.navigateToStep("System Database");
  }

  @AfterEach
  void afterEach() {
    resetConfig();
    deleteTempDb();
    EngineCockpitUtil.removeRestart();
  }

  @Test
  void webServerStep() {
    WebTestSystemDb.assertDefaultValues();
    WebTestSystemDb.assertSystemDbCreationDialog();
    WebTestWizard.activeStepShouldHaveWarnings();
    WebTestSystemDb.assertSystemDbCreation();
    WebTestWizard.finish();
    $("#configErrorMessage").shouldBe(visible, text("LICENCE"));
    $("#finishWizardForm\\:finishWizardYes").click();
    $(WebTestWizard.ACTIVE_WIZARD_STEP).shouldNot(exist);
    assertCurrentUrlContains("system");
  }

  @Test
  void connectionResults() {
    WebTestSystemDb.assertConnectionResults();
  }

  @Test
  void oldDbConversionNeeded() {
    WebTestSystemDb.assertSystemDbConversionDialog();
  }

  @Test
  void uiLogicSwitchesAndDefaults() {
    WebTestSystemDb.assertDatabaseTypeSwitch();
    WebTestSystemDb.assertDefaultPortSwitch();
    WebTestSystemDb.assertAdditionalProperties();
  }
}
