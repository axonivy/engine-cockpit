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
import com.codeborne.selenide.Condition;
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
    openAddAdminDialog();
    WebTestAdmins.addAdmin("admin", "admin@ivyTeam.ch", "password", "password", "en", "en");
    $(".ui-growl-title").shouldBe(text("'admin' added"));
    Selenide.refresh();
    var table = new Table(By.id("admins:adminForm:adminTable"));
    table.firstColumnShouldBe(exactTexts("admin"));
    $(By.id("addAdminForm:adminWarnMessage")).shouldBe(empty);
    WebTestWizard.activeStepShouldBeOk();
    WebTestWizard.nextStep();
    $(WebTestWizard.ACTIVE_WIZARD_STEP).shouldBe(text("Web Server"));
  }

  @Test
  void addEditDeleteAdmin() {
    openAddAdminDialog();
    WebTestAdmins.testAddEditDelete();
  }

  @Test
  void adminDialogInvalid() {
    openAddAdminDialog();
    WebTestAdmins.testAddAdminInvalidValues();
    openAddAdminDialog();
    WebTestAdmins.testAddAdminInvalidPassword();
  }

  @Test
  void ownAdminCannotBeDeleted() {
    WebTestAdmins.assertOwnAdminCannotBeDeleted();
  }

  private static void openAddAdminDialog() {
    $(By.id("addAdminForm:newAdminBtn")).shouldBe(Condition.enabled, Condition.visible).click();
    $(By.id("admins:editAdminDialog")).shouldBe(Condition.visible);
  }
}
