package ch.ivyteam.enginecockpit.configuration;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.executeJs;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.attributeMatching;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

@IvyWebTest
public class WebTestBranding {
  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toBranding();
    Tab.switchToDefault();
  }

  @Test
  void appSwitch() {
    $(By.id(getFormId())).find("img", 0).shouldBe(visible, attributeMatching("src", ".*logo.png.*"));
    openCustomCssDialog();
    $(By.id("editCustomCssForm:editCustomCssValue")).shouldHave(value(":root {"));
    $(By.id("cancelCustomCss")).shouldBe(visible).click();

    Tab.switchToTab("test-ad");
    $(By.id(getFormId())).find("img", 0).shouldBe(visible, attributeMatching("src", ".*logo.svg.*"));
    openCustomCssDialog();
    $(By.id("editCustomCssForm:editCustomCssValue")).shouldBe(exactValue(""));
  }

  @Test
  void uploadNoFile() {
    uploadImage(0);
    $(By.id("uploadModal:uploadBtn")).click();
    $(By.id("uploadError")).shouldBe(exactText("Choose a valid file before upload."));
  }

  @Test
  void uploadInvalidLogo() throws IOException {
    uploadImage(0);
    Path createTempFile = Files.createTempFile("logo", ".txt");
    $(By.id("fileInput")).sendKeys(createTempFile.toString());
    $(By.id("uploadModal:uploadBtn")).click();
    $(By.id("uploadError")).shouldBe(exactText("Choose a valid file before upload."));
  }

  @Test
  void uploadLogoWhite() throws IOException {
    Tab.switchToTab("demo-portal");
    uploadAndAssertImage(1, "blalba", ".jpg", "logo_white.jpg", "logo_white.svg");
  }

  @Test
  void uploadFavicon() throws IOException {
    Tab.switchToTab("test-ad");
    uploadAndAssertImage(4, "icon", ".webp", "favicon.webp", "favicon.png");
  }

  @Test
  void editCustomCss() {
    Tab.switchToTab("demo-portal");
    openCustomCssDialog();
    executeJs("$('#editCustomCssForm > textarea').val('hallo123')");
    executeJs("refreshCodeMirror();");
    $(By.id("saveCustomCss")).shouldBe(visible).click();
    $(By.id("msgs_container")).shouldBe(visible, text("Successfully saved custom.css"));

    Selenide.refresh();
    openCustomCssDialog();
    $(By.id("editCustomCssForm:editCustomCssValue")).shouldBe(exactValue("hallo123"));
    executeJs("$('#editCustomCssForm > textarea').val('')");
    executeJs("refreshCodeMirror();");
    $(By.id("saveCustomCss")).shouldBe(visible).click();
  }

  private void uploadAndAssertImage(int index, String tempFileName, String tempFileExt, String expectedImg, String defaultImg) throws IOException {
    uploadImage(index);
    Path createTempFile = Files.createTempFile(tempFileName, tempFileExt);
    $(By.id("fileInput")).sendKeys(createTempFile.toString());
    $(By.id("uploadModal:uploadBtn")).click();
    $("#uploadLog").shouldHave(exactText("Successfully uploaded " + expectedImg));
    $(By.id("uploadModal:closeDeploymentBtn")).shouldBe(visible).click();

    $(By.id(getFormId())).find("img", index).shouldBe(visible, attributeMatching("src", ".*" + expectedImg + ".*"));
    resetImage(index);
    $(By.id(getFormId())).find("img", index).shouldBe(visible, attributeMatching("src", ".*" + defaultImg + ".*"));
  }

  private void uploadImage(int index) {
    var baseId = getFormId() + ":images:" + index + ":";
    $(By.id(baseId + "uploadBtn")).shouldBe(visible).click();
    $(By.id("uploadModal:fileUploadModal")).shouldBe(visible);
    $(By.id("uploadError")).shouldBe(empty);
    $(By.id("uploadModal:fileUploadModal_title")).shouldHave(text(Tab.getSelectedTab()));
  }

  private void resetImage(int index) {
    var baseId = getFormId() + ":images:" + index + ":";
    $(By.id(baseId + "uploadBtn_menuButton")).shouldBe(visible).click();
    $(By.id(baseId + "resetBtn")).shouldBe(visible).click();
  }

  private void openCustomCssDialog() {
    $(By.id(getFormId() + ":editCustomCssBtn")).shouldBe(visible).click();
    $(By.id("editCustomCssModal")).shouldBe(visible);
  }

  private String getFormId() {
    return "apps:applicationTabView:" + Tab.getSelectedTabIndex() + ":form";
  }
}
