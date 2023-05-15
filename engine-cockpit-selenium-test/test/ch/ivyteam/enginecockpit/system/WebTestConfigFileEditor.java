package ch.ivyteam.enginecockpit.system;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.viewUrl;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
class WebTestConfigFileEditor {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toEditor();
  }

  @Test
  void editor() {
    selectFromAutocomplete("ivy.yaml");
    $(By.id("currentFile")).shouldHave(text("ivy.yaml"));
    $(By.className("CodeMirror-lines")).click();
    $(By.tagName("body")).sendKeys(Keys.CONTROL, Keys.SPACE);
    String ivyYamlHints = $(By.className("CodeMirror-hints")).shouldBe(visible).getText();

    selectFromAutocomplete("demo-portal/app.yaml");
    $(By.id("currentFile")).shouldHave(text("demo-portal/app.yaml"));
    $(By.className("CodeMirror-lines")).click();
    $(By.tagName("body")).sendKeys(Keys.CONTROL, Keys.SPACE);
    String appYamlHints = $(By.className("CodeMirror-hints")).shouldBe(visible).getText();
    assertThat(ivyYamlHints).isNotEqualTo(appYamlHints);
  }

  @Test
  void editorSave() {
    selectFromAutocomplete("test/app.yaml");
    $(By.id("currentFile")).shouldBe(text("test/app.yaml"));
    $(By.className("CodeMirror-lines")).click();
    $(By.tagName("body")).sendKeys("test: hi\n  bla: fail");

    $(By.id("editorForm:cancelEditor")).click();
    $("#editorMessage_container").shouldBe(empty);

    $(By.className("CodeMirror-lines")).click();
    $(By.tagName("body")).sendKeys("test: hi\n  bla: fail");
    $(By.id("editorForm:saveEditor")).click();
    $("#editorMessage_container .ui-growl-message").shouldHave(text("Saved test/app.yaml Successfully"));
  }

  @Test
  void directFileOpenUrl() {
    open(viewUrl("editor.xhtml?file=test/app.yaml"));
    $(By.id("currentFile")).shouldBe(text("test/app.yaml"));
    $(By.id("editorForm:codeMirror")).shouldHave(value("BusinessCalendars:"));
  }

  private void selectFromAutocomplete(String elementName) {
    var fileChooserInput = $(By.id("fileChooserForm:fileDropDown_input"));
    fileChooserInput.clear();
    fileChooserInput.shouldBe(visible).sendKeys(elementName);
    Selenide.sleep(1000);
    var autocompleteElement = $(By.className("ui-autocomplete-list-item"));
    assertThat(autocompleteElement.getAttribute("data-item-label")).isEqualTo(elementName);
    autocompleteElement.click();
  }
}
