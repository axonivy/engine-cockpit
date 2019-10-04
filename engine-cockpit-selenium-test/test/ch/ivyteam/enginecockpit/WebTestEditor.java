package ch.ivyteam.enginecockpit;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.util.ApplicationTab;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestEditor extends WebTestBase
{
  
  private static final String DEV_MODE_COOKIE = "preview";

  @Test
  void testDevMode(FirefoxDriver driver)
  {
    login(driver);
    driver.manage().deleteCookie(new Cookie(DEV_MODE_COOKIE, "false"));
    
    enableDevMode(driver);
    webAssertThat(() -> assertThat(driver.manage().getCookieNamed(DEV_MODE_COOKIE).getValue()).isEqualTo("true"));
    
    disableDevMode(driver);
    webAssertThat(() -> assertThat(driver.manage().getCookieNamed(DEV_MODE_COOKIE).getValue()).isEqualTo("false"));
  }
  
  @Test
  void testEditor(FirefoxDriver driver)
  {
    String expectedAppYaml = "SecuritySystem: test-ad\n" + 
            "GlobalVariables:\n" + 
            "  myGlobalVariable: \"value\"";
    
    toEditor(driver);
    webAssertThat(() -> assertThat(ApplicationTab.getApplicationCount(driver)).isGreaterThan(1));
    String ivyYamlHints = driver.findElementById("card:editorTabView:0:editorForm:yamlhints").getAttribute("value");
    webAssertThat(() -> assertThat(ivyYamlHints).isNotBlank());
    webAssertThat(() -> assertThat(driver.findElementById("card:editorTabView:0:editorForm:content")
            .getAttribute("value")).isNotBlank());
    ApplicationTab.switchToApplication(driver, "app-test-ad.yaml");
    int tabIndex = ApplicationTab.getSelectedApplicationIndex(driver);
    String appYamlHints = driver.findElementById("card:editorTabView:" + tabIndex + ":editorForm:yamlhints")
            .getAttribute("value");
    webAssertThat(() -> assertThat(appYamlHints).isNotBlank());
    assertThat(ivyYamlHints).isNotEqualTo(appYamlHints);
    webAssertThat(() -> assertThat(driver.findElementById("card:editorTabView:"
            + tabIndex + ":editorForm:content").getAttribute("value")).isEqualTo(expectedAppYaml));
    
  }
  
  private void disableDevMode(FirefoxDriver driver)
  {
    toggleDevMode(driver);
    elementNotAvailable(driver, By.id("menuform:sr_editor"));
  }

  private void enableDevMode(FirefoxDriver driver)
  {
    toggleDevMode(driver);
    webAssertThat(() -> assertThat(driver.findElementById("menuform:sr_editor").isDisplayed()).isTrue());
  }

  private void toggleDevMode(FirefoxDriver driver)
  {
    driver.findElementByXPath("//*[@id='sessionUser']/a").click();
    webAssertThat(() -> assertThat(driver.findElementById("devModeBtn").isDisplayed()).isTrue());
    driver.findElementById("devModeBtn").click();
  }

  private void toEditor(FirefoxDriver driver)
  {
    login(driver);
    driver.manage().deleteCookie(new Cookie(DEV_MODE_COOKIE, "false"));
    enableDevMode(driver);
    Navigation.toEditor(driver);
    saveScreenshot(driver, "editor");
  }
  
}
