package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WebTestDashboard extends WebTestBase
{
  @Test
  void testDashboardContent(FirefoxDriver driver)
  {
    toDashboard(driver);
    checkOverviewBoxes(driver);

    checkInfoPanels(driver);
    
    checkLicenceInfo(driver);
  }

  private void checkOverviewBoxes(FirefoxDriver driver)
  {
    List<WebElement> overviewBoxes = driver.findElementsByClassName("overview-box-content");
    assertThat(overviewBoxes).hasSize(4);
    List<String> boxesExpect = new ArrayList<>(
            Arrays.asList("Sessions", "Users", "Running Cases", "Applications"));
    overviewBoxes.stream().map(b -> b.findElement(new By.ByClassName("overview-box-title")).getText())
            .forEach(t -> assertThat(t).isNotEmpty().isIn(boxesExpect));
    overviewBoxes.stream().map(b -> b.findElement(new By.ByClassName("overview-box-count")).getText())
            .forEach(c -> assertThat(c).isNotEmpty());
  }

  private void checkInfoPanels(FirefoxDriver driver)
  {
    List<WebElement> infoPanels = driver.findElementsByClassName("ui-panel");
    assertThat(infoPanels).hasSize(5);
  }
  
  private void checkLicenceInfo(FirefoxDriver driver)
  {
    driver.findElementById("tasksButtonLicenceDetail").click();
    assertThat(driver.findElementById("licenceDetailDialog_title").isDisplayed()).isTrue();
    WebElement licenceList = driver.findElementById("licenceInfoForm:detailsList");
    assertThat(licenceList.isDisplayed()).isTrue();
  }
  
  @Test
  public void testSendTestMailInvalidInputs(FirefoxDriver driver)
  {
    toDashboardAndOpenSendMailModal(driver);
    
    driver.findElementById("sendTestMailForm:sendToInput").clear();
    driver.findElementById("sendTestMailForm:subjectInput").clear();
    driver.findElementById("sendTestMailForm:sendTestMailBtn").click();
    saveScreenshot(driver, "invalid_inputs");
    webAssertThat(() -> assertThat(driver.findElementById("sendTestMailForm:sendToInputMessage").getText()).isEqualTo("Value is required"));
    webAssertThat(() -> assertThat(driver.findElementById("sendTestMailForm:subjectInputMessage").getText()).isEqualTo("Value is required"));
  }
  
  @Test
  public void testSendTestMailError(FirefoxDriver driver)
  {
    toDashboardAndOpenSendMailModal(driver);
    
    driver.findElementById("sendTestMailForm:sendToInput").sendKeys("test@example.com");
    driver.findElementById("sendTestMailForm:sendTestMailBtn").click();
    saveScreenshot(driver, "error_send");
    webAssertThat(() -> assertThat(driver.findElementById("mailConfigForm:msgs_container").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("mailConfigForm:msgs_container").getText()).contains("Error while sending test mail"));
  }
  
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
  
  @Disabled
  @Test
  public void testLicenceUploadInvalidLicence(FirefoxDriver driver) throws IOException
  {
    toDashboardAndOpenLicenceUpload(driver);
    
    Path createTempFile = Files.createTempFile("licence", ".lic");
    driver.findElementById("fileInput").sendKeys(createTempFile.toString());
    driver.findElementById("licenceUpload:uploadBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("uploadError").getText()).isNotEmpty());
    saveScreenshot(driver, "invalid_lic");
    webAssertThat(() -> assertThat(driver.findElementById("uploadError").getText()).isEqualTo("Licence file has wrong format."));
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

  private void toDashboardAndOpenSendMailModal(FirefoxDriver driver)
  {
    toDashboard(driver);
    driver.findElementById("mailConfigForm:openTestMailBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("sendTestMailModal").isDisplayed()).isTrue());
    saveScreenshot(driver, "send_mail_modal");
  }
  
  private void toDashboard(FirefoxDriver driver)
  {
    login(driver);
    saveScreenshot(driver);
  }
}
