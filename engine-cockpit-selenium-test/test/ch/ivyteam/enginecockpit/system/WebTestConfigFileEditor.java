package ch.ivyteam.enginecockpit.system;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.executeJs;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.viewUrl;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
class WebTestConfigFileEditor {

  private static final String APP_YAML = "test/app.yaml";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toEditor();
  }

  @Test
  void editor() {
    selectFromAutocomplete("ivy.yaml");
    var selector = $(By.id("currentFile")).shouldHave(text("ivy.yaml"));

    selector.getWrappedDriver().switchTo().frame("framedEditor");
    $(By.className("monaco-editor")).shouldBe(visible);

    var idpName = $(By.xpath("//div[@class='view-line']//span[@class='mtk22' and text()='Name']"))
      .shouldBe(visible);
    assertThat(idpName.text())
      .as("selected SecuritySystems.test-ad.IdentityProvider.Name")
      .isEqualTo("Name");
    idpName.hover();

    var hoverHelp = $(By.className("hover-contents")).shouldBe(visible);
    assertThat(hoverHelp.text())
      .as("config-editor shows key specific help, provided by json-schemas")
      .contains("azure-active-directory");
  }

  @Test
  void editorSave() throws Exception {
    String newEditorContent = """
      #test: hi
      #bla: fail
      #testEscape: 'false'""";
    selectFromAutocomplete(APP_YAML);
    $(By.id("currentFile")).shouldBe(text(APP_YAML));
    $(By.id("editorForm:codeHolder")).shouldHave(Condition.partialValue("app.json"));
    writeToEditor(newEditorContent);

    $(By.id("editorForm:cancelEditor")).click();
    $(By.id("editorMessage_container")).shouldBe(empty);
    $(By.id("editorForm:codeHolder")).shouldNotHave(Condition.partialValue("bla: fail"));

    writeToEditor(newEditorContent);
    $(By.id("editorForm:saveEditor")).click();
    $("#editorMessage_container .ui-growl-message").shouldHave(text("Saved "+APP_YAML+" Successfully"));
  }

  private void writeToEditor(String newContent) {
    String editorContent = $(By.id("editorForm:codeHolder")).getAttribute("value");
    var escapedContent = StringEscapeUtils.escapeJson(editorContent + newContent);
    executeJs("""
      waitFor(() => monaco()).then(model => {
        model.setValue("%s");
      })
      """.formatted(escapedContent));
  }

  @Test
  void directFileOpenUrl() {
    open(viewUrl("editor.xhtml?file="+APP_YAML));
    $(By.id("currentFile")).shouldBe(text(APP_YAML));
    $(By.id("editorForm:codeHolder")).shouldHave(value("BusinessCalendars:"));
  }

  private void selectFromAutocomplete(String elementName) {
    var fileChooserInput = $(By.id("fileChooserForm:fileDropDown_input"));
    fileChooserInput.clear();
    fileChooserInput.shouldBe(visible).sendKeys(elementName);
    Selenide.sleep(1000);
    var autocompleteElement = $(By.className("ui-autocomplete-item"));
    assertThat(autocompleteElement.getAttribute("data-item-label")).isEqualTo(elementName);
    autocompleteElement.click();
  }
}
