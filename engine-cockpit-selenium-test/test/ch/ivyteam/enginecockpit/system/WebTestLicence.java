package ch.ivyteam.enginecockpit.system;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
class WebTestLicence {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toLicence();
    $(By.id("licence:fileUploadForm:dropZone")).shouldHave(text(".lic"));
  }

  @Test
  void licenceUploadInvalidFileEnding() throws IOException {
    var createTempFile = Files.createTempFile("licence", ".txt");
    $(By.id("licence:fileUploadForm:licenceUpload_input")).sendKeys(createTempFile.toString());
    $(".ui-growl-message").shouldHave(text("Licence files must have file extension .lic"));
  }

  @Test
  void licenceUploadInvalidLicence() throws IOException {
    var createTempFile = Files.createTempFile("licence", ".lic");
    $(By.id("licence:fileUploadForm:licenceUpload_input")).sendKeys(createTempFile.toString());
    $(".ui-growl-message").shouldHave(text("Licence file has a wrong format. It must have at least 6 lines"));
  }

  @Test
  void liveStats() {
    EngineCockpitUtil.assertLiveStats(List.of("Sessions"));
  }
}
