package ch.ivyteam.enginecockpit.setupwizard;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.security.WebTestAdmins;
import ch.ivyteam.enginecockpit.util.Table;

public class WebTestWizardAdmins extends WebTestBase
{
  
  @AfterEach
  void cleanup()
  {
    resetConfig(driver);
  }
  
  @Test
  void testAdminStep()
  {
    navigateToAdminsWizardStep();
    
    webAssertThat(() -> assertThat(driver.findElementById("adminNextStep").isEnabled()).isFalse());
    Table table = new Table(driver, By.id("admins:adminForm:adminTable"));
    webAssertThat(() -> assertThat(table.getFirstColumnEntriesForSpanClass("admin_name")).isEmpty());
    
    WebTestAdmins.addAdmin(driver, "admin", "admin@ivyTeam.ch", "password", "password");
    saveScreenshot("add_admin");
    webAssertThat(() -> assertThat(driver.findElementByClassName("ui-growl-title").getText())
            .contains("'admin' added successfully"));
    webAssertThat(() -> assertThat(table.getFirstColumnEntriesForSpanClass("admin_name")).containsOnly("admin"));
    webAssertThat(() -> assertThat(driver.findElementById("adminNextStep").isEnabled()).isTrue());
    
    driver.findElementById("adminNextStep").click();
    webAssertThat(() -> assertThat(driver.findElementByCssSelector("#wizardSteps li.ui-state-highlight").getText())
            .contains("Web Server"));
  }
  
  @Test
  void testAddEditDeleteAdmin()
  {
    navigateToAdminsWizardStep();
    WebTestAdmins.testAddEditDeleteAdmin(driver);
  }
  
  @Test
  void testAdminDialogInvalid()
  {
    navigateToAdminsWizardStep();
    WebTestAdmins.testAddAdminInvalidValues(driver);
    WebTestAdmins.testAddAdminInvalidPassword(driver);
  }
  
  private void navigateToAdminsWizardStep()
  {
    login("setup.xhtml");
    WebTestWizardLicence.skipLicStep(driver);
    saveScreenshot("admins");
    webAssertThat(() -> assertThat(driver.findElementByCssSelector("#wizardSteps li.ui-state-highlight").getText())
            .contains("Administrators"));
  }
  
  public static void skipAdminStep(RemoteWebDriver driver)
  {
    webAssertThat(() -> assertThat(driver.findElementByCssSelector("#wizardSteps li.ui-state-highlight").getText())
            .contains("Administrators"));
    WebTestAdmins.addAdmin(driver, "test", "test@test.ch", "password", "password");
    webAssertThat(() -> assertThat(driver.findElementById("adminNextStep").isEnabled()).isTrue());
    driver.findElementById("adminNextStep").click();
  }

}
