package ch.ivyteam.enginecockpit.renewlicence;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;

import ch.ivyteam.enginecockpit.WebTestBase;

public class WebTestRenewLicence extends WebTestBase
{

  private void waitForElasticsearch() throws InterruptedException
  {
    Thread.sleep(1000);
  }

  @Test
  public void testRenewRequest(FirefoxDriver driver) throws InterruptedException
  {
    toDashboardAndOpenLicenceUpload(driver);
    uploadLicenceToRenew(driver);
    sendRenew(driver, "webTest@renewLicence.axonivy.test");
    waitForElasticsearch();
    saveScreenshot(driver, "sent_renew1");
    webAssertThat(() -> assertThat(driver.findElementByCssSelector(".ui-growl-message")
            .getText()).contains("This is for testing"));
    saveScreenshot(driver, "renew_positive");
    removeGrowl(driver);
  }
  
  @Test
  public void testRenewRequestNoMail(FirefoxDriver driver) throws InterruptedException
  {
    toDashboardAndOpenLicenceUpload(driver);
    uploadLicenceToRenew(driver);
    sendRenew(driver, "");
    waitForElasticsearch();
    saveScreenshot(driver, "sent_renew1");
    webAssertThat(() -> assertThat(driver.findElementByCssSelector(".ui-growl-message")
            .getText()).contains("Please put your mail"));
    saveScreenshot(driver, "renew_noMail");
    removeGrowl(driver);
  }

  private void uploadLicenceToRenew(FirefoxDriver driver) throws InterruptedException
  {
    File file = new File(System.getProperty("user.dir")+"/test/ch/ivyteam/enginecockpit/renewlicence/test.lic");
    String path = file.getAbsolutePath();
    driver.findElementById("fileInput").sendKeys(path);
    saveScreenshot(driver, "selected_licence");
    driver.findElementById("licenceUpload:uploadBtn").click();
    waitForElasticsearch();
    waitForElasticsearch();
    saveScreenshot(driver, "uploadedLic_log");
    webAssertThat(() -> assertThat(driver.findElementById("uploadLog").getText()).contains("Successful uploaded licence"));
    driver.findElementById("licenceUpload:closeDeploymentBtn").click();
    waitForElasticsearch();
    saveScreenshot(driver, "uploadedLic_dash");
    driver.navigate().refresh();
    webAssertThat(() -> assertThat(driver.findElementById("licenceType").getText()).contains("Standard Edition"));
  }

  private void sendRenew(FirefoxDriver driver, String mailTo)
  {
    driver.findElementById("tasksButtonLicenceRenew").click();
    webAssertThat(() -> assertThat(driver.findElementById("renewLicence:renewLicence").isDisplayed()).isTrue());
    driver.findElementById("renewLicence:form:emailInput").sendKeys(mailTo);
    saveScreenshot(driver, "filled_renew");
    driver.findElementById("renewLicence:form:renewBtn").click();
  }

  private void removeGrowl(FirefoxDriver driver)
  {
    new Actions(driver).moveToElement(driver.findElementById("mailConfigForm:msgs_container"))
            .moveToElement(driver.findElementByClassName("ui-growl-icon-close")).click()
            .build().perform();
  }

  private void toDashboardAndOpenLicenceUpload(FirefoxDriver driver)
  {
    toDashboard(driver);
    if (driver.findElementsById("uploadLicenceBtn").size() != 0)
    {
      driver.findElementById("uploadLicenceBtn").click();
    }
    else
    {
      driver.findElementById("tasksButtonLicence").click();
    }
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
