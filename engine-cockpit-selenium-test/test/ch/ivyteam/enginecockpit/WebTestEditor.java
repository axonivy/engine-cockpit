package ch.ivyteam.enginecockpit;


import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.text.StringEscapeUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;

import ch.ivyteam.enginecockpit.util.ApplicationTab;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestEditor extends WebTestBase
{
  
  private static final String DEV_MODE_COOKIE = "preview";

  @Test
  void testDevMode()
  {
    login();
    driver.manage().deleteCookie(new Cookie(DEV_MODE_COOKIE, "false"));
    
    enableDevMode();
    webAssertThat(() -> assertThat(driver.manage().getCookieNamed(DEV_MODE_COOKIE).getValue()).isEqualTo("true"));
    
    disableDevMode();
    webAssertThat(() -> assertThat(driver.manage().getCookieNamed(DEV_MODE_COOKIE).getValue()).isEqualTo("false"));
  }
  
  @Test
  void testEditor()
  {
    toEditor();
    webAssertThat(() -> assertThat(ApplicationTab.getApplicationCount(driver)).isGreaterThan(1));
    String ivyYamlHints = driver.findElementById("card:editorTabView:0:editorForm:yamlhints").getAttribute("value");
    webAssertThat(() -> assertThat(ivyYamlHints).isNotBlank());
    webAssertThat(() -> assertThat(driver.findElementById("card:editorTabView:0:editorForm:content")
            .getAttribute("value")).isNotBlank());
    ApplicationTab.switchToApplication(driver, "app-test-ad.yaml");
    saveScreenshot("app-test-ad-yaml");
    int tabIndex = ApplicationTab.getSelectedApplicationIndex(driver);
    String appYamlHints = driver.findElementById("card:editorTabView:" + tabIndex + ":editorForm:yamlhints")
            .getAttribute("value");
    webAssertThat(() -> assertThat(appYamlHints).isNotBlank());
    assertThat(ivyYamlHints).isNotEqualTo(appYamlHints);
    webAssertThat(() -> assertThat(driver.findElementById("card:editorTabView:"
            + tabIndex + ":editorForm:content").getAttribute("value")).contains("SecuritySystem: test-ad"));
  }
  
  @Test
  void testEditorSaveErrorsDialog()
  {
    toEditor();
    ApplicationTab.switchToApplication(driver, "app-test.yaml");
    int tabIndex = ApplicationTab.getSelectedApplicationIndex(driver);
    String editorContentId = "card:editorTabView:" + tabIndex + ":editorForm:content";
    String editorContent = driver.findElementById(editorContentId).getAttribute("value");
    String newEditorContent = "test: hi\n  bla: fail";
    saveScreenshot("app-test-yaml");
    webAssertThat(() -> assertThat(editorContent).isNotBlank());
    driver.executeScript("editor_app_test.setValue(\"" + StringEscapeUtils.escapeJava(newEditorContent) + "\");");
    saveScreenshot("new_content");
    webAssertThat(() -> assertThat(driver.findElementById(editorContentId).getAttribute("value")).isEqualTo(newEditorContent));
    webAssertThat(() -> assertThat(driver.findElementsByClassName("CodeMirror-lint-marker-error")).isNotEmpty());
    webAssertThat(() -> assertThat(driver.findElementById("card:saveEditorModel").isDisplayed()).isFalse());
    
    driver.findElementById("card:editorTabView:" + tabIndex + ":editorForm:saveEditor").click();
    saveScreenshot("save_dialog");
    webAssertThat(() -> assertThat(driver.findElementById("card:saveEditorModel").isDisplayed()).isTrue());
    
    driver.findElementById("card:saveEditorForm:cancelChangesBtn").click();
    saveScreenshot("save_cancel");
    webAssertThat(() -> assertThat(driver.findElementById("card:saveEditorModel").isDisplayed()).isFalse());
    
    driver.executeScript("editor_app_test.setValue(\"" + StringEscapeUtils.escapeJava(editorContent) + "\");");
    driver.executeScript("editor_app_test.performLint();");
    saveScreenshot("clear_content");
    webAssertThat(() -> assertThat(driver.findElementById(editorContentId).getAttribute("value")).isNotBlank());
    webAssertThat(() -> assertThat(driver.findElementsByClassName("CodeMirror-lint-marker-error")).isEmpty());
    
    webAssertThat(() -> assertThat(driver.findElementById("card:editorMessage_container").getText()).isBlank());
    driver.findElementById("card:editorTabView:" + tabIndex + ":editorForm:saveEditor").click();
    saveScreenshot("save");
    webAssertThat(() -> assertThat(driver.findElementById("card:editorMessage_container").getText())
            .isEqualTo("Saved app-test.yaml Successfully"));
  }
  
  private void disableDevMode()
  {
    toggleDevMode();
    elementNotAvailable(driver, By.id("menuform:sr_editor"));
  }

  private void enableDevMode()
  {
    toggleDevMode();
    webAssertThat(() -> assertThat(driver.findElementById("menuform:sr_system").isDisplayed()).isTrue());
    if (!driver.findElementById("menuform:sr_system").getAttribute("class").contains("active-menuitem"))
    {
      driver.findElementById("menuform:sr_system").click();
    }
    webAssertThat(() -> assertThat(driver.findElementById("menuform:sr_editor").isDisplayed()).isTrue());
  }

  private void toggleDevMode()
  {
    driver.findElementByXPath("//*[@id='sessionUser']/a").click();
    webAssertThat(() -> assertThat(driver.findElementById("devModeBtn").isDisplayed()).isTrue());
    driver.findElementById("devModeBtn").click();
  }

  private void toEditor()
  {
    login();
    driver.manage().deleteCookie(new Cookie(DEV_MODE_COOKIE, "false"));
    enableDevMode();
    Navigation.toEditor(driver);
    saveScreenshot("editor");
  }
  
}
