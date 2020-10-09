package ch.ivyteam.enginecockpit.setupwizard;

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

import com.axonivy.ivy.webtest.IvyWebTest;

@IvyWebTest
public class WebTestWizardLicence
{
  
  @BeforeEach
  void beforeEach()
  {
    WebTestWizard.navigateToStep("Licence");
  }
  
  @AfterEach
  void afterEach()
  {
    resetLicence();
  }
  
  @Test
  void testLicenceStep()
  {
    $("#licence\\:licWarnMessage").shouldHave(text("Please upload a valid licence."));
    WebTestWizard.activeStepShouldHaveWarnings();
    uploadLicence();
    $("#uploadStatus").shouldBe(exactText("Success"));
    $("#licence\\:fileUploadForm\\:licenceInfoTable").shouldHave(text("Lukas Lieb"));
    $("#licence\\:licWarnMessage").shouldHave(empty);
    WebTestWizard.activeStepShouldBeOk();
    
    WebTestWizard.nextStep();
    $(WebTestWizard.ACTIVE_WIZARD_STEP).shouldBe(text("Administrators"));
  }
  
  @Test
  public void testLicenceUploadInvalidFileEnding() throws IOException
  {
    uploadLicence(Files.createTempFile("licence", ".txt"));
    $("#uploadStatus").shouldBe(empty);
    $("#selectedFileOutput").shouldBe(exactText("Choose or drop a file which ends with: .lic"));
  }
  
  @Test
  public void testLicenceUploadInvalidLicence() throws IOException
  {
    uploadLicence(Files.createTempFile("licence", ".lic"));
    $("#uploadStatus").shouldBe(exactText("Error"));
    $("#uploadLog").shouldBe(exactText("Licence file has a wrong format. It must have at least 6 lines"));
  }
  
  private void uploadLicence()
  {
    uploadLicence(new File(System.getProperty("user.dir")+"/resources/test.lic").toPath());
  }
  
  private void uploadLicence(Path lic)
  {
    $("#fileInput").sendKeys(lic.toString());
  }
  
}
