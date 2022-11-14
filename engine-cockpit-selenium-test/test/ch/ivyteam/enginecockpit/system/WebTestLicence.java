package ch.ivyteam.enginecockpit.system;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
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
    $("#selectedFileOutput").shouldHave(text(".lic"));
    $("#uploadStatus").shouldBe(empty);
  }

  @Test
  void licenceUploadInvalidFileEnding() throws IOException {
    var createTempFile = Files.createTempFile("licence", ".txt");
    $("#fileInput").sendKeys(createTempFile.toString());
    $("#uploadStatus").shouldBe(empty);
  }

  @Test
  void licenceUploadInvalidLicence() throws IOException {
    var createTempFile = Files.createTempFile("licence", ".lic");
    $("#fileInput").sendKeys(createTempFile.toString());
    $("#uploadLog").shouldBe(exactText("Licence file has a wrong format. It must have at least 6 lines"));
  }

  @Test
  void liveStats() {
    EngineCockpitUtil.assertLiveStats(List.of("Sessions"));
  }
}
