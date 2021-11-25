package ch.ivyteam.enginecockpit.fileupload;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.axonivy.ivy.webtest.engine.EngineUrl.DESIGNER;
import static com.axonivy.ivy.webtest.engine.EngineUrl.isDesigner;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;

import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
public class WebTestDeployment {
  private static final String APP = isDesigner() ? DESIGNER : "test-ad";

  @BeforeEach
  void beforeEach() {
    login();
  }

  @Test
  void noFile() {
    toAppDetailAndOpenDeployment();
    $("#deploymentModal\\:uploadBtn").click();
    $("#uploadError").shouldBe(exactText("Choose a valid file before upload."));
  }

  @Test
  void invalidFileEnding() throws IOException {
    toAppDetailAndOpenDeployment();
    Path createTempFile = Files.createTempFile("app", ".txt");
    $("#fileInput").sendKeys(createTempFile.toString());
    $("#deploymentModal\\:uploadBtn").click();
    $("#uploadError").shouldNotBe(empty);
    $("#uploadError").shouldBe(exactText("Choose a valid file before upload."));
  }

  @Test
  void invalidAppAndBack() throws IOException {
    toAppDetailAndOpenDeployment();
    deployPath(Files.createTempFile("app", ".iar"));
    if (isDesigner()) {
      $("#uploadLog").shouldHave(text("404"));
    } else {
      $("#uploadLog").shouldHave(text("Deployment failed: No ivy projects found in deployment artifact.."));
    }

    $("#deploymentModal\\:backBtn").click();
    $("#fileUploadForm").shouldBe(visible);
    $("#uploadLog").shouldNotBe(visible);
    $("#deploymentModal\\:uploadBtn > .ui-icon").shouldNotHave(cssClass("si-is-spinning"));
  }

  @Test
  void validApp() {
    if (isDesigner()) {
      return;
    }
    toAppDetailAndOpenDeployment();
    deployPath(Path.of(System.getProperty("basedir")).getParent().resolve("engine-cockpit-test-data").resolve("target").resolve("engine-cockpit-test-data-9.3.0-SNAPSHOT.iar"));
    $("#uploadLog").shouldHave(text("Using default>DeploymentOptions"),
            text("successful deployed to application"));
  }

  @Test
  void validAppWithDeployOptions() {
    if (isDesigner()) {
      return;
    }
    toAppDetailAndOpenDeployment();
    showDeploymentOptions();
    deployPath(Path.of(System.getProperty("basedir")).getParent().resolve("engine-cockpit-test-data").resolve("target").resolve("engine-cockpit-test-data-9.3.0-SNAPSHOT.iar"));
    $("#uploadLog").shouldHave(text("Using resource.params>DeploymentOptions"),
            text("successful deployed to application"));
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
    String appName = $$(".activity-name").first().shouldBe(visible).getText();
    $("#card\\:form\\:tree\\:0\\:deployBtn").shouldBe(visible).click();
    $("#deploymentModal\\:fileUploadModal").shouldBe(visible);
    $("#deploymentModal\\:fileUploadModal_title").shouldHave(text(appName));
  }

  private void toAppDetailAndOpenDeployment() {
    Navigation.toApplicationDetail(APP);
    $("#appDetailInfoForm\\:showDeployment").shouldBe(visible).click();
    $("#deploymentModal\\:fileUploadModal").shouldBe(visible);
    $("#uploadError").shouldBe(empty);
    $("#deploymentModal\\:fileUploadModal_title").shouldHave(text(APP));
  }

}
