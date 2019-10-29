package ch.ivyteam.enginecockpit.fileupload;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.supplements.primeui.tester.PrimeUi;
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
    
    driver.findElementById("deploymentModal:uploadBtn").click();
    saveScreenshot("no_file");
    webAssertThat(() -> assertThat(driver.findElementById("uploadError").getText()).isEqualTo("Choose a valid file before upload."));
  }

  @Test
  void testDeplomentInvalidFileEnding() throws IOException
  {
    toAppDetailAndOpenDeployment();
    
    Path createTempFile = Files.createTempFile("app", ".txt");
    driver.findElementById("fileInput").sendKeys(createTempFile.toString());
    driver.findElementById("deploymentModal:uploadBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("uploadError").getText()).isNotEmpty());
    saveScreenshot("wrong_app_format");
    webAssertThat(() -> assertThat(driver.findElementById("uploadError").getText()).isEqualTo("Choose a valid file before upload."));
  }
  
  @Test
  void testDeploymentInvalidAppAndBack() throws IOException
  {
    toAppDetailAndOpenDeployment();
    
    Path createTempFile = Files.createTempFile("app", ".iar");
    driver.findElementById("fileInput").sendKeys(createTempFile.toString());
    driver.findElementById("deploymentModal:uploadBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("uploadLog").getText()).isNotEmpty());
    webAssertThat(() -> assertThat(driver.findElementById("fileUploadForm").isDisplayed()).isFalse());
    if (EngineCockpitUrl.isDesignerApp())
    {
      webAssertThat(() -> assertThat(driver.findElementById("uploadLog").getText()).contains("404"));
    }
    else
    {
      webAssertThat(() -> assertThat(driver.findElementById("uploadLog").getText()).contains("Deployment failed: No ivy projects found in deployment artifact.."));
    }
    saveScreenshot("deploy_ok");
    
    driver.findElementById("deploymentModal:backBtn").click();
    saveScreenshot("back");
    webAssertThat(() -> assertThat(driver.findElementById("fileUploadForm").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("uploadLog").isDisplayed()).isFalse());
  }
  
  @Test
  void testDeploymentDeployOptions() 
  {
    toAppDetailAndOpenDeployment();
    
    showDeploymentOptions();
    PrimeUi primeUi = new PrimeUi(driver);
    SelectOneMenu testUser = primeUi.selectOne(By.id("deploymentModal:deployTestUsers"));
    SelectBooleanCheckbox overwrite = primeUi.selectBooleanCheckbox(By.id("deploymentModal:overwriteProject"));
    SelectOneMenu cleanup = primeUi.selectOne(By.id("deploymentModal:cleanupProject"));
    SelectOneMenu version = primeUi.selectOne(By.id("deploymentModal:version"));
    SelectOneMenu state = primeUi.selectOne(By.id("deploymentModal:state"));
    SelectOneMenu fileFormat = primeUi.selectOne(By.id("deploymentModal:fileFormat"));

    webAssertThat(() -> assertThat(testUser.getSelectedItem()).isEqualTo("AUTO"));
    webAssertThat(() -> assertThat(overwrite.isChecked()).isFalse());
    webAssertThat(() -> assertThat(cleanup.getSelectedItem()).isEqualTo("DISABLED"));
    webAssertThat(() -> assertThat(version.getSelectedItem()).isEqualTo("AUTO"));
    webAssertThat(() -> assertThat(state.getSelectedItem()).isEqualTo("ACTIVE_AND_RELEASED"));
    webAssertThat(() -> assertThat(fileFormat.getSelectedItem()).isEqualTo("AUTO"));
    
    SelectBooleanCheckbox checkbox = new PrimeUi(driver).selectBooleanCheckbox(By.id("deploymentModal:overwriteProject"));
    checkbox.setChecked();
    webAssertThat(() -> assertThat(overwrite.isChecked()).isTrue());
    saveScreenshot("change_options");
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
    
    webAssertThat(() -> assertThat(driver.findElementById("deploymentModal:versionRangeLabel").isDisplayed()).isFalse());
    driver.findElementById("deploymentModal:version").click();
    saveScreenshot("versions");
    webAssertThat(() -> assertThat(driver.findElementById("deploymentModal:version_items").isDisplayed()).isTrue());
    
    driver.findElementByXPath("//ul[@id='deploymentModal:version_items']/li[text()='RANGE']").click();
    webAssertThat(() -> assertThat(driver.findElementById("deploymentModal:versionRangeLabel").isDisplayed()).isTrue());
    saveScreenshot("show_range");
  }
  
  private void showDeploymentOptions()
  {
    if (!driver.findElementById("deploymentModal:deployOptionsPanel").isDisplayed())
    {
      driver.findElementById("deploymentModal:showDeployOptionsBtn").click();
      webAssertThat(() -> assertThat(driver.findElementById("deploymentModal:deployOptionsPanel").isDisplayed()).isTrue());
    }
    saveScreenshot("show_options");
  }
  
  private void toAppsAndOpenDeployDialog()
  {
    toApplications();
    
    String appName = driver.findElementsByClassName("activity-name").get(0).getText();
    driver.findElementById("card:form:tree:0:deployBtn").click();
    saveScreenshot("deploy_dialog");
    webAssertThat(() -> assertThat(driver.findElementById("deploymentModal:fileUploadModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("deploymentModal:fileUploadModal_title").getText())
            .contains(appName));
  }
  
  private void toAppDetailAndOpenDeployment()
  {
    toApplicationDetail();
    
    driver.findElementById("appDetailInfoForm:showDeployment").click();
    saveScreenshot("deploy");
    webAssertThat(() -> assertThat(driver.findElementById("deploymentModal:fileUploadModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("uploadError").getText()).isEmpty());
    webAssertThat(() -> assertThat(driver.findElementById("deploymentModal:fileUploadModal_title")
            .getText()).contains(APP));
  }
  
  private void toApplicationDetail()
  {
    login();
    Navigation.toApplicationDetail(driver, APP);
    saveScreenshot("app_detail");
  }
  
  private void toApplications()
  {
    login();
    Navigation.toApplications(driver);
    saveScreenshot("apps");
  }
}
