package ch.ivyteam.enginecockpit.setupwizard;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.WebTestBase;

public class WebTestWizardLicence extends WebTestBase
{
  
  @AfterEach
  void cleanup()
  {
    resetLicence();
    driver.quit();
  }
  
  @Test
  void testLicenceStep()
  {
    navigateToLicWizardStep();
    
    $("#fileUploadForm\\:licDetailLink").shouldBe(text("Demo licence"));
    $("#licNextStepModel").shouldNotBe(visible);
    $("#fileUploadForm\\:licNextStep").shouldNotBe(exist);
    $("#fileUploadForm\\:licNextStepDemo").click();
    $("#licNextStepModel").shouldBe(visible);
    $("#licNextStepForm\\:licNextStepDemoNo").click();
    $("#licNextStepModel").shouldNotBe(visible);
    
    uploadLicence();
    $("#fileUploadForm\\:licDetailLink").shouldBe(text("Jacek Lajdecki"));
    $("#uploadStatus").shouldBe(exactText("Success"));
    
    $("#fileUploadForm\\:licDetailLink").click();
    $("#licenceDetailDialog").shouldBe(visible);
    $("#licenceDetailDialog .ui-dialog-titlebar-close").click();
    $("#licenceDetailDialog").shouldNotBe(visible);
    
    $("#fileUploadForm\\:licNextStepDemo").shouldNotBe(exist);
    $("#fileUploadForm\\:licNextStep").click();
    $("#wizardSteps li.ui-state-highlight").shouldBe(text("Administrators"));
  }
  
  @Test
  public void testLicenceUploadInvalidFileEnding() throws IOException
  {
    navigateToLicWizardStep();
    
    uploadLicence(Files.createTempFile("licence", ".txt"));
    $("#uploadStatus").shouldBe(empty);
    $("#selectedFileOutput").shouldBe(exactText("Choose or drop a file which ends with: .lic"));
  }
  
  @Test
  public void testLicenceUploadInvalidLicence() throws IOException
  {
    navigateToLicWizardStep();
    
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
  
  private void navigateToLicWizardStep()
  {
    login("setup.xhtml");
    $("#wizardSteps li.ui-state-highlight").shouldBe(text("Licence"));
  }
  
  public static void skipLicStep()
  {
    $("#wizardSteps li.ui-state-highlight").shouldBe(text("Licence"));
    $("#fileUploadForm\\:licNextStepDemo").click();
    $("#licNextStepModel").shouldBe(visible);
    $("#licNextStepForm\\:licNextStepDemoYes").click();
  }
}
