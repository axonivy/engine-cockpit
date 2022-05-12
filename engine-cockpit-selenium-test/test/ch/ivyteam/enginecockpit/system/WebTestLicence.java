package ch.ivyteam.enginecockpit.system;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.engine.EngineUrl;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestLicence {

  private static final String SESSION_USER = "foo";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toLicence();
    $("#selectedFileOutput").shouldHave(text(".lic"));
    $("#uploadStatus").shouldBe(empty);
  }

  @Test
  void licenceUploadInvalidFileEnding() throws IOException {
    var createTempFile = Files.createTempFile("licence", ".txt");
    $("#fileInput").sendKeys(createTempFile.toString());
    $("#uploadStatus").shouldBe(empty);
  }

  @Test
  void licenceUploadInvalidLicence() throws IOException {
    var createTempFile = Files.createTempFile("licence", ".lic");
    $("#fileInput").sendKeys(createTempFile.toString());
    $("#uploadLog").shouldBe(exactText("Licence file has a wrong format. It must have at least 6 lines"));
  }

  @Test
  void liveStats() {
    EngineCockpitUtil.assertLiveStats(List.of("Sessions"));
  }

  @Test
  void killSession() {
    openAnotherSession();
    $("#layout-config-button").shouldBe(visible).click();
    var table = new Table(By.cssSelector("#layout-config .ui-datatable"));
    var adminUser = EngineUrl.isDesigner() ? "Developer" : "admin";
    table.firstColumnShouldBe(textsInAnyOrder(adminUser, SESSION_USER));
    table.clickButtonForEntry(SESSION_USER, "killSession");
    table.firstColumnShouldBe(textsInAnyOrder(adminUser));
    assertOtherSession();
  }

  private void openAnotherSession() {
    $(".layout-topbar-actions .help-link a").shouldBe(visible).click();
    Selenide.switchTo().window(1);
    if (EngineUrl.isDesigner()) {
      Selenide.open(EngineUrl.create().app(EngineUrl.DESIGNER).path("faces/login.xhtml").toUrl());
    } else {
      Selenide.open(EngineUrl.create().app("test").path("login").toUrl());
    }
    $("h1").shouldHave(text("Login"));
    $("#loginForm\\:userName").sendKeys(SESSION_USER);
    $("#loginForm\\:password").sendKeys(SESSION_USER);
    $("#loginForm\\:login").click();
    $("#sessionUserName").shouldHave(text(SESSION_USER));
    Selenide.switchTo().window(0);
    Selenide.refresh();
  }

  private void assertOtherSession() {
    Selenide.switchTo().window(1);
    Selenide.refresh();
    $("#sessionUserName").shouldHave(text("Unknown User"));
    Selenide.switchTo().window(0);
  }
}
