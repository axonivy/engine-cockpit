package ch.ivyteam.enginecockpit.fileupload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.axonivy.ivy.supplements.primeui.tester.PrimeUi;
import com.axonivy.ivy.supplements.primeui.tester.PrimeUi.SelectBooleanCheckbox;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.EngineCockpitUrl;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestDeployment extends WebTestBase
{
  private static final String APP = EngineCockpitUrl.isDesignerApp() ? "designer" : "test-ad";
  
  @Test
  void testDeploymentNoFile(FirefoxDriver driver)
  {
    toAppDetailAndOpenDeployment(driver);
    
    driver.findElementById("deploymentModal:uploadBtn").click();
    saveScreenshot(driver, "no_file");
    webAssertThat(() -> assertThat(driver.findElementById("uploadError").getText()).isEqualTo("Choose a valid file before upload."));
  }

  @Test
  void testDeplomentInvalidFileEnding(FirefoxDriver driver) throws IOException
  {
    toAppDetailAndOpenDeployment(driver);
    
    Path createTempFile = Files.createTempFile("app", ".txt");
    driver.findElementById("fileInput").sendKeys(createTempFile.toString());
    driver.findElementById("deploymentModal:uploadBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("uploadError").getText()).isNotEmpty());
    saveScreenshot(driver, "wrong_app_format");
    webAssertThat(() -> assertThat(driver.findElementById("uploadError").getText()).isEqualTo("Choose a valid file before upload."));
  }
  
  @Test
  void testDeploymentInvalidApp(FirefoxDriver driver) throws IOException
  {
    toAppDetailAndOpenDeployment(driver);
    
    Path createTempFile = Files.createTempFile("app", ".iar");
    driver.findElementById("fileInput").sendKeys(createTempFile.toString());
    driver.findElementById("deploymentModal:uploadBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("uploadLog").getText()).isNotEmpty());
    if (EngineCockpitUrl.isDesignerApp())
    {
      webAssertThat(() -> assertThat(driver.findElementById("uploadLog").getText()).contains("404"));
    }
    else
    {
      webAssertThat(() -> assertThat(driver.findElementById("uploadLog").getText()).doesNotContain("404"));
    }
    saveScreenshot(driver, "deploy_ok");
  }
  
  @Test
  void testDeploymentDeployOptions(FirefoxDriver driver) 
  {
    toAppDetailAndOpenDeployment(driver);
    
    showDeploymentOptions(driver);

    String deployOptions = driver.executeScript("return getDeployOptions()").toString();
    String expectedDefaultDeployOptions = "{configuration={cleanup=DISABLED, overwrite=false}, deployTestUsers=FALSE, target={fileFormat=AUTO, state=ACTIVE_AND_RELEASED, version=AUTO}}";
    assertThat(deployOptions).isEqualTo(expectedDefaultDeployOptions);
    
    SelectBooleanCheckbox checkbox = new PrimeUi(driver).selectBooleanCheckbox(By.id("deploymentModal:overwriteProject"));
    checkbox.setChecked();
    webAssertThat(() -> assertThat(checkbox.isChecked()).isTrue());
    saveScreenshot(driver, "change_options");
    deployOptions = driver.executeScript("return getDeployOptions()").toString();
    expectedDefaultDeployOptions = "{configuration={cleanup=DISABLED, overwrite=true}, deployTestUsers=FALSE, target={fileFormat=AUTO, state=ACTIVE_AND_RELEASED, version=AUTO}}";
    assertThat(deployOptions).isEqualTo(expectedDefaultDeployOptions);
  }
  
  @Test
  void testDeploymentDeployOptionsVersionRange(FirefoxDriver driver)
  {
    toAppDetailAndOpenDeployment(driver);
    
    showDeploymentOptions(driver);
    
    webAssertThat(() -> assertThat(driver.findElementById("deploymentModal:versionRange").isDisplayed()).isFalse());
    driver.findElementById("deploymentModal:version").click();
    saveScreenshot(driver, "versions");
    webAssertThat(() -> assertThat(driver.findElementById("deploymentModal:version_items").isDisplayed()).isTrue());
    
    driver.findElementByXPath("//ul[@id='deploymentModal:version_items']/li[text()='RANGE']").click();
    webAssertThat(() -> assertThat(driver.findElementById("deploymentModal:versionRange").isDisplayed()).isTrue());
    saveScreenshot(driver, "show_range");
    
    driver.findElementById("deploymentModal:version").click();
    webAssertThat(() -> assertThat(driver.findElementById("deploymentModal:version_items").isDisplayed()).isTrue());
    driver.findElementByXPath("//ul[@id='deploymentModal:version_items']/li[text()='RELEASED']").click();
    webAssertThat(() -> assertThat(driver.findElementById("deploymentModal:versionRange").isDisplayed()).isFalse());
    saveScreenshot(driver, "hide_range");
  }
  
  @Test
  void testOpenDeployAppModal(FirefoxDriver driver)
  {
    toApplications(driver);
    
    openDeployApplicationModal(driver);
  }
  
  private void showDeploymentOptions(FirefoxDriver driver)
  {
    if (!driver.findElementById("deploymentModal:deployOptionsPanel").isDisplayed())
    {
      driver.findElementById("deploymentModal:showDeployOptionsBtn").click();
      await().until(() -> driver.findElementById("deploymentModal:deployOptionsPanel").isDisplayed());
    }
  }
  
  private void openDeployApplicationModal(FirefoxDriver driver)
  {
    driver.findElementById("card:form:addButton").click();
    webAssertThat(() -> assertThat(driver.findElementById("card:form:addMenu").isDisplayed()).isTrue());
    driver.findElementById("card:form:deployApplicationBtn").click();
    saveScreenshot(driver, "deploy_app_dialog");
    await().untilAsserted(
            () -> assertThat(driver.findElementById("deploymentModal:fileUploadModal").isDisplayed()).isTrue());
  }
  
  private void toAppDetailAndOpenDeployment(FirefoxDriver driver)
  {
    toApplicationDetail(driver);
    
    driver.findElementById("appDetailInfoForm:showDeployment").click();
    saveScreenshot(driver, "deploy");
    webAssertThat(() -> assertThat(driver.findElementById("deploymentModal:fileUploadModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("uploadError").getText()).isEmpty());
  }
  
  private void toApplications(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toApplications(driver);
    saveScreenshot(driver, "applications");
  }
  
  private void toApplicationDetail(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toApplicationDetail(driver, APP);
    saveScreenshot(driver, "app_detail");
  }
}
