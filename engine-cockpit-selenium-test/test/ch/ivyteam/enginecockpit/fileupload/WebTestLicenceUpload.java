package ch.ivyteam.enginecockpit.fileupload;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;

@IvyWebTest
public class WebTestLicenceUpload
{
  
  @BeforeEach
  void beforeEach()
  {
    login();
    $("#uploadLicenceBtn").click();
    $("#licenceUpload\\:fileUploadModal").shouldBe(visible);
    $("#selectedFileOutput").shouldHave(text(".lic"));
    $("#uploadError").shouldBe(empty);
  }
  
  @Test
  public void testLicenceUploadWithNoFile()
  {
    $("#licenceUpload\\:uploadBtn").click();
    $("#uploadError").shouldBe(exactText("Choose a valid file before upload."));
  }
  
  @Test
  public void testLicenceUploadInvalidFileEnding() throws IOException
  {
    Path createTempFile = Files.createTempFile("licence", ".txt");
    $("#fileInput").sendKeys(createTempFile.toString());
    $("#licenceUpload\\:uploadBtn").click();
    $("#uploadError").shouldNotBe(empty);
    $("#uploadError").shouldBe(exactText("Choose a valid file before upload."));
  }
  
  @Test
  public void testLicenceUploadInvalidLicenceAndBack() throws IOException
  {
    Path createTempFile = Files.createTempFile("licence", ".lic");
    $("#fileInput").sendKeys(createTempFile.toString());
    $("#licenceUpload\\:uploadBtn").click();
    $("#uploadLog").shouldNotBe(empty);
    $("#fileUploadForm").shouldNotBe(visible);
    $("#uploadLog").shouldBe(exactText("Licence file has a wrong format. It must have at least 6 lines"));
    
    $("#licenceUpload\\:backBtn").click();
    $("#fileUploadForm").shouldBe(visible);
    $("#uploadLog").shouldNotBe(visible);
  }
  
}
