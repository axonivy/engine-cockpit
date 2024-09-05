package ch.ivyteam.enginecockpit.setup;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.resetConfig;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Selenide;

@IvyWebTest
class WebTestWizardStorage {

  @BeforeEach
  void beforeEach() {
    WebTestWizard.navigateToStep("Storage");
  }

  @AfterEach
  void afterEach() {
    resetConfig();
  }

  @Test
  void warningMessage() {
    $(By.id("form:appDir")).shouldBe(visible, exactValue("applications"));
    $(By.id("storageWarnMessage")).shouldNotBe(visible);

    $(By.id("form:appDir")).clear();
    $(By.id("form:save")).shouldBe(visible).click();
    $(By.id("storageWarnMessage")).shouldBe(visible);

    $(By.id("form:reset")).shouldBe(visible).click();
    $(By.id("storageWarnMessage")).shouldNotBe(visible);
    $(By.id("form:appDir")).shouldBe(exactValue("applications"));
  }

  @Test
  void saveAndReset() {
    $(By.id("form:appDir")).shouldBe(visible, exactValue("applications"));
    $(By.id("form:dataDir")).shouldBe(visible, exactValue("data"));

    $(By.id("form:appDir")).sendKeys("_app");
    $(By.id("form:dataDir")).sendKeys("_data");
    $(By.id("form:save")).shouldBe(visible).click();
    $(By.className("ui-growl-message")).shouldBe(visible, exactText("Directory changes saved successfully"));

    Selenide.refresh();
    $(By.id("form:appDir")).shouldBe(visible, exactValue("applications_app"));
    $(By.id("form:dataDir")).shouldBe(visible, exactValue("data_data"));

    $(By.id("form:reset")).shouldBe(visible).click();
    $(By.className("ui-growl-message")).shouldBe(visible, exactText("Directory changes saved successfully"));
    $(By.id("form:appDir")).shouldBe(visible, exactValue("applications"));
    $(By.id("form:dataDir")).shouldBe(visible, exactValue("data"));
  }
}
