package ch.ivyteam.enginecockpit.renewlicence;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.resetLicence;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;

@IvyWebTest
public class WebTestRenewLicence
{
  
  @BeforeEach
  void beforeEach()
  {
    login();
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
  
  @AfterEach
  void afterEach()
  {
    resetLicence();
  }

  @Test
  public void testRenewRequest()
  {
    sendRenew("webTest@renewLicence.axonivy.test");
    $(".ui-growl-message").shouldHave(text("This is for testing"));
    $("#renewLicence\\:renewLicence").shouldNotBe(visible);
  }
  
  @Test
  public void testRenewRequestNoOrInvalidMail()
  {
    sendRenew("");
    $("#renewLicence\\:form\\:emailInputMessage").shouldBe(exactText("Please put your mail"));
    $("#renewLicence\\:form\\:cancelRenewBtn").click();
    $("#renewLicence\\:renewLicence").shouldNotBe(visible);
    
    sendRenew("invalid");
    $(".ui-growl-message").shouldHave(text("Your email address is not valid"));
  }

  private void sendRenew(String mailTo)
  {
    $("#tasksButtonLicenceRenew").click();
    $("#renewLicence\\:renewLicence").shouldBe(visible);
    $("#renewLicence\\:form\\:emailInput").sendKeys(mailTo);
    $("#renewLicence\\:form\\:renewBtn").click();
  }

  
}
