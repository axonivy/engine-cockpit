package ch.ivyteam.enginecockpit.info;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;

@IvyWebTest
class WebTestInfo {

  @BeforeEach
  void beforeEach() {
    login("info.xhtml");
  }

  @Test
  void navigateToSetup_menu() {
    $(By.id("menuform:sr_setup")).shouldBe(visible).click();
    $(By.id("menuform:sr_setup_install")).shouldBe(visible).click();
    assertSetupPageIsVisible();
  }

  @Test
  void navigateToMigration_menu() {
    $(By.id("menuform:sr_setup")).shouldBe(visible).click();
    $(By.id("menuform:sr_setup_migrate")).shouldBe(visible).click();
    assertMigratePageIsVisible();
  }

  @Test
  void navigateToCockpit_menu() {
    $(By.id("menuform:sr_cockpit")).shouldBe(visible).click();
    EngineCockpitUtil.assertCurrentUrlContains("dashboard.xhtml");
    $(By.id("menuform:sr_dashboard")).shouldBe(visible);
  }

  private void assertSetupPageIsVisible() {
    EngineCockpitUtil.assertCurrentUrlContains("setup.xhtml");
    $(By.id("stepForm:wizardSteps")).shouldBe(visible);
  }

  private void assertMigratePageIsVisible() {
    EngineCockpitUtil.assertCurrentUrlContains("migrate.xhtml");
    $("h2").shouldHave(text("Migrate old Axon Ivy Engine"));
  }
}
