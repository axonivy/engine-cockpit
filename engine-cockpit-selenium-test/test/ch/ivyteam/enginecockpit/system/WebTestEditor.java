package ch.ivyteam.enginecockpit.system;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.executeJs;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.viewUrl;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.text.StringEscapeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.CollectionCondition;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

@IvyWebTest
public class WebTestEditor {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toEditor();
  }

  @Test
  void testEditor() {
    assertThat(Tab.APP.getCount()).isGreaterThan(1);
    String ivyYamlHints = $(yamlHintsSelector()).shouldNotBe(empty).getAttribute("value");
    $(editorContentSelector()).shouldNotHave(attribute("value", ""));
    Tab.APP.switchToTab("test-ad/app.yaml");
    String appYamlHints = $(yamlHintsSelector()).shouldNotBe(empty).getAttribute("value");
    assertThat(ivyYamlHints).isNotEqualTo(appYamlHints);
    $(editorContentSelector()).shouldHave(value("SecuritySystem: test-ad"));
  }

  @Test
  void testEditorSaveErrorsDialog() {
    Tab.APP.switchToTab("test/app.yaml");
    String newEditorContent = "test: hi\n  bla: fail";
    String editorContent = $(editorContentSelector()).shouldNotBe(empty).getAttribute("value");

    executeJs("editor_test_app.setValue(\"" + StringEscapeUtils.escapeJava(newEditorContent) + "\");");
    $(editorContentSelector()).shouldBe(exactValue(newEditorContent));
    $$(".CodeMirror-lint-marker-error").shouldBe(sizeGreaterThan(0));
    $("#card\\:saveEditorModel").shouldNotBe(visible);

    $(getActivePanelCss() + "editorForm\\:saveEditor").click();
    $("#card\\:saveEditorModel").shouldBe(visible);

    $("#card\\:saveEditorForm\\:cancelChangesBtn").click();
    $("#card\\:saveEditorModel").shouldNotBe(visible);

    executeJs("editor_test_app.setValue(\"" + StringEscapeUtils.escapeJava(editorContent) + "\");");
    executeJs("editor_test_app.performLint();");
    $(editorContentSelector()).shouldNotBe(empty);
    $$(".CodeMirror-lint-marker-error").shouldBe(CollectionCondition.empty);

    $("#card\\:editorMessage_container").shouldBe(empty);
    $(getActivePanelCss() + "editorForm\\:saveEditor").click();
    $("#card\\:editorMessage_container .ui-growl-message").shouldBe(exactText("Saved test/app.yaml Successfully"));
  }

  @Test
  void directFileOpenUrl() {
    open(viewUrl("editor.xhtml?file=test/app.yaml"));
    assertThat(Tab.APP.getSelectedTab()).contains("test/app.yaml");
    $(editorContentSelector()).shouldHave(value("BusinessCalendars:"));
  }

  private String yamlHintsSelector() {
    return getActivePanelCss() + "editorForm\\:yamlhints";
  }

  private String editorContentSelector() {
    return getActivePanelCss() + "editorForm\\:content";
  }

  private String getActivePanelCss() {
    return "#card\\:editorTabView\\:" + Tab.APP.getSelectedTabIndex() + "\\:";
  }

}
