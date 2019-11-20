package ch.ivyteam.enginecockpit.setupwizard;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.services.WebTestSystemDb;

public class WebTestWizardSystemDb extends WebTestBase
{
  
  @AfterEach
  void cleanup()
  {
    resetConfig(driver);
  }
  
  @Test
  void testWebServerStep()
  {
    navigateToSystemDbWizardStep();
    
    //TODO: fix connection and test finish
  }
  
  @Test
  void testUiLogicSwitchesAndDefaults()
  {
    navigateToSystemDbWizardStep();
    WebTestSystemDb.assertDefaultValues(driver);
    WebTestSystemDb.assertDefaultPortSwitch(driver);
    WebTestSystemDb.assertDatabaseTypeSwitch(driver);
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
