package ch.ivyteam.enginecockpit.setupwizard;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.system.WebTestSystemDb;

public class WebTestWizardSystemDb extends WebTestBase
{
  
  @AfterEach
  void cleanup()
  {
    resetConfig(driver);
    deleteTempDb(driver);
  }
  
  @Test
  void testWebServerStep()
  {
    navigateToSystemDbWizardStep();
    webAssertThat(() -> assertThat(driver.findElementById("sysDbNextStep").isEnabled()).isTrue());
    WebTestSystemDb.assertDefaultValues(driver);
    WebTestSystemDb.assertSystemDbCreationDialog(driver);
    WebTestSystemDb.assertSystemDbCreation(driver);
    webAssertThat(() -> assertThat(driver.findElementById("sysDbNextStep").isEnabled()).isTrue());
    driver.findElementById("sysDbNextStep").click();
    webAssertThat(() -> assertThat(driver.findElementById("sysDbNextStepModel").isDisplayed()).isTrue());
    driver.findElementById("sysDbNextStepForm:licNextStepDemoYes").click();
    webAssertThat(() -> assertThat(driver.getCurrentUrl()).contains("info"));
  }
  
  @Test
  void testConnectionResults()
  {
    navigateToSystemDbWizardStep();
    WebTestSystemDb.assertConnectionResults(driver);
  }
  
  @Test
  void testOldDbConversionNeeded()
  {
    createOldDb(driver);
    navigateToSystemDbWizardStep();
    WebTestSystemDb.assertSystemDbConversionDialog(driver);
  }
  
  @Test
  void testUiLogicSwitchesAndDefaults()
  {
    navigateToSystemDbWizardStep();
    WebTestSystemDb.assertDatabaseTypeSwitch(driver);
    WebTestSystemDb.assertDefaultPortSwitch(driver);
    WebTestSystemDb.assertAdditionalProperties(driver);
  }

  private void navigateToSystemDbWizardStep()
  {
    login("setup.xhtml");
    WebTestWizardLicence.skipLicStep(driver);
    WebTestWizardAdmins.skipAdminStep(driver);
    WebTestWizardWebServer.skipWebserverStep(driver);
    saveScreenshot("sysdb");
    webAssertThat(() -> assertThat(driver.findElementByCssSelector("#wizardSteps li.ui-state-highlight").getText())
            .contains("System Database"));
  }
}
