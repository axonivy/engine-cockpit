package ch.ivyteam.enginecockpit.renewlicence;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
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
            .getText()).contains("You shouldn't see this"));
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
  }

  private void sendRenew(FirefoxDriver driver, String mailTo)
  {
    driver.findElementByCssSelector(".ui-icon-refresh").click();
    driver.findElementById("renewLicence:form:emailInput").sendKeys(mailTo);
    saveScreenshot(driver, "filled_renew");
    driver.findElementById("renewLicence:form:renewBtn").click();
  }

  private void removeGrowl(FirefoxDriver driver)
  {
    Actions action = new Actions(driver);
    WebElement we = driver.findElementById("mailConfigForm:msgs_container");
    action.moveToElement(we).moveToElement(driver.findElementByClassName("ui-growl-icon-close")).click()
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
