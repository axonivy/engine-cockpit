package ch.ivyteam.enginecockpit.setupwizard;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.system.WebTestAdmins;
import ch.ivyteam.enginecockpit.util.Table;

public class WebTestWizardAdmins extends WebTestBase
{
  
  @AfterEach
  void cleanup()
  {
    resetConfig();
    driver.quit();
  }
  
  @Test
  void testAdminStep()
  {
    navigateToAdminsWizardStep();
    $("#adminNextStep").shouldBe(enabled);
    Table table = new Table(By.id("admins:adminForm:adminTable"));
    WebTestAdmins.addAdmin("admin", "admin@ivyTeam.ch", "password", "password");
    $(".ui-growl-title").shouldBe(text("'admin' added successfully"));
    Selenide.refresh();
    table.firstColumnShouldBe(exactTexts("admin"));
    $("#adminNextStep").shouldBe(enabled);
    
    $("#adminNextStep").click();
    $("#wizardSteps li.ui-state-highlight").shouldBe(text("Web Server"));
  }
  
  @Test
  void testAddEditDeleteAdmin()
  {
    navigateToAdminsWizardStep();
    WebTestAdmins.testAddEditDelete();
  }
  
  @Test
  void testAdminDialogInvalid()
  {
    navigateToAdminsWizardStep();
    WebTestAdmins.testAddAdminInvalidValues();
    WebTestAdmins.testAddAdminInvalidPassword();
  }
  
  @Test
  void testOwnAdminCannotBeDeleted()
  {    
    navigateToAdminsWizardStep();
    WebTestAdmins.assertOwnAdminCannotBeDeleted();
  }
  
  private void navigateToAdminsWizardStep()
  {
    login("setup.xhtml");
    WebTestWizardLicence.skipLicStep();
    $("#wizardSteps li.ui-state-highlight").shouldBe(text("Administrators"));
  }
  
  public static void skipAdminStep()
  {
    $("#wizardSteps li.ui-state-highlight").shouldBe(text("Administrators"));
    $("#adminNextStep").shouldBe(enabled);
    $("#adminNextStep").click();
  }

}
