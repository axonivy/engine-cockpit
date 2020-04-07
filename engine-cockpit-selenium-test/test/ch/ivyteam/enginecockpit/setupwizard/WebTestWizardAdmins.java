package ch.ivyteam.enginecockpit.setupwizard;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.resetConfig;
import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.system.WebTestAdmins;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestWizardAdmins
{
  
  @BeforeEach
  void beforeEach()
  {
    WebTestWizard.navigateToStep("Administrators");
  }
  
  @AfterEach
  void afterEach()
  {
    resetConfig();
  }
  
  @Test
  void testAdminStep()
  {
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
    WebTestAdmins.testAddEditDelete();
  }
  
  @Test
  void testAdminDialogInvalid()
  {
    WebTestAdmins.testAddAdminInvalidValues();
    WebTestAdmins.testAddAdminInvalidPassword();
  }
  
  @Test
  void testOwnAdminCannotBeDeleted()
  {    
    WebTestAdmins.assertOwnAdminCannotBeDeleted();
  }
  
}
