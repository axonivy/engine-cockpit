package ch.ivyteam.enginecockpit.system;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
class WebTestLicence {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toLicence();
    $("#licence\\:fileUploadForm\\:dropZone").shouldHave(text(".lic"));
  }

  @Test
  void licenceUploadInvalidFileEnding() throws IOException {
    var createTempFile = Files.createTempFile("licence", ".txt");
    $("#licence\\:fileUploadForm\\:chooseFileBtn_input").sendKeys(createTempFile.toString());
    $(".ui-growl-message").shouldHave(text("Licence files must have file extension .lic"));
  }

  @Test
  void licenceUploadInvalidLicence() throws IOException {
    var createTempFile = Files.createTempFile("licence", ".lic");
    $("#licence\\:fileUploadForm\\:chooseFileBtn_input").sendKeys(createTempFile.toString());
    $(".ui-growl-message").shouldHave(text("Licence file has a wrong format. It must have at least 6 lines"));
  }

  @Test
  void liveStats() {
    EngineCockpitUtil.assertLiveStats(List.of("Sessions"));
  }
}
