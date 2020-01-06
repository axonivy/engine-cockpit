package ch.ivyteam.enginecockpit.setupwizard;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.system.WebTestSystemDb;

public class WebTestWizardSystemDb extends WebTestBase
{
  
  @AfterEach
  void cleanup()
  {
    resetConfig();
    deleteTempDb();
  }
  
  @Test
  void testWebServerStep()
  {
    navigateToSystemDbWizardStep();
    WebTestWizard.activeStepShouldBeOk();
    WebTestSystemDb.assertDefaultValues();
    WebTestSystemDb.assertSystemDbCreationDialog();
    WebTestWizard.activeStepShouldHaveWarnings();
    WebTestSystemDb.assertSystemDbCreation();
    WebTestWizard.finishWizard();
    $("#configErrorMessage").shouldBe(visible, text("LICENCE"));
    $("#finishWizardForm\\:finishWizardYes").click();
    assertCurrentUrlContains("info");
  }
  
  @Test
  void testConnectionResults()
  {
    navigateToSystemDbWizardStep();
    WebTestSystemDb.assertConnectionResults();
  }
  
  @Test
  void testOldDbConversionNeeded()
  {
    createOldDb();
    navigateToSystemDbWizardStep();
    WebTestSystemDb.assertSystemDbConversionDialog();
  }
  
  @Test
  void testUiLogicSwitchesAndDefaults()
  {
    navigateToSystemDbWizardStep();
    WebTestSystemDb.assertDatabaseTypeSwitch();
    WebTestSystemDb.assertDefaultPortSwitch();
    WebTestSystemDb.assertAdditionalProperties();
  }

  private void navigateToSystemDbWizardStep()
  {
    WebTestWizard.navigateToStep("System Database");
  }
}
