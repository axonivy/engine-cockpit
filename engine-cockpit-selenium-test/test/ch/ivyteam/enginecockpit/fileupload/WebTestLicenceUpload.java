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
  public void testLicenceUploadInvalidLicence(FirefoxDriver driver) throws IOException
  {
    toDashboardAndOpenLicenceUpload(driver);
    
    Path createTempFile = Files.createTempFile("licence", ".lic");
    driver.findElementById("fileInput").sendKeys(createTempFile.toString());
    driver.findElementById("licenceUpload:uploadBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("uploadLog").getText()).isNotEmpty());
    saveScreenshot(driver, "invalid_lic");
    webAssertThat(() -> assertThat(driver.findElementById("uploadLog").getText()).isEqualTo("Licence file has wrong format."));
  }

  private void toDashboardAndOpenLicenceUpload(FirefoxDriver driver)
  {
    toDashboard(driver);
    
    driver.findElementById("uploadLicenceBtn").click();
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
}
