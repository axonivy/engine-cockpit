package ch.ivyteam.enginecockpit.setup;

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
public class WebTestMaintenance
{
  
  @BeforeEach
  void beforeEach()
  {
    login("maintenance.xhtml");
  }

  @Test
  void navigateToSetup_menu()
  {
    $(By.id("menuform:setup")).shouldBe(visible).click();
    assertSetupPageIsVisible();
  }

  @Test
  void navigateToSetup_card()
  {
    $(By.id("setup")).shouldBe(visible).click();
    assertSetupPageIsVisible();
  }
  
  @Test
  void navigateToMigration_menu()
  {
    $(By.id("menuform:migrate")).shouldBe(visible).click();
    assertMigratePageIsVisible();
  }
  
  @Test
  void navigateToMigration_card()
  {
    $(By.id("migrate")).shouldBe(visible).click();
    assertMigratePageIsVisible();
  }
  
  @Test
  void navigateToCockpit_menu()
  {
    $(By.id("menuform:cockpit")).shouldBe(visible).click();
    EngineCockpitUtil.assertCurrentUrlEndsWith("dashboard.xhtml");
    $(By.id("menuform:sr_dashboard")).shouldBe(visible);
  }
  
  private void assertSetupPageIsVisible()
  {
    EngineCockpitUtil.assertCurrentUrlEndsWith("setup.xhtml");
    $(By.id("stepForm:wizardSteps")).shouldBe(visible);
  }
  
  private void assertMigratePageIsVisible()
  {
    EngineCockpitUtil.assertCurrentUrlEndsWith("migrate.xhtml");
    $("h1").shouldHave(text("Migrate old Axon Ivy Engine"));
  }
}
