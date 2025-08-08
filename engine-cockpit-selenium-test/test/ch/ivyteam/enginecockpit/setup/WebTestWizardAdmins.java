package ch.ivyteam.enginecockpit.setup;

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
class WebTestWizardAdmins {

  @BeforeEach
  void beforeEach() {
    WebTestWizard.navigateToStep("Administrators");
  }

  @AfterEach
  void afterEach() {
    resetConfig();
  }

  @Test
  void adminStep() {
    var table = new Table(By.id("admins:adminForm:adminTable"));
    WebTestAdmins.addAdmin("admin", "admin@ivyTeam.ch", "password", "password");
    $(".ui-growl-title").shouldBe(text("'admin' added"));
    Selenide.refresh();
    table.firstColumnShouldBe(exactTexts("admin"));
    $(By.id("addAdminForm:adminWarnMessage")).shouldBe(empty);
    WebTestWizard.activeStepShouldBeOk();
    WebTestWizard.nextStep();
    $(WebTestWizard.ACTIVE_WIZARD_STEP).shouldBe(text("Web Server"));
  }

  @Test
  void addEditDeleteAdmin() {
    WebTestAdmins.testAddEditDelete();
  }

  @Test
  void adminDialogInvalid() {
    WebTestAdmins.testAddAdminInvalidValues();
    WebTestAdmins.testAddAdminInvalidPassword();
  }

  @Test
  void ownAdminCannotBeDeleted() {
    WebTestAdmins.assertOwnAdminCannotBeDeleted();
  }
}
