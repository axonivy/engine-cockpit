package ch.ivyteam.enginecockpit.fileupload;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.supplements.primeui.tester.PrimeUi.SelectBooleanCheckbox;
import com.axonivy.ivy.supplements.primeui.tester.PrimeUi.SelectOneMenu;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.EngineCockpitUrl;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestDeployment extends WebTestBase
{
  private static final String APP = EngineCockpitUrl.isDesignerApp() ? "designer" : "test-ad";
  
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
    if (EngineCockpitUrl.isDesignerApp())
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
    SelectOneMenu testUser = primeUi.selectOne(By.id("deploymentModal:deployTestUsers"));
    SelectBooleanCheckbox overwrite = primeUi.selectBooleanCheckbox(By.id("deploymentModal:overwriteProject"));
    SelectOneMenu cleanup = primeUi.selectOne(By.id("deploymentModal:cleanupProject"));
    SelectOneMenu version = primeUi.selectOne(By.id("deploymentModal:version"));
    SelectOneMenu state = primeUi.selectOne(By.id("deploymentModal:state"));
    SelectOneMenu fileFormat = primeUi.selectOne(By.id("deploymentModal:fileFormat"));

    assertThat(testUser.getSelectedItem()).isEqualTo("AUTO");
    assertThat(overwrite.isChecked()).isFalse();
    assertThat(cleanup.getSelectedItem()).isEqualTo("DISABLED");
    assertThat(version.getSelectedItem()).isEqualTo("AUTO");
    assertThat(state.getSelectedItem()).isEqualTo("ACTIVE_AND_RELEASED");
    assertThat(fileFormat.getSelectedItem()).isEqualTo("AUTO");
    
    SelectBooleanCheckbox checkbox = primeUi.selectBooleanCheckbox(By.id("deploymentModal:overwriteProject"));
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
  void testDeploymentDialogOpenApps()
  {
    toAppsAndOpenDeployDialog();
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
    if (!$("#deploymentModal\\:deployOptionsPanel").isDisplayed())
    {
      $("#deploymentModal\\:showDeployOptionsBtn").click();
      $("#deploymentModal\\:deployOptionsPanel").shouldBe(visible);
    }
  }
  
  private void toAppsAndOpenDeployDialog()
  {
    toApplications();
    
    String appName = $$(".activity-name").first().shouldBe(visible).getText();
    $("#card\\:form\\:tree\\:0\\:deployBtn").shouldBe(visible).click();
    $("#deploymentModal\\:fileUploadModal").shouldBe(visible);
    $("#deploymentModal\\:fileUploadModal_title").shouldHave(text(appName));
  }
  
  private void toAppDetailAndOpenDeployment()
  {
    toApplicationDetail();
    
    $("#appDetailInfoForm\\:showDeployment").shouldBe(visible).click();
    $("#deploymentModal\\:fileUploadModal").shouldBe(visible);
    $("#uploadError").shouldBe(empty);
    $("#deploymentModal\\:fileUploadModal_title").shouldHave(text(APP));
  }
  
  private void toApplicationDetail()
  {
    login();
    Navigation.toApplicationDetail(APP);
  }
  
  private void toApplications()
  {
    login();
    Navigation.toApplications();
  }
}
