package ch.ivyteam.enginecockpit.renewlicence;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import ch.ivyteam.enginecockpit.WebTestBase;

public class WebTestRenewLicence extends WebTestBase
{
  
  @AfterEach
  void cleanup()
  {
    resetLicence(driver);
  }

  @Test
  public void testRenewRequest()
  {
    toDashboard();
    uploadLicenceToRenew();
    sendRenew("webTest@renewLicence.axonivy.test");
    saveScreenshot("sent_renew1");
    webAssertThat(() -> assertThat(driver.findElementByCssSelector(".ui-growl-message")
            .getText()).contains("This is for testing"));
    saveScreenshot("renew_positive");
    removeGrowl();
    webAssertThat(() -> assertThat(driver.findElementById("renewLicence:renewLicence").isDisplayed()).isFalse());
  }
  
  @Test
  public void testRenewRequestNoOrInvalidMail()
  {
    toDashboard();
    uploadLicenceToRenew();
    sendRenew("");
    saveScreenshot("nomail");
    webAssertThat(() -> assertThat(driver.findElementById("renewLicence:form:emailInputMessage").getText())
            .isEqualTo("Please put your mail"));
    driver.findElementById("renewLicence:form:cancelRenewBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("renewLicence:renewLicence").isDisplayed()).isFalse());
    
    sendRenew("invalid");
    saveScreenshot("invalid");
    webAssertThat(() -> assertThat(driver.findElementByCssSelector(".ui-growl-message")
            .getText()).contains("Your email address is not valid"));
  }

  private void uploadLicenceToRenew()
  {
    if (driver.findElementsById("uploadLicenceBtn").size() != 0)
    {
      driver.findElementById("uploadLicenceBtn").click();
    }
    else
    {
      driver.findElementById("tasksButtonLicence").click();
    }
    saveScreenshot("fileupload");
    webAssertThat(() -> assertThat(driver.findElementById("licenceUpload:fileUploadModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("selectedFileOutput").getText()).contains(".lic"));
    webAssertThat(() -> assertThat(driver.findElementById("uploadError").getText()).isEmpty());
    
    File file = new File(System.getProperty("user.dir")+"/resources/test.lic");
    String path = file.getAbsolutePath();
    driver.findElementById("fileInput").sendKeys(path);
    saveScreenshot("selected_licence");
    driver.findElementById("licenceUpload:uploadBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("uploadLog").getText()).contains("Successfully uploaded licence"));
    saveScreenshot("uploadedLic_log");
    driver.findElementById("licenceUpload:closeDeploymentBtn").click();
    saveScreenshot("uploadedLic_dash");
    webAssertThat(() -> assertThat(driver.findElementById("licenceType").getText()).contains("Standard Edition"));
  }

  private void sendRenew(String mailTo)
  {
    driver.findElementById("tasksButtonLicenceRenew").click();
    webAssertThat(() -> assertThat(driver.findElementById("renewLicence:renewLicence").isDisplayed()).isTrue());
    driver.findElementById("renewLicence:form:emailInput").sendKeys(mailTo);
    saveScreenshot("filled_renew");
    driver.findElementById("renewLicence:form:renewBtn").click();
  }

  private void removeGrowl()
  {
    new Actions(driver).moveToElement(driver.findElementById("mailConfigForm:msgs_container"))
            .moveToElement(driver.findElementByClassName("ui-growl-icon-close")).click()
            .build().perform();
  }

  private void toDashboard()
  {
    login();
    saveScreenshot();
  }
}
