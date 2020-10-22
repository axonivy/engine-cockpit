package ch.ivyteam.enginecockpit;


import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.executeJs;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.viewUrl;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.text.StringEscapeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver.Options;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.WebDriverRunner;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

@IvyWebTest
public class WebTestEditor
{
  
  private static final String DEV_MODE_COOKIE = "advanced";
  
  @BeforeEach
  void beforeEach()
  {
    login();
    driverOptions().deleteCookie(new Cookie(DEV_MODE_COOKIE, "false"));
    enableDevMode();
    Navigation.toEditor();
  }

  @Test
  void testDevMode()
  {
    assertThat(driverOptions().getCookieNamed(DEV_MODE_COOKIE).getValue()).isEqualTo("true");
    disableDevMode();
    assertThat(driverOptions().getCookieNamed(DEV_MODE_COOKIE).getValue()).isEqualTo("false");
  }
  
  @Test
  void testEditor()
  {
    assertThat(Tab.getCount()).isGreaterThan(1);
    String ivyYamlHints = $(yamlHintsSelector()).shouldNotBe(empty).getAttribute("value");
    $(editorContentSelector()).shouldNotHave(attribute("value", ""));
    Tab.switchToTab("app-test-ad.yaml");
    String appYamlHints = $(yamlHintsSelector()).shouldNotBe(empty).getAttribute("value");
    assertThat(ivyYamlHints).isNotEqualTo(appYamlHints);
    $(editorContentSelector()).shouldHave(value("SecuritySystem: test-ad"));
  }
  
  @Test
  void testEditorSaveErrorsDialog()
  {
    Tab.switchToTab("app-test.yaml");
    String newEditorContent = "test: hi\n  bla: fail";
    String editorContent = $(editorContentSelector()).shouldNotBe(empty).getAttribute("value");
    
    executeJs("editor_app_test.setValue(\"" + StringEscapeUtils.escapeJava(newEditorContent) + "\");");
    $(editorContentSelector()).shouldBe(exactValue(newEditorContent));
    $$(".CodeMirror-lint-marker-error").shouldBe(sizeGreaterThan(0));
    $("#card\\:saveEditorModel").shouldNotBe(visible);
    
    $(getActivePanelCss() + "editorForm\\:saveEditor").click();
    $("#card\\:saveEditorModel").shouldBe(visible);
    
    $("#card\\:saveEditorForm\\:cancelChangesBtn").click();
    $("#card\\:saveEditorModel").shouldNotBe(visible);
    
    executeJs("editor_app_test.setValue(\"" + StringEscapeUtils.escapeJava(editorContent) + "\");");
    executeJs("editor_app_test.performLint();");
    $(editorContentSelector()).shouldNotBe(empty);
    $$(".CodeMirror-lint-marker-error").shouldBe(CollectionCondition.empty);
    
    $("#card\\:editorMessage_container").shouldBe(empty);
    $(getActivePanelCss() + "editorForm\\:saveEditor").click();
    $("#card\\:editorMessage_container").shouldBe(exactText("Saved app-test.yaml Successfully"));
  }
  
  @Test
  void directFileOpenUrl()
  {
    open(viewUrl("editor.xhtml?file=app-test.yaml"));
    assertThat(Tab.getSelectedTab()).contains("app-test.yaml");
    $(editorContentSelector()).shouldHave(value("BusinessCalendars:"));
  }
  
  private void disableDevMode()
  {
    toggleDevMode();
    $("#menuform\\:sr_editor").shouldNotBe(exist);
  }

  private void enableDevMode()
  {
    toggleDevMode();
    $("#menuform\\:sr_system").shouldBe(visible);
    if (!$("#menuform\\:sr_system").getAttribute("class").contains("active-menuitem"))
    {
      $("#menuform\\:sr_system").click();
    }
    $("#menuform\\:sr_editor").shouldBe(visible);
  }

  private void toggleDevMode()
  {
    $("#sessionUser > a").click();
    $("#devModeBtn").shouldBe(visible).click();
  }

  private String yamlHintsSelector()
  {
    return getActivePanelCss() + "editorForm\\:yamlhints";
  }
  
  private String editorContentSelector()
  {
    return getActivePanelCss() + "editorForm\\:content";
  }
  
  private String getActivePanelCss()
  {
    return "#card\\:editorTabView\\:" + Tab.getSelectedTabIndex() + "\\:";
  }
  
  private Options driverOptions()
  {
    return WebDriverRunner.getWebDriver().manage();
  }

}
