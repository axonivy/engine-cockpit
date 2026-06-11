package ch.ivyteam.enginecockpit.deployment;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.engine.EngineUrl;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.codeborne.selenide.WebDriverRunner;

import ch.ivyteam.enginecockpit.util.Navigation;

@Disabled
@IvyWebTest
class WebTestDeployment {

  private static final String APP = isDesigner() ? EngineUrl.applicationName() : "test-ad";

  @BeforeEach
  void beforeEach() {
    login();
  }

  @Test
  void noFile() {
    toAppDetailAndOpenDeployment();
    $(By.id("information:deployment:fileUploadForm:uploadBtn")).shouldBe(disabled);
  }

  @Test
  void invalidFileEnding(@TempDir Path tempDir) throws IOException {
    toAppDetailAndOpenDeployment();
    var tempFile = tempDir.resolve("app.txt");
    Files.createFile(tempFile);
    $(By.id("information:deployment:fileUploadForm:fileUpload_input")).sendKeys(tempFile.toString());
    $(By.id("information:deployment:fileUploadForm:uploadBtn")).shouldBe(disabled);
  }

  @Test
  void corruptZip(@TempDir Path tempDir) throws IOException {
    toAppDetailAndOpenDeployment();
    var tempFile = tempDir.resolve("app.iar");
    Files.createFile(tempFile);
    deployPath(tempFile, "information:deployment");
    $(By.id("information:deployment:uploadStatus")).shouldHave(text("Error"));
    $(By.id("uploadLog")).shouldHave(text("Couldn't deploy 'app.iar'"));
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
    deployPath(findTestProject(), "information:deployment");
    $(By.id("information:deployment:uploadStatus")).shouldHave(text("Success"));
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

  private void deployPath(Path testDataIar, String idPath) {
    $(By.id(idPath + ":fileUploadForm:fileUpload_input")).sendKeys(testDataIar.toString());
    $(By.id(idPath + ":fileUploadForm:uploadBtn")).shouldNotBe(disabled).click();
    $(By.id("uploadLog")).shouldNotBe(empty);
    $(By.id(idPath + ":fileUploadForm")).shouldNotBe(visible);
  }

  @Test
  void deployOptions() {
    toAppDetailAndOpenDeployment();
    showDeploymentOptions();
    PrimeUi.selectOne(By.id("information:deployment:fileUploadForm:deployTestUsers")).selectedItemShould(exactText("AUTO"));
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
    deployPath(findTestProject(), "deployment");
    $(By.id("deployment:closeDeploymentBtn")).shouldBe(visible).click();
    $("#form\\:tree_node_0").shouldHave(attribute("aria-expanded", "true"));

    $("#form\\:tree_node_0 > td > span").shouldBe(visible).click();
    openDeployDialog();
    deployPath(findTestProject(), "deployment");
    $(By.id("deployment:closeDeploymentBtn")).shouldBe(visible).click();
    $("#form\\:tree_node_0").shouldHave(attribute("aria-expanded", "false"));
    driver.manage().window().setSize(oldSize);
  }

  private void showDeploymentOptions() {
    if (!$(By.id("information:deployment:fileUploadForm:deployOptionsPanel")).is(visible)) {
      $(By.id("information:deployment:fileUploadForm:showDeployOptionsBtn")).click();
      $(By.id("information:deployment:fileUploadForm:deployOptionsPanel")).shouldBe(visible);
    }
  }

  private void openDeployDialog() {
    String appName = $$(".activity-name").first().shouldBe(visible).getText();
    $(By.id("form:tree:0:deployBtn")).shouldBe(visible).click();
    $(By.id("deployment:fileUploadModal")).shouldBe(visible);
    appName = org.apache.commons.lang3.StringUtils.substringBefore(appName, " (v");
    $(By.id("deployment:fileUploadModal_title")).shouldHave(text(appName));
  }

  private void toAppDetailAndOpenDeployment() {
    Navigation.toApplicationDetail(APP);
    $(By.id("information:appDetailInfoForm:showDeployment")).shouldBe(visible).click();
    $(By.id("information:deployment:fileUploadModal")).shouldBe(visible);
    $(By.id("information:deployment:fileUploadModal:uploadError")).shouldNotBe(visible);
    $(By.id("information:deployment:fileUploadModal_title")).shouldHave(text(APP));
  }
}
