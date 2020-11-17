package ch.ivyteam.enginecockpit.fileupload;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.axonivy.ivy.webtest.engine.EngineUrl.DESIGNER;
import static com.axonivy.ivy.webtest.engine.EngineUrl.isDesigner;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.axonivy.ivy.webtest.primeui.widget.SelectBooleanCheckbox;
import com.axonivy.ivy.webtest.primeui.widget.SelectOneMenu;

import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
public class WebTestDeployment
{
  private static final String APP = isDesigner() ? DESIGNER : "test-ad";
  
  @BeforeEach
  void beforeEach()
  {
    login();
  }
  
  @Test
  void testDeploymentNoFile()
  {
    toAppDetailAndOpenDeployment();
    $("#deploymentModal\\:uploadBtn").click();
    $("#uploadError").shouldBe(exactText("Choose a valid file before upload."));
  }

  @Test
  void testDeplomentInvalidFileEnding() throws IOException
  {
    toAppDetailAndOpenDeployment();
    Path createTempFile = Files.createTempFile("app", ".txt");
    $("#fileInput").sendKeys(createTempFile.toString());
    $("#deploymentModal\\:uploadBtn").click();
    $("#uploadError").shouldNotBe(empty);
    $("#uploadError").shouldBe(exactText("Choose a valid file before upload."));
  }
  
  @Test
  void testDeploymentInvalidAppAndBack() throws IOException
  {
    toAppDetailAndOpenDeployment();
    Path createTempFile = Files.createTempFile("app", ".iar");
    $("#fileInput").sendKeys(createTempFile.toString());
    $("#deploymentModal\\:uploadBtn").click();
    $("#uploadLog").shouldNotBe(empty);
    $("#fileUploadForm").shouldNotBe(visible);
    if (isDesigner())
    {
      $("#uploadLog").shouldHave(text("404"));
    }
    else
    {
      $("#uploadLog").shouldHave(text("Deployment failed: No ivy projects found in deployment artifact.."));
    }
    
    $("#deploymentModal\\:backBtn").click();
    $("#fileUploadForm").shouldBe(visible);
    $("#uploadLog").shouldNotBe(visible);
  }
  
  @Test
  void testDeploymentDeployOptions() 
  {
    toAppDetailAndOpenDeployment();
    showDeploymentOptions();
    SelectOneMenu testUser = PrimeUi.selectOne(By.id("deploymentModal:deployTestUsers"));
    SelectBooleanCheckbox overwrite = PrimeUi.selectBooleanCheckbox(By.id("deploymentModal:overwriteConfig"));
    SelectOneMenu cleanup = PrimeUi.selectOne(By.id("deploymentModal:cleanupConfig"));
    SelectOneMenu version = PrimeUi.selectOne(By.id("deploymentModal:version"));
    SelectOneMenu state = PrimeUi.selectOne(By.id("deploymentModal:state"));
    SelectOneMenu fileFormat = PrimeUi.selectOne(By.id("deploymentModal:fileFormat"));

    assertThat(testUser.getSelectedItem()).isEqualTo("AUTO");
    assertThat(overwrite.isChecked()).isFalse();
    assertThat(cleanup.getSelectedItem()).isEqualTo("DISABLED");
    assertThat(version.getSelectedItem()).isEqualTo("AUTO");
    assertThat(state.getSelectedItem()).isEqualTo("ACTIVE_AND_RELEASED");
    assertThat(fileFormat.getSelectedItem()).isEqualTo("AUTO");
    
    SelectBooleanCheckbox checkbox = PrimeUi.selectBooleanCheckbox(By.id("deploymentModal:overwriteConfig"));
    checkbox.setChecked();
    assertThat(overwrite.isChecked()).isTrue();
  }
  
  @Test
  void testDeploymentDeployOptionsVersionRange()
  {
    toAppDetailAndOpenDeployment();
    openDeployOptionsAndAssertVersionRange();
  }
  
  @Test
  void testDeploymentDeployOptionsVersionRange_AppsView()
  {
    toAppsAndOpenDeployDialog();
    openDeployOptionsAndAssertVersionRange();
  }
  
  private void openDeployOptionsAndAssertVersionRange()
  {
    showDeploymentOptions();
    $("#deploymentModal\\:versionRangeLabel").shouldNotBe(visible);
    $("#deploymentModal\\:version").click();
    $("#deploymentModal\\:version_items").shouldBe(visible);
    
    $$("#deploymentModal\\:version_items > li").find(text("RANGE")).click();
    $("#deploymentModal\\:versionRangeLabel").shouldBe(visible);
  }
  
  private void showDeploymentOptions()
  {
    if (!$("#deploymentModal\\:deployOptionsPanel").is(visible))
    {
      $("#deploymentModal\\:showDeployOptionsBtn").click();
      $("#deploymentModal\\:deployOptionsPanel").shouldBe(visible);
    }
  }
  
  private void toAppsAndOpenDeployDialog()
  {
    Navigation.toApplications();
    String appName = $$(".activity-name").first().shouldBe(visible).getText();
    $("#card\\:form\\:tree\\:0\\:deployBtn").shouldBe(visible).click();
    $("#deploymentModal\\:fileUploadModal").shouldBe(visible);
    $("#deploymentModal\\:fileUploadModal_title").shouldHave(text(appName));
  }
  
  private void toAppDetailAndOpenDeployment()
  {
    Navigation.toApplicationDetail(APP);
    $("#appDetailInfoForm\\:showDeployment").shouldBe(visible, enabled).click();
    $("#deploymentModal\\:fileUploadModal").shouldBe(visible);
    $("#uploadError").shouldBe(empty);
    $("#deploymentModal\\:fileUploadModal_title").shouldHave(text(APP));
  }
  
}
