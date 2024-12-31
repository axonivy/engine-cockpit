package ch.ivyteam.enginecockpit.fileupload;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.axonivy.ivy.webtest.engine.EngineUrl.DESIGNER;
import static com.axonivy.ivy.webtest.engine.EngineUrl.isDesigner;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;

import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
class WebTestDeployment {

  private static final String APP = isDesigner() ? DESIGNER : "test-ad";

  @BeforeEach
  void beforeEach() {
    login();
  }

  @Test
  void noFile() {
    toAppDetailAndOpenDeployment();
    $("#deploymentModal\\:uploadBtn").click();
    $("#deploymentModal\\:uploadError").shouldBe(text("Choose a valid file before upload"));
  }

  @Test
  void invalidFileEnding(@TempDir Path tempDir) throws IOException {
    toAppDetailAndOpenDeployment();
    var tempFile = tempDir.resolve("app.txt");
    Files.createFile(tempFile);
    $("#fileInput").sendKeys(tempFile.toString());
    $("#deploymentModal\\:uploadBtn").click();
    $("#deploymentModal\\:uploadError").shouldBe(text("Choose a valid file before upload"));
  }

  @Test
  void emptyApp(@TempDir Path tempDir) throws IOException {
    toAppDetailAndOpenDeployment();
    var tempFile = tempDir.resolve("app.iar");
    Files.createFile(tempFile);
    deployPath(tempFile);
    $("#uploadStatus").shouldHave(text("Success"));
    $("#uploadLog").shouldHave(text("No projects to deploy"), text("successful deployed to application"));
  }

  @Test
  void validApp() {
    if (isDesigner()) {
      return;
    }
    toAppDetailAndOpenDeployment();
    deployAndAssert("Using default>DeploymentOptions");
  }

  @Test
  void validAppWithDeployOptions() {
    if (isDesigner()) {
      return;
    }
    toAppDetailAndOpenDeployment();
    showDeploymentOptions();
    deployAndAssert("Using resource.params>DeploymentOptions");
  }

  private void deployAndAssert(String expectedDeployOptionsText) {
    deployPath(findTestProject());
    $("#uploadStatus").shouldHave(text("Success"));
    $("#uploadLog").shouldHave(text(expectedDeployOptionsText), text("successful deployed to application"));
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

  private void deployPath(Path testDataIar) {
    $("#fileInput").sendKeys(testDataIar.toString());
    $("#deploymentModal\\:uploadBtn").click();
    $("#uploadLog").shouldNotBe(empty);
    $("#fileUploadForm").shouldNotBe(visible);
  }

  @Test
  void deployOptions() {
    toAppDetailAndOpenDeployment();
    showDeploymentOptions();
    PrimeUi.selectOne(By.id("deploymentModal:deployTestUsers")).selectedItemShould(exactText("AUTO"));
    PrimeUi.selectOne(By.id("deploymentModal:version")).selectedItemShould(exactText("AUTO"));
    PrimeUi.selectOne(By.id("deploymentModal:state")).selectedItemShould(exactText("ACTIVE_AND_RELEASED"));
    PrimeUi.selectOne(By.id("deploymentModal:fileFormat")).selectedItemShould(exactText("AUTO"));
  }

  @Test
  void deployOptionsVersionRange() {
    toAppDetailAndOpenDeployment();
    openDeployOptionsAndAssertVersionRange();
  }

  @Test
  void deployOptionsVersionRange_AppsView() {
    toAppsAndOpenDeployDialog();
    openDeployOptionsAndAssertVersionRange();
  }

  @Test
  void keepExpandedState() {
    if (isDesigner()) {
      return;
    }
    Navigation.toApplications();
    $("#form\\:tree_node_0 > td > span").shouldBe(visible).click();
    $$("#form\\:tree_node_0_0 > td > span").get(1).shouldBe(visible).click();
    openDeployDialog();
    deployPath(findTestProject());
    $("#deploymentModal\\:closeDeploymentBtn").shouldBe(visible).click();
    $("#form\\:tree_node_0").shouldHave(attribute("aria-expanded", "true"));
    $("#form\\:tree_node_0_0").shouldHave(attribute("aria-expanded", "true"));

    $("#form\\:tree_node_0 > td > span").shouldBe(visible).click();
    openDeployDialog();
    deployPath(findTestProject());
    $("#deploymentModal\\:closeDeploymentBtn").shouldBe(visible).click();
    $("#form\\:tree_node_0").shouldHave(attribute("aria-expanded", "false"));
  }

  private void openDeployOptionsAndAssertVersionRange() {
    showDeploymentOptions();
    $("#deploymentModal\\:versionRangeLabel").shouldNotBe(visible);
    $("#deploymentModal\\:version").click();
    $("#deploymentModal\\:version_items").shouldBe(visible);

    $$("#deploymentModal\\:version_items > li").find(text("RANGE")).click();
    $("#deploymentModal\\:versionRangeLabel").shouldBe(visible);
  }

  private void showDeploymentOptions() {
    if (!$("#deploymentModal\\:deployOptionsPanel").is(visible)) {
      $("#deploymentModal\\:showDeployOptionsBtn").click();
      $("#deploymentModal\\:deployOptionsPanel").shouldBe(visible);
    }
  }

  private void toAppsAndOpenDeployDialog() {
    Navigation.toApplications();
    openDeployDialog();
  }

  private void openDeployDialog() {
    String appName = $$(".activity-name").first().shouldBe(visible).getText();
    $("#form\\:tree\\:0\\:deployBtn").shouldBe(visible).click();
    $("#deploymentModal\\:fileUploadModal").shouldBe(visible);
    $("#deploymentModal\\:fileUploadModal_title").shouldHave(text(appName));
  }

  private void toAppDetailAndOpenDeployment() {
    Navigation.toApplicationDetail(APP);
    $("#appDetailInfoForm\\:showDeployment").shouldBe(visible).click();
    $("#deploymentModal\\:fileUploadModal").shouldBe(visible);
    $("#deploymentModal\\:uploadError").shouldBe(empty);
    $("#deploymentModal\\:fileUploadModal_title").shouldHave(text(APP));
  }
}
