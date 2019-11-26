package ch.ivyteam.enginecockpit.setupwizard;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;

import ch.ivyteam.enginecockpit.WebTestBase;

public class WebTestWizardLicence extends WebTestBase
{
  
  @AfterEach
  void cleanup()
  {
    resetLicence(driver);
  }
  
  @Test
  void testLicenceStep()
  {
    navigateToLicWizardStep();
    
    webAssertThat(() -> assertThat(driver.findElementById("fileUploadForm:licDetailLink").getText())
            .contains("Demo licence"));
    webAssertThat(() -> assertThat(driver.findElementById("licNextStepModel").isDisplayed()).isFalse());
    webAssertThat(() -> assertThat(elementNotAvailable(driver, By.id("fileUploadForm:licNextStep"))).isTrue());
    driver.findElementById("fileUploadForm:licNextStepDemo").click();
    saveScreenshot("demo_model");
    webAssertThat(() -> assertThat(driver.findElementById("licNextStepModel").isDisplayed()).isTrue());
    driver.findElementById("licNextStepForm:licNextStepDemoNo").click();
    webAssertThat(() -> assertThat(driver.findElementById("licNextStepModel").isDisplayed()).isFalse());
    
    uploadLicence();
    webAssertThat(() -> assertThat(driver.findElementById("fileUploadForm:licDetailLink").getText())
            .contains("Jacek Lajdecki"));
    saveScreenshot("upload_lic");
    webAssertThat(() -> assertThat(driver.findElementById("uploadStatus").getText()).isEqualTo("Success"));
    
    driver.findElementById("fileUploadForm:licDetailLink").click();
    saveScreenshot("lic_detail");
    webAssertThat(() -> assertThat(driver.findElementById("licenceDetailDialog").isDisplayed()).isTrue());
    driver.findElementByCssSelector("#licenceDetailDialog .ui-dialog-titlebar-close").click();
    webAssertThat(() -> assertThat(driver.findElementById("licenceDetailDialog").isDisplayed()).isFalse());
    
    webAssertThat(() -> assertThat(elementNotAvailable(driver, By.id("fileUploadForm:licNextStepDemo"))).isTrue());
    driver.findElementById("fileUploadForm:licNextStep").click();
    saveScreenshot("next_step");
    webAssertThat(() -> assertThat(driver.findElementByCssSelector("#wizardSteps li.ui-state-highlight").getText())
            .contains("Administrators"));
  }
  
  @Test
  public void testLicenceUploadInvalidFileEnding() throws IOException
  {
    navigateToLicWizardStep();
    
    uploadLicence(Files.createTempFile("licence", ".txt"));
    webAssertThat(() -> assertThat(driver.findElementById("uploadStatus").getText()).isEmpty());
    webAssertThat(() -> assertThat(driver.findElementById("selectedFileOutput").getText())
            .isEqualTo("Choose or drop a file which ends with: .lic"));
  }
  
  @Test
  public void testLicenceUploadInvalidLicence() throws IOException
  {
    navigateToLicWizardStep();
    
    uploadLicence(Files.createTempFile("licence", ".lic"));
    webAssertThat(() -> assertThat(driver.findElementById("uploadStatus").getText()).isEqualTo("Error"));
    saveScreenshot("invalid_lic");
    webAssertThat(() -> assertThat(driver.findElementById("uploadLog").getText())
            .isEqualTo("Licence file has a wrong format. It must have at least 6 lines"));
  }
  
  private void uploadLicence()
  {
    uploadLicence(new File(System.getProperty("user.dir")+"/resources/test.lic").toPath());
  }
  
  private void uploadLicence(Path lic)
  {
    driver.findElementById("fileInput").sendKeys(lic.toString());
  }
  
  private void navigateToLicWizardStep()
  {
    login("setup.xhtml");
    saveScreenshot("lic");
    webAssertThat(() -> assertThat(driver.findElementByCssSelector("#wizardSteps li.ui-state-highlight").getText())
            .contains("Licence"));
  }
  
  public static void skipLicStep(RemoteWebDriver driver)
  {
    webAssertThat(() -> assertThat(driver.findElementByCssSelector("#wizardSteps li.ui-state-highlight").getText())
            .contains("Licence"));
    driver.findElementById("fileUploadForm:licNextStepDemo").click();
    webAssertThat(() -> assertThat(driver.findElementById("licNextStepModel").isDisplayed()).isTrue());
    driver.findElementById("licNextStepForm:licNextStepDemoYes").click();
  }
}
