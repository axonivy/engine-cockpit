package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.util.EngineCockpitUrl;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestApplicationDetail extends WebTestBase
{
  private String app = EngineCockpitUrl.isDesignerApp() ? "designer" : "test-ad";

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
    if (app.equals("designer"))
    {
      return;
    }
    toApplicationDetail(driver);
    
    driver.findElementById("appDetailSecurityForm:synchronizeSecurity").click();
    saveScreenshot(driver, "sync");
    await().untilAsserted(() -> assertThat(driver.findElementByXPath("//*[@id='appDetailSecurityForm:synchronizeSecurity']/span[1]").getAttribute("class")).doesNotContain("fa-spin"));
    
    saveScreenshot(driver, "sync_finished");
    await().untilAsserted(() -> assertThat(driver.findElementById("appDetailSecurityForm:showAdSyncLogBtn").isDisplayed()).isTrue());
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
    Navigation.toApplicationDetail(driver, app);
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
    List<String> panelsExpect = new ArrayList<>(
            Arrays.asList("Application information", "Security System", "Activity", "Properties"));
    infoPanels.stream().map(p -> p.findElement(new By.ByClassName("ui-panel-title")).getText())
            .forEach(t -> assertThat(t).isNotEmpty().isIn(panelsExpect));
  }
}
