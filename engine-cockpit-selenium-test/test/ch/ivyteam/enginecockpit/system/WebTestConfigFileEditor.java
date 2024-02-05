package ch.ivyteam.enginecockpit.system;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.executeJs;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.viewUrl;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.commons.text.StringEscapeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Condition;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

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
    var driver = selector.getWrappedDriver();

    var original = readEditorContent();
    try {
      String ivyYaml = """
        # yaml-language-server: $schema=https://json-schema.axonivy.com/ivy/11.3.6/ivy.json
        SecuritySystems:
          test-ad:
            IdentityProvider:
              Name: microsoft-active-directory
        """;
      setMonacoValue(ivyYaml);

      driver.switchTo().frame("framedEditor");
      $(By.className("monaco-editor")).shouldBe(visible);

      var idpName = $(By.xpath("//div[@class='view-line']//span[@class='mtk22' and text()='Name']"))
        .as("selected SecuritySystems.test-ad.IdentityProvider.Name")
        .shouldBe(visible);
      idpName.hover();

      $(By.className("hover-contents")).shouldBe(visible)
        .as("config-editor shows key specific help, provided by json-schemas")
        .shouldHave(Condition.partialText("microsoft-entra-id"));
    }
    finally {
      driver.switchTo().defaultContent();
      setMonacoValue(original);
    }
  }

  @Test
  void editorSave() throws Exception {
    selectFromAutocomplete(APP_YAML);
    $(By.id("currentFile")).shouldBe(text(APP_YAML));
    var original = readEditorContent();
    try {
      String newEditorContent = """
        # yaml-language-server: $schema=https://json-schema.axonivy.com/app/0.0.1/app.json
        #test: hi
        #bla: fail
        #testEscape: 'false'""";
      setMonacoValue(newEditorContent);

      $(By.id("editorForm:cancelEditor")).click();
      $(By.id("editorMessage_container")).shouldBe(empty);
      $(By.id("editorForm:codeHolder")).shouldNotHave(Condition.partialValue("bla: fail"));

      setMonacoValue(newEditorContent);
      saveEditor();
      $("#editorMessage_container .ui-growl-message").shouldHave(text("Saved "+APP_YAML+" Successfully"));
    }
    finally {
      setMonacoValue(original);
      saveEditor();
    }
  }

  @Test
  void downloadBinary() {
    $(By.id("fileChooserForm:fileDropDown_input")).clear();
    $(By.id("fileChooserForm:fileDropDown_input")).sendKeys("truststore.p12");
    $(By.id("fileChooserForm:fileDropDown_panel")).click();
    $(By.id("uploadDownloadBinary:binaryDownload")).shouldHave(text("Download Binary file"));
  }

  @Test
  void uploadBinary() throws IOException {
    $(By.id("fileChooserForm:fileDropDown_input")).clear();
    $(By.id("fileChooserForm:fileDropDown_input")).sendKeys("truststore.p12");
    $(By.id("fileChooserForm:fileDropDown_panel")).click();
    $(By.id("uploadDownloadBinary:uploadbtn")).shouldHave(text("Upload Binary file"));
    var createTempFile = Files.createTempFile("truststore", ".p12");
    try (var is = WebTestConfigFileEditor.class.getResourceAsStream("truststore.p12")) {
      Files.copy(is, createTempFile, StandardCopyOption.REPLACE_EXISTING);
    }
    $(By.id("uploadDownloadBinary:binaryUpload_input")).sendKeys(createTempFile.toString());
    Navigation.toSSL();
    var table = new Table(By.id("sslTrustTable:storeTable:storeCertificates"));
    table.firstColumnShouldBe(texts("ivy"));
  }

  private void saveEditor() {
    $(By.id("editorForm:saveEditor")).click();
  }

  private void setMonacoValue(String content) {
    executeJs("""
      waitFor(() => monaco()).then(model => {
        model.setValue("%s");
      })
      """.formatted(StringEscapeUtils.escapeJson(content)));
  }

  private String readEditorContent() {
    return $(By.id("editorForm:codeHolder")).getAttribute("value");
  }

  @Test
  void directFileOpenUrl() {
    var file = "web.xml";
    open(viewUrl("editor.xhtml?file=web.xml"));
    $(By.id("currentFile")).shouldBe(text(file));
    $(By.id("editorForm:codeHolder")).shouldHave(value("<web-app"));
  }

  private void selectFromAutocomplete(String elementName) {
    if (elementName.equals($(By.id("currentFile")).text())) {
      return;
    }
    var fileChooserInput = $(By.id("fileChooserForm:fileDropDown_input"));
    fileChooserInput.clear();
    fileChooserInput.shouldBe(visible).sendKeys(elementName);
    var autocompleteElement = $(By.className("ui-autocomplete-item"))
      .shouldHave(Condition.attribute("data-item-label", elementName));
    autocompleteElement.click();
  }

}
