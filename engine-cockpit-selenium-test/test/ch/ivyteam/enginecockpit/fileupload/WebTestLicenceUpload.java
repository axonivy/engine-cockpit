package ch.ivyteam.enginecockpit.fileupload;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.WebTestBase;

public class WebTestLicenceUpload extends WebTestBase
{
  @Test
  public void testLicenceUploadWithNoFile(FirefoxDriver driver)
  {
    toDashboardAndOpenLicenceUpload(driver);
    
    driver.findElementById("licenceUpload:uploadBtn").click();
    saveScreenshot(driver, "no_lic");
    webAssertThat(() -> assertThat(driver.findElementById("uploadError").getText()).isEqualTo("Choose a valid file before upload."));
  }
  
  @Test
  public void testLicenceUploadInvalidFileEnding(FirefoxDriver driver) throws IOException
  {
    toDashboardAndOpenLicenceUpload(driver);
    
    Path createTempFile = Files.createTempFile("licence", ".txt");
    driver.findElementById("fileInput").sendKeys(createTempFile.toString());
    driver.findElementById("licenceUpload:uploadBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("uploadError").getText()).isNotEmpty());
    saveScreenshot(driver, "wrong_file_format");
    webAssertThat(() -> assertThat(driver.findElementById("uploadError").getText()).isEqualTo("Choose a valid file before upload."));
  }
  
  @Test
  public void testLicenceUploadInvalidLicenceAndBack(FirefoxDriver driver) throws IOException
  {
    toDashboardAndOpenLicenceUpload(driver);
    
    Path createTempFile = Files.createTempFile("licence", ".lic");
    driver.findElementById("fileInput").sendKeys(createTempFile.toString());
    driver.findElementById("licenceUpload:uploadBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("uploadLog").getText()).isNotEmpty());
    webAssertThat(() -> assertThat(driver.findElementById("fileUploadForm").isDisplayed()).isFalse());
    saveScreenshot(driver, "invalid_lic");
    webAssertThat(() -> assertThat(driver.findElementById("uploadLog").getText()).isEqualTo("Licence file has a wrong format. It must have at least 6 lines"));
    
    driver.findElementById("licenceUpload:backBtn").click();
    saveScreenshot(driver, "back");
    webAssertThat(() -> assertThat(driver.findElementById("fileUploadForm").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("uploadLog").isDisplayed()).isFalse());
  }

  private void toDashboardAndOpenLicenceUpload(FirefoxDriver driver)
  {
    toDashboard(driver);
    
    findUploadButton(driver);
    saveScreenshot(driver, "fileupload");
    webAssertThat(() -> assertThat(driver.findElementById("licenceUpload:fileUploadModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("selectedFileOutput").getText()).contains(".lic"));
    webAssertThat(() -> assertThat(driver.findElementById("uploadError").getText()).isEmpty());
  }
  
  private void toDashboard(FirefoxDriver driver)
  {
    login(driver);
    saveScreenshot(driver);
  }
  
  private void findUploadButton(FirefoxDriver driver)
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
