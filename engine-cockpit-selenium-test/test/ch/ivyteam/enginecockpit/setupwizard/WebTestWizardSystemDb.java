package ch.ivyteam.enginecockpit.setupwizard;

import static com.codeborne.selenide.Condition.enabled;
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
    driver.quit();
  }
  
  @Test
  void testWebServerStep()
  {
    navigateToSystemDbWizardStep();
    $("#sysDbNextStep").shouldBe(enabled);
    WebTestSystemDb.assertDefaultValues();
    WebTestSystemDb.assertSystemDbCreationDialog();
    WebTestSystemDb.assertSystemDbCreation();
    $("#sysDbNextStep").shouldBe(enabled);
    $("#sysDbNextStep").click();
    $("#sysDbNextStepModel").shouldBe(visible);
    $("#sysDbNextStepForm\\:licNextStepDemoYes").click();
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
    login("setup.xhtml");
    WebTestWizardLicence.skipLicStep();
    WebTestWizardAdmins.skipAdminStep();
    WebTestWizardWebServer.skipWebserverStep();
    $("#wizardSteps li.ui-state-highlight").shouldBe(text("System Database"));
  }
}
