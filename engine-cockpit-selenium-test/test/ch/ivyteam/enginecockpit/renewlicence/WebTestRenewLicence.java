package ch.ivyteam.enginecockpit.renewlicence;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.WebTestBase;

public class WebTestRenewLicence extends WebTestBase
{
  
  @AfterEach
  void cleanup()
  {
    resetLicence();
  }

  @Test
  public void testRenewRequest()
  {
    toDashboard();
    uploadLicenceToRenew();
    sendRenew("webTest@renewLicence.axonivy.test");
    $(".ui-growl-message").shouldHave(text("This is for testing"));
    $("#renewLicence\\:renewLicence").shouldNotBe(visible);
  }
  
  @Test
  public void testRenewRequestNoOrInvalidMail()
  {
    toDashboard();
    uploadLicenceToRenew();
    sendRenew("");
    $("#renewLicence\\:form\\:emailInputMessage").shouldBe(exactText("Please put your mail"));
    $("#renewLicence\\:form\\:cancelRenewBtn").click();
    $("#renewLicence\\:renewLicence").shouldNotBe(visible);
    
    sendRenew("invalid");
    $(".ui-growl-message").shouldHave(text("Your email address is not valid"));
  }

  private void uploadLicenceToRenew()
  {
    $("#uploadLicenceBtn").click();
    $("#licenceUpload\\:fileUploadModal").shouldBe(visible);
    $("#selectedFileOutput").shouldHave(text(".lic"));
    $("#uploadError").shouldBe(empty);
    
    File file = new File(System.getProperty("user.dir")+"/resources/test.lic");
    String path = file.getAbsolutePath();
    $("#fileInput").sendKeys(path);
    $("#licenceUpload\\:uploadBtn").click();
    $("#uploadLog").shouldHave(text("Successfully uploaded licence"));
    $("#licenceUpload\\:closeDeploymentBtn").click();
    $("#licenceType").shouldHave(text("Standard Edition"));
  }

  private void sendRenew(String mailTo)
  {
    $("#tasksButtonLicenceRenew").click();
    $("#renewLicence\\:renewLicence").shouldBe(visible);
    $("#renewLicence\\:form\\:emailInput").sendKeys(mailTo);
    $("#renewLicence\\:form\\:renewBtn").click();
  }

  private void toDashboard()
  {
    login();
  }
}
