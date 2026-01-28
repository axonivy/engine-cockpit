package ch.ivyteam.enginecockpit.deployment;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.axonivy.ivy.webtest.engine.EngineUrl.DESIGNER;
import static com.axonivy.ivy.webtest.engine.EngineUrl.isDesigner;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.disabled;
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
import org.openqa.selenium.Dimension;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.codeborne.selenide.WebDriverRunner;

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
    $(By.id("fileUploadForm:uploadBtn")).shouldBe(disabled);
  }

  @Test
  void invalidFileEnding(@TempDir Path tempDir) throws IOException {
    toAppDetailAndOpenDeployment();
    var tempFile = tempDir.resolve("app.txt");
    Files.createFile(tempFile);
    $(By.id("fileUploadForm:fileUpload_input")).sendKeys(tempFile.toString());
    $(By.id("fileUploadForm:uploadBtn")).shouldBe(disabled);
  }

  @Test
  void emptyApp(@TempDir Path tempDir) throws IOException {
    toAppDetailAndOpenDeployment();
    var tempFile = tempDir.resolve("app.iar");
    Files.createFile(tempFile);
    deployPath(tempFile);
    $(By.id("uploadStatus")).shouldHave(text("Success"));
    $(By.id("uploadLog")).shouldHave(text("No projects to deploy"), text("successfully deployed to application"));
  }

  @Test
  void validApp() {
    if (isDesigner()) {
      return;
    }
    toAppDetailAndOpenDeployment();
    deployAndAssert("Using options>DeploymentOptions");
  }

  @Test
  void validAppWithDeployOptions() {
    if (isDesigner()) {
      return;
    }
    toAppDetailAndOpenDeployment();
    showDeploymentOptions();
    deployAndAssert("Using options>DeploymentOptions");
  }

  private void deployAndAssert(String expectedDeployOptionsText) {
    deployPath(findTestProject());
    $(By.id("uploadStatus")).shouldHave(text("Success"));
    $(By.id("uploadLog")).shouldHave(text(expectedDeployOptionsText), text("successfully deployed to application"));
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
    $(By.id("fileUploadForm:fileUpload_input")).sendKeys(testDataIar.toString());
    $(By.id("fileUploadForm:uploadBtn")).shouldNotBe(disabled).click();
    $(By.id("uploadLog")).shouldNotBe(empty);
    $(By.id("fileUploadForm")).shouldNotBe(visible);
  }

  @Test
  void deployOptions() {
    toAppDetailAndOpenDeployment();
    showDeploymentOptions();
    PrimeUi.selectOne(By.id("fileUploadForm:deployTestUsers")).selectedItemShould(exactText("AUTO"));
  }

  @Test
  void keepExpandedState() {
    if (isDesigner()) {
      return;
    }
    var driver = WebDriverRunner.getWebDriver();
    var oldSize = driver.manage().window().getSize();
    driver.manage().window().setSize(new Dimension(1600, 1500));
    Navigation.toApplications();
    $("#form\\:tree_node_0 > td > span").shouldBe(visible).click();
    openDeployDialog();
    deployPath(findTestProject());
    $(By.id("closeDeploymentBtn")).shouldBe(visible).click();
    $("#form\\:tree_node_0").shouldHave(attribute("aria-expanded", "true"));

    $("#form\\:tree_node_0 > td > span").shouldBe(visible).click();
    openDeployDialog();
    deployPath(findTestProject());
    $(By.id("closeDeploymentBtn")).shouldBe(visible).click();
    $("#form\\:tree_node_0").shouldHave(attribute("aria-expanded", "false"));
    driver.manage().window().setSize(oldSize);
  }

  private void showDeploymentOptions() {
    if (!$(By.id("fileUploadForm:deployOptionsPanel")).is(visible)) {
      $(By.id("fileUploadForm:showDeployOptionsBtn")).click();
      $(By.id("fileUploadForm:deployOptionsPanel")).shouldBe(visible);
    }
  }

  private void openDeployDialog() {
    String appName = $$(".activity-name").first().shouldBe(visible).getText();
    $(By.id("form:tree:0:deployBtn")).shouldBe(visible).click();
    $(By.id("fileUploadModal")).shouldBe(visible);
    appName = org.apache.commons.lang3.StringUtils.substringBefore(appName, " (v");
    $(By.id("fileUploadModal_title")).shouldHave(text(appName));
  }

  private void toAppDetailAndOpenDeployment() {
    Navigation.toApplicationDetail(APP);
    $(By.id("appDetailInfoForm:showDeployment")).shouldBe(visible).click();
    $(By.id("fileUploadModal")).shouldBe(visible);
    $(By.id("fileUploadModal:uploadError")).shouldNotBe(visible);
    $(By.id("fileUploadModal_title")).shouldHave(text(APP));
  }
}
