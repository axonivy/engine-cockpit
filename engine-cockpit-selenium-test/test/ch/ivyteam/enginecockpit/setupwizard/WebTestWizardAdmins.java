package ch.ivyteam.enginecockpit.setupwizard;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.Condition.empty;
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
  }
  
  @Test
  void testAdminStep()
  {
    addSystemAdmin();
    navigateToAdminsWizardStep();
    Table table = new Table(By.id("admins:adminForm:adminTable"));
    WebTestAdmins.addAdmin("admin", "admin@ivyTeam.ch", "password", "password");
    $(".ui-growl-title").shouldBe(text("'admin' added successfully"));
    Selenide.refresh();
    table.firstColumnShouldBe(exactTexts("admin"));
    $("#addAdminForm\\:adminWarnMessage").shouldBe(empty);
    WebTestWizard.activeStepShouldBeOk();
    WebTestWizard.nextStep();
    $(WebTestWizard.ACTIVE_WIZARD_STEP).shouldBe(text("Web Server"));
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
    WebTestWizard.navigateToStep("Administrators");
  }

}
