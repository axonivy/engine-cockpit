package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.axonivy.ivy.supplements.primeui.tester.PrimeUi;
import com.axonivy.ivy.supplements.primeui.tester.PrimeUi.SelectBooleanCheckbox;

import ch.ivyteam.enginecockpit.util.EngineCockpitUrl;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestApplicationDetail extends WebTestBase
{
  private static final String APP = EngineCockpitUrl.isDesignerApp() ? "designer" : "test-ad";

  @Test
  void testApplicationDetailDashboardContent(FirefoxDriver driver)
  {
    toApplicationDetail(driver);
    
    checkOverviewBoxes(driver);
    checkInfoPanels(driver);
  }

  @Test
  void testChangeEnvironment(FirefoxDriver driver)
  {
    toApplicationDetail(driver);
    
    String newEnv = toggleEnvAndSave(driver);
    
    driver.navigate().refresh();
    saveScreenshot(driver, "refresh");
    await().untilAsserted(() -> assertThat(driver.findElementById("appDetailInfoForm:activeEnvironmentSelect_label").getText()).isEqualTo(newEnv));
  
    String oldEnv = toggleEnvAndSave(driver);
    saveScreenshot(driver, "back");
    await().untilAsserted(() -> assertThat(driver.findElementById("appDetailInfoForm:activeEnvironmentSelect_label").getText()).isEqualTo(oldEnv));
  }
  
  @Test
  void testAdSync(FirefoxDriver driver)
  {
    if (APP.equals("designer"))
    {
      return;
    }
    toApplicationDetail(driver);
    
    driver.findElementById("appDetailSecurityForm:synchronizeSecurity").click();
    saveScreenshot(driver, "sync");
    await().ignoreExceptionsInstanceOf(StaleElementReferenceException.class).untilAsserted(() -> assertThat(
            driver.findElementByXPath("//*[@id='appDetailSecurityForm:synchronizeSecurity']/span[1]").getAttribute("class")).doesNotContain("fa-spin"));
    
    saveScreenshot(driver, "sync_finished");
    await().untilAsserted(() -> assertThat(driver.findElementById("appDetailSecurityForm:showAdSyncLogBtn").isDisplayed()).isTrue());
  }
  
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
    saveScreenshot(driver, "deploy_ok");
  }
  
  @Test
  void testDeploymentDeployOptions(FirefoxDriver driver) 
  {
    toAppDetailAndOpenDeployment(driver);

    String deployOptions = driver.executeScript("return getDeployOptions()").toString();
    String expectedDefaultDeployOptions = "{configuration={cleanup=DISABLED, overwrite=false}, deployTestUsers=FALSE, target={fileFormat=AUTO, state=ACTIVE_AND_RELEASED, version=AUTO, versionRange=}}";
    assertThat(deployOptions).isEqualTo(expectedDefaultDeployOptions);
    
    SelectBooleanCheckbox checkbox = new PrimeUi(driver).selectBooleanCheckbox(By.id("deploymentModal:overwriteProject"));
    checkbox.setChecked();
    webAssertThat(() -> assertThat(checkbox.isChecked()).isTrue());
    saveScreenshot(driver, "change_options");
    deployOptions = driver.executeScript("return getDeployOptions()").toString();
    expectedDefaultDeployOptions = "{configuration={cleanup=DISABLED, overwrite=true}, deployTestUsers=FALSE, target={fileFormat=AUTO, state=ACTIVE_AND_RELEASED, version=AUTO, versionRange=}}";
    assertThat(deployOptions).isEqualTo(expectedDefaultDeployOptions);
  }
  
  @Test
  void testDeploymentDeployOptionsVersionRange(FirefoxDriver driver)
  {
    toAppDetailAndOpenDeployment(driver);
    
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

  private void toAppDetailAndOpenDeployment(FirefoxDriver driver)
  {
    toApplicationDetail(driver);
    
    driver.findElementById("appDetailInfoForm:showDeployment").click();
    saveScreenshot(driver, "deploy");
    webAssertThat(() -> assertThat(driver.findElementById("deploymentModal:fileUploadModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("uploadError").getText()).isEmpty());
  }
  
  private String toggleEnvAndSave(FirefoxDriver driver)
  {
    String setEnv = driver.findElementById("appDetailInfoForm:activeEnvironmentSelect_label").getText();
    String newEnv = setEnv.equals("Default") ? "test" : "Default";
    driver.findElementById("appDetailInfoForm:activeEnvironmentSelect_label").click();
    saveScreenshot(driver, "env_menu");
    await().untilAsserted(() -> assertThat(driver.findElementById("appDetailInfoForm:activeEnvironmentSelect_items").isDisplayed()).isTrue());
    
    driver.findElementByXPath("//*[@id='appDetailInfoForm:activeEnvironmentSelect_items']/li[text()='" + newEnv + "']").click();
    saveScreenshot(driver, "change_env");
    await().untilAsserted(() -> assertThat(driver.findElementById("appDetailInfoForm:activeEnvironmentSelect_label").getText()).isEqualTo(newEnv));
    
    driver.findElementById("appDetailInfoForm:saveApplicationInformation").click();
    saveScreenshot(driver, "save_changes");
    await().untilAsserted(() -> assertThat(driver.findElementById("appDetailInfoForm:informationSaveSuccess_container").isDisplayed()).isTrue());
    return newEnv;
  }
  
  private void toApplicationDetail(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toApplicationDetail(driver, APP);
    saveScreenshot(driver, "app_detail");
  }
  
  private void checkOverviewBoxes(FirefoxDriver driver)
  {
    List<WebElement> overviewBoxes = driver.findElementsByClassName("overview-box-content");
    assertThat(overviewBoxes).hasSize(4);
    List<String> boxesExpect = new ArrayList<>(
            Arrays.asList("Sessions", "Users", "Running Cases", "Process Models"));
    overviewBoxes.stream().map(b -> b.findElement(new By.ByClassName("overview-box-title")).getText())
            .forEach(t -> assertThat(t).isNotEmpty().isIn(boxesExpect));
    overviewBoxes.stream().map(b -> b.findElement(new By.ByClassName("overview-box-count")).getText())
            .forEach(c -> assertThat(c).isNotEmpty());
  }
  
  private void checkInfoPanels(FirefoxDriver driver)
  {
    List<WebElement> infoPanels = driver.findElementsByClassName("ui-panel");
    assertThat(infoPanels).hasSize(4);
  }
}
