package ch.ivyteam.enginecockpit.configuration;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.executeJs;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.attributeMatching;
import static com.codeborne.selenide.Condition.cssValue;
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
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestBranding {
  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toBranding();
    Tab.APP.switchToDefault();
  }

  @Test
  void appSwitch() {
    $(By.id(getResourcesFormId())).find("img", 1).shouldBe(visible, attributeMatching("src", ".*logo.png.*"));
    openCustomCssDialog();
    $(By.id("editCustomCssForm:editCustomCssValue")).shouldHave(value(":root {"));
    $(By.id("cancelCustomCss")).shouldBe(visible).click();
    new Table(By.id(getColorTableId())).tableEntry("--ivy-primary-color", 2).shouldHave(text("hsl(64, 70%, 49%)"));

    Tab.APP.switchToTab("test-ad");
    $(By.id(getResourcesFormId())).find("img", 1).shouldBe(visible, attributeMatching("src", ".*logo.svg.*"));
    openCustomCssDialog();
    $(By.id("editCustomCssForm:editCustomCssValue")).shouldBe(exactValue(""));
    new Table(By.id(getColorTableId())).tableEntry("--ivy-primary-color", 2).shouldHave(text("hsl(195, 100%, 29%)"));
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
  void uploadLogoLight() throws IOException {
    Tab.APP.switchToTab("demo-portal");
    uploadAndAssertImage(2, "blalba", ".jpg", "logo_light.jpg", "logo_light.svg");
  }

  @Test
  void uploadFavicon() throws IOException {
    Tab.APP.switchToTab("test-ad");
    uploadAndAssertImage(0, "icon", ".webp", "favicon.webp", "favicon.png");
  }

  @Test
  void editCustomCss() {
    Tab.APP.switchToTab("demo-portal");
    openCustomCssDialog();
    executeJs("$('#editCustomCssForm > textarea').val('hallo123')");
    executeJs("refreshCodeMirror();");
    $(By.id("saveCustomCss")).shouldBe(visible).click();
    $(By.id("msgs_container")).shouldBe(visible, text("Successfully saved 'custom.css'"));

    Selenide.refresh();
    openCustomCssDialog();
    $(By.id("editCustomCssForm:editCustomCssValue")).shouldBe(exactValue("hallo123"));
    $(By.id("cancelCustomCss")).shouldBe(visible).click();
    resetCustomCss();
    $(By.id("msgs_container")).shouldBe(visible, text("Successfully reset 'custom.css'"));

    openCustomCssDialog();
    $(By.id("editCustomCssForm:editCustomCssValue")).shouldBe(exactValue(""));
  }

  @Test
  void filterColors() {
    var colorTable = new Table(By.id(getColorTableId()));
    colorTable.search("primary-dark");
    colorTable.firstColumnShouldBe(size(2));
    colorTable.tableEntry("--ivy-primary-dark-color", 2).shouldHave(text("hsl(64, 70%, 39%)"));
    colorTable.tableEntry("--ivy-primary-dark-color", 2).find(".color-preview").shouldHave(cssValue("background-color", "rgb(160, 169, 30)"));

    Tab.APP.switchToTab("test-ad");
    colorTable = new Table(By.id(getColorTableId()));
    colorTable.firstColumnShouldBe(sizeGreaterThan(40));
    colorTable.tableEntry("--ivy-primary-dark-color", 2).shouldHave(text("hsl(195, 100%, 24%)"));
    colorTable.tableEntry("--ivy-primary-dark-color", 2).find(".color-preview").shouldHave(cssValue("background-color", "rgb(0, 92, 122)"));
  }

  @Test
  void setAndResetColor() {
    var colorTable = new Table(By.id(getColorTableId()));
    colorTable.clickButtonForEntry("--accent-color", "setColor");
    $(By.id("cssColorPickerForm:editCssColorModal_title")).shouldBe(visible, exactText("Edit color --accent-color"));
    $(By.id("cssColorPickerForm:cssColorPickerInput")).clear();
    $(By.id("cssColorPickerForm:cssColorPickerInput")).sendKeys("#123456");
    $(By.id("cssColorPickerForm:saveCssColor")).click();
    $(By.id("msgs_container")).shouldBe(visible, text("Successfully update color '--accent-color"));
    colorTable.tableEntry("--accent-color", 2).shouldHave(text("#123456"));
    colorTable.tableEntry("--accent-color", 2).find(".color-preview").shouldHave(cssValue("background-color", "rgb(18, 52, 86)"));

    colorTable.clickButtonForEntry("--accent-color", "resetColor");
    $(By.id("resetColorConfirmDialog_content")).shouldBe(visible, text("'--accent-color'"));
    $(By.id("resetColorForm:resetColorConfirmYesBtn")).click();
    $(By.id("msgs_container")).shouldBe(visible, text("Successfully update color '--accent-color"));
    colorTable.tableEntry("--accent-color", 2).shouldHave(text("hsl(0, 1%, 34%)"));
    colorTable.tableEntry("--accent-color", 2).find(".color-preview").shouldHave(cssValue("background-color", "rgb(88, 86, 86)"));
  }

  @Test
  void colorPicker() {
    var colorTable = new Table(By.id(getColorTableId()));
    colorTable.clickButtonForEntry("--ivy-primary-text-color", "setColor");
    $(By.id("cssColorPickerForm:cssColorPickerInput")).shouldBe(exactValue("hsl(0, 0%, 100%)"));
    $(By.id("cssColorPickerForm:cssColorPickerBtn")).shouldBe(exactValue("#ffffff"));

    $(By.id("cssColorPickerForm:cssColorPickerInput")).clear();
    $(By.id("cssColorPickerForm:cssColorPickerInput")).sendKeys("hsl(150, 60%, 50%)");
    $(By.id("cssColorPickerForm:cssColorPickerBtn")).shouldBe(exactValue("#33cc80"));

    Selenide.executeJavaScript("document.getElementById('cssColorPickerForm\\:cssColorPickerBtn').value = '#654321';");
    Selenide.executeJavaScript("document.getElementById('cssColorPickerForm\\:cssColorPickerBtn').dispatchEvent(new Event('input'));");
    $(By.id("cssColorPickerForm:cssColorPickerInput")).shouldBe(exactValue("hsl(30,50.7%,26.3%)"));
  }

  private void uploadAndAssertImage(int index, String tempFileName, String tempFileExt, String expectedImg, String defaultImg) throws IOException {
    uploadImage(index);
    Path createTempFile = Files.createTempFile(tempFileName, tempFileExt);
    $(By.id("fileInput")).sendKeys(createTempFile.toString());
    $(By.id("uploadModal:uploadBtn")).click();
    $("#uploadLog").shouldHave(exactText("Successfully uploaded " + expectedImg));
    $(By.id("uploadModal:closeDeploymentBtn")).shouldBe(visible).click();

    $(By.id(getResourcesFormId())).find("img", index).shouldBe(visible, attributeMatching("src", ".*" + expectedImg + ".*"));
    resetImage(index);
    $(By.id(getResourcesFormId())).find("img", index).shouldBe(visible, attributeMatching("src", ".*" + defaultImg + ".*"));
  }

  private void uploadImage(int index) {
    var baseId = getResourcesFormId() + ":images:" + index + ":";
    $(By.id(baseId + "uploadBtn")).shouldBe(visible).click();
    $(By.id("uploadModal:fileUploadModal")).shouldBe(visible);
    $(By.id("uploadError")).shouldBe(empty);
    $(By.id("uploadModal:fileUploadModal_title")).shouldHave(text(Tab.APP.getSelectedTab()));
  }

  private void resetImage(int index) {
    var baseId = getResourcesFormId() + ":images:" + index + ":";
    $(By.id(baseId + "uploadBtn_menuButton")).shouldBe(visible).click();
    $(By.id(baseId + "resetBtn")).shouldBe(visible).click();
    $(By.id("resetForm:resetBrandingConfirmYesBtn")).shouldBe(visible).click();
  }

  private void openCustomCssDialog() {
    $(By.id(getResourcesFormId() + ":editCustomCssBtn")).shouldBe(visible).click();
    $(By.id("editCustomCssModal")).shouldBe(visible);
  }

  private void resetCustomCss() {
    var baseId = getResourcesFormId() + ":";
    $(By.id(baseId + "editCustomCssBtn_menuButton")).shouldBe(visible).click();
    $(By.id(baseId + "resetCustomCssBtn")).shouldBe(visible).click();
    $(By.id("resetForm:resetBrandingConfirmYesBtn")).shouldBe(visible).click();
  }

  private String getResourcesFormId() {
    return "apps:applicationTabView:" + Tab.APP.getSelectedTabIndex() + ":form";
  }

  private String getColorTableId() {
    return "apps:applicationTabView:" + Tab.APP.getSelectedTabIndex() + ":colorForm:colorsTable";
  }
}
