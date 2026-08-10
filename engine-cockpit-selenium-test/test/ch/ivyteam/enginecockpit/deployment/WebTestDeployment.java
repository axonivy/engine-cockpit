package ch.ivyteam.enginecockpit.deployment;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.axonivy.ivy.webtest.engine.EngineUrl.isDesigner;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.engine.EngineUrl;
import com.axonivy.ivy.webtest.primeui.PrimeUi;

import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
class WebTestDeployment {

  private static final String APP = isDesigner() ? EngineUrl.applicationName() : "demo-portal";

  @BeforeEach
  void beforeEach() {
    login();
  }

  @Test
  void noFile() {
    toApplicationAndOpenDeployment();
    $(By.id("deployment:fileUploadForm:uploadBtn")).shouldBe(disabled);
  }

  @Test
  void invalidFileEnding(@TempDir Path tempDir) throws IOException {
    toApplicationAndOpenDeployment();
    var tempFile = tempDir.resolve("app.txt");
    Files.createFile(tempFile);
    $(By.id("deployment:fileUploadForm:fileUpload_input")).sendKeys(tempFile.toString());
    $(By.id("deployment:fileUploadForm:uploadBtn")).shouldBe(disabled);
  }

  @Test
  void corruptZip(@TempDir Path tempDir) throws IOException {
    toApplicationAndOpenDeployment();
    var tempFile = tempDir.resolve("app.iar");
    Files.createFile(tempFile);
    deployPath(tempFile, "deployment");
    $(By.id("deployment:uploadStatus")).shouldHave(text("Error"));
    $(By.id("uploadLog")).shouldHave(text("Couldn't deploy 'app.iar'"));
  }

  @Test
  void validApp() {
    toApplicationAndOpenDeployment();
    deployAndAssert("Using options>DeploymentOptions");
  }

  @Test
  void validAppWithDeployOptions() {
    toApplicationAndOpenDeployment();
    showDeploymentOptions();
    deployAndAssert("Using options>DeploymentOptions");
    removeDeployedApp();
  }

  private void deployAndAssert(String expectedDeployOptionsText) {
    deployPath(findTestProject(), "deployment");
    $(By.id("deployment:uploadStatus")).shouldHave(text("Success"));
    $(By.id("uploadLog")).shouldHave(text(expectedDeployOptionsText), text("successfully deployed to application"));
    $(By.id("deployment:closeDeploymentBtn")).shouldBe(visible).click();
  }

  private Path findTestProject() {
    var targetDir = Path.of(System.getProperty("basedir")).getParent().resolve("engine-cockpit-test-data").resolve("target");
    try (var walker = Files.walk(targetDir, 1)) {
      return walker.filter(Files::isRegularFile)
          .filter(f -> {
            var fileName = f.getFileName().toString();
            return fileName.startsWith("engine-cockpit-test-data-") && fileName.endsWith(".iar");
          })
          .findFirst().orElseThrow();
    } catch (IOException | NoSuchElementException ex) {
      throw new RuntimeException("Couldn't find the engine-cockpit-test-data.iar project", ex);
    }
  }

  private void deployPath(Path testDataIar, String idPath) {
    $(By.id(idPath + ":fileUploadForm:fileUpload_input")).sendKeys(testDataIar.toString());
    $(By.id(idPath + ":fileUploadForm:uploadBtn")).shouldNotBe(disabled).click();
    $(By.id("uploadLog")).shouldNotBe(empty);
    $(By.id(idPath + ":fileUploadForm")).shouldNotBe(visible);
  }

  private void removeDeployedApp() {
    $(By.id("versionProjectsForm:projectsTable:0:deleteBtn")).shouldBe(visible).click();
    $(By.id("versionProjectsForm:deleteProjectDialog")).shouldBe(visible);
    $(By.id("versionProjectsForm:deleteProjectDialogConfirmYesBtn")).shouldBe(visible).click();
  }

  @Test
  void deployOptions() {
    toApplicationAndOpenDeployment();
    showDeploymentOptions();
    PrimeUi.selectOne(By.id("deployment:fileUploadForm:deployTestUsers")).selectedItemShould(exactText("AUTO"));
  }

  private void showDeploymentOptions() {
    if (!$(By.id("deployment:fileUploadForm:deployOptionsPanel")).is(visible)) {
      $(By.id("deployment:fileUploadForm:showDeployOptionsBtn")).click();
      $(By.id("deployment:fileUploadForm:deployOptionsPanel")).shouldBe(visible);
    }
  }

  private void toApplicationAndOpenDeployment() {
    Navigation.toApplicationVersion(APP , "1");
    $(By.id("versionProjectsForm:deployButton")).shouldBe(visible).click();
    $(By.id("deployment:fileUploadModal")).shouldBe(visible);
    $(By.id("deployment:fileUploadModal:uploadError")).shouldNotBe(visible);
    $(By.id("deployment:fileUploadModal_title")).shouldHave(text(APP));
  }
}
