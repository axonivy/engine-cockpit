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
import java.nio.file.Path;
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
public class WebTestLicence
{

  private static final String SESSION_USER = "foo";

  @BeforeEach
  void beforeEach()
  {
    login();
    Navigation.toLicence();
    $("#selectedFileOutput").shouldHave(text(".lic"));
    $("#uploadStatus").shouldBe(empty);
  }

  @Test
  public void testLicenceUploadInvalidFileEnding() throws IOException
  {
    Path createTempFile = Files.createTempFile("licence", ".txt");
    $("#fileInput").sendKeys(createTempFile.toString());
    $("#uploadStatus").shouldBe(empty);
  }

  @Test
  public void testLicenceUploadInvalidLicence() throws IOException
  {
    Path createTempFile = Files.createTempFile("licence", ".lic");
    $("#fileInput").sendKeys(createTempFile.toString());
    $("#uploadLog").shouldBe(exactText("Licence file has a wrong format. It must have at least 6 lines"));
  }

  @Test
  void liveStats()
  {
    EngineCockpitUtil.assertLiveStats(List.of("Sessions"));
  }

  @Test
  void killSession()
  {
    openAnotherSession();
    $("#layout-config-button").shouldBe(visible).click();
    var table = new Table(By.cssSelector("#layout-config .ui-datatable"));
    table.firstColumnShouldBe(textsInAnyOrder("admin", SESSION_USER));
    table.clickButtonForEntry(SESSION_USER, "killSession");
    table.firstColumnShouldBe(textsInAnyOrder("admin"));
    assertOtherSession();
  }

  private void openAnotherSession()
  {
    $("#breadcrumbOptions a").shouldBe(visible).click();
    Selenide.switchTo().window(1);
    Selenide.open(EngineUrl.create().app("test").path("login").toUrl());
    $("h1").shouldHave(text("Login"));
    $("#loginForm\\:username").sendKeys(SESSION_USER);
    $("#loginForm\\:password").sendKeys(SESSION_USER);
    $("#loginForm\\:login").click();
    $("#sessionUserName").shouldHave(text(SESSION_USER));
    Selenide.switchTo().window(0);
  }

  private void assertOtherSession()
  {
    Selenide.switchTo().window(1);
    Selenide.refresh();
    $("#sessionUserName").shouldHave(text("Unknown User"));
    Selenide.switchTo().window(0);
  }

}
