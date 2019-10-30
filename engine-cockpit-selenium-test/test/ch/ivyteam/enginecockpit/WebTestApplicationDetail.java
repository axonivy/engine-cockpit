package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.util.EngineCockpitUrl;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestApplicationDetail extends WebTestBase
{
  private static final String APP = EngineCockpitUrl.isDesignerApp() ? "designer" : "test-ad";

  @Test
  void testApplicationDetailDashboardContent()
  {
    toApplicationDetail();
    
    webAssertThat(() -> assertThat(driver.findElementsByClassName("overview-box-content")).hasSize(4));
    webAssertThat(() -> assertThat(driver.findElementsByClassName("ui-panel")).hasSize(4));
  }

  @Test
  void testChangeEnvironment()
  {
    toApplicationDetail();
    
    String newEnv = toggleEnvAndSave();
    
    driver.navigate().refresh();
    saveScreenshot("refresh");
    webAssertThat(() -> assertThat(driver.findElementById("appDetailInfoForm:activeEnvironmentSelect_label").getText()).isEqualTo(newEnv));
  
    String oldEnv = toggleEnvAndSave();
    saveScreenshot("back");
    webAssertThat(() -> assertThat(driver.findElementById("appDetailInfoForm:activeEnvironmentSelect_label").getText()).isEqualTo(oldEnv));
  }
  
  @Test
  void testAdSync()
  {
    if (APP.equals("designer"))
    {
      return;
    }
    toApplicationDetail();
    
    driver.findElementById("appDetailSecurityForm:synchronizeSecurity").click();
    saveScreenshot("sync");
    webAssertThat(() -> assertThat(driver.findElementByXPath("//*[@id='appDetailSecurityForm:synchronizeSecurity']/span[1]").getAttribute("class")).doesNotContain("fa-spin"));
    
    saveScreenshot("sync_finished");
    webAssertThat(() -> assertThat(driver.findElementById("appDetailSecurityForm:showAdSyncLogBtn").isDisplayed()).isTrue());
  }

  private String toggleEnvAndSave()
  {
    String setEnv = driver.findElementById("appDetailInfoForm:activeEnvironmentSelect_label").getText();
    String newEnv = setEnv.equals("Default") ? "test" : "Default";
    driver.findElementById("appDetailInfoForm:activeEnvironmentSelect_label").click();
    saveScreenshot("env_menu");
    webAssertThat(() -> assertThat(driver.findElementById("appDetailInfoForm:activeEnvironmentSelect_items").isDisplayed()).isTrue());
    
    driver.findElementByXPath("//*[@id='appDetailInfoForm:activeEnvironmentSelect_items']/li[text()='" + newEnv + "']").click();
    saveScreenshot("change_env");
    webAssertThat(() -> assertThat(driver.findElementById("appDetailInfoForm:activeEnvironmentSelect_label").getText()).isEqualTo(newEnv));
    
    driver.findElementById("appDetailInfoForm:saveApplicationInformation").click();
    saveScreenshot("save_changes");
    webAssertThat(() -> assertThat(driver.findElementById("appDetailInfoForm:informationSaveSuccess_container").isDisplayed()).isTrue());
    return newEnv;
  }
  
  private void toApplicationDetail()
  {
    login();
    Navigation.toApplicationDetail(driver, APP);
    saveScreenshot("app_detail");
  }
  
}
