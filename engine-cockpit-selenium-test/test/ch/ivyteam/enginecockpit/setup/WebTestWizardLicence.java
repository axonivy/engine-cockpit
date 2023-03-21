package ch.ivyteam.enginecockpit.setup;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.resetLicence;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

@IvyWebTest
public class WebTestWizardLicence {

  @BeforeEach
  void beforeEach() {
    WebTestWizard.navigateToStep("Licence");
  }

  @AfterEach
  void afterEach() {
    resetLicence();
  }

  @Test
  void testLicenceStep() {
    $(By.id("licence:licWarnMessage")).shouldHave(text("Please upload a valid licence."));
    WebTestWizard.activeStepShouldHaveWarnings();
    uploadLicence();
    $(".ui-growl-message").shouldHave(text("Success"));
    $(By.id("licence:fileUploadForm:licenceInfoTable")).shouldHave(text("lukas.lieb@axonivy.com"));
    $(By.id("licence:licWarnMessage")).shouldHave(empty);
    WebTestWizard.activeStepShouldBeOk();

    WebTestWizard.nextStep();
    $(WebTestWizard.ACTIVE_WIZARD_STEP).shouldBe(text("Administrators"));
  }

  @Test
  public void testLicenceUploadInvalidFileEnding() throws IOException {
    uploadLicence(Files.createTempFile("licence", ".txt"));
    $(By.id("licence:fileUploadForm:dropZone")).shouldHave(exactText("Choose or drop a file which ends with: .lic"));
  }

  @Test
  public void testLicenceUploadInvalidLicence() throws IOException {
    uploadLicence(Files.createTempFile("licence", ".lic"));
    $(".ui-growl-message").shouldHave(text("Licence file has a wrong format. It must have at least 6 lines"));
  }

  private void uploadLicence() {
    uploadLicence(new File(System.getProperty("user.dir") + "/resources/test.lic").toPath());
  }

  private void uploadLicence(Path lic) {
    $(By.id("licence:fileUploadForm:licenceUpload_input")).sendKeys(lic.toString());
  }

}
