package ch.ivyteam.enginecockpit.fileupload;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.WebTestBase;

public class WebTestLicenceUpload extends WebTestBase
{
  @Test
  public void testLicenceUploadWithNoFile()
  {
    toDashboardAndOpenLicenceUpload();
    
    driver.findElementById("licenceUpload:uploadBtn").click();
    saveScreenshot("no_lic");
    webAssertThat(() -> assertThat(driver.findElementById("uploadError").getText()).isEqualTo("Choose a valid file before upload."));
  }
  
  @Test
  public void testLicenceUploadInvalidFileEnding() throws IOException
  {
    toDashboardAndOpenLicenceUpload();
    
    Path createTempFile = Files.createTempFile("licence", ".txt");
    driver.findElementById("fileInput").sendKeys(createTempFile.toString());
    driver.findElementById("licenceUpload:uploadBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("uploadError").getText()).isNotEmpty());
    saveScreenshot("wrong_file_format");
    webAssertThat(() -> assertThat(driver.findElementById("uploadError").getText()).isEqualTo("Choose a valid file before upload."));
  }
  
  @Test
  public void testLicenceUploadInvalidLicenceAndBack() throws IOException
  {
    toDashboardAndOpenLicenceUpload();
    
    Path createTempFile = Files.createTempFile("licence", ".lic");
    driver.findElementById("fileInput").sendKeys(createTempFile.toString());
    driver.findElementById("licenceUpload:uploadBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("uploadLog").getText()).isNotEmpty());
    webAssertThat(() -> assertThat(driver.findElementById("fileUploadForm").isDisplayed()).isFalse());
    saveScreenshot("invalid_lic");
    webAssertThat(() -> assertThat(driver.findElementById("uploadLog").getText()).isEqualTo("Licence file has a wrong format. It must have at least 6 lines"));
    
    driver.findElementById("licenceUpload:backBtn").click();
    saveScreenshot("back");
    webAssertThat(() -> assertThat(driver.findElementById("fileUploadForm").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("uploadLog").isDisplayed()).isFalse());
  }

  private void toDashboardAndOpenLicenceUpload()
  {
    toDashboard();
    
    findUploadButton();
    saveScreenshot("fileupload");
    webAssertThat(() -> assertThat(driver.findElementById("licenceUpload:fileUploadModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("selectedFileOutput").getText()).contains(".lic"));
    webAssertThat(() -> assertThat(driver.findElementById("uploadError").getText()).isEmpty());
  }
  
  private void toDashboard()
  {
    login();
    saveScreenshot();
  }
  
  private void findUploadButton()
  {
    if (driver.findElementsById("uploadLicenceBtn").size() != 0)
    {
      driver.findElementById("uploadLicenceBtn").click();
    }
    else
    {
      driver.findElementById("tasksButtonLicence").click();
    }
  }
}
