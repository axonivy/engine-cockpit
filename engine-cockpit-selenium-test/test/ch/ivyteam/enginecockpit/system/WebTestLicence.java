package ch.ivyteam.enginecockpit.system;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.texts;
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

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestLicence
{
  
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
    $("#layout-config-button").shouldBe(visible).click();
    var table = new Table(By.cssSelector("#layout-config .ui-datatable"));
    var expectedSession = EngineUrl.isDesigner() ? "Developer" : "admin";
    table.firstColumnShouldBe(texts(expectedSession));
    table.clickButtonForEntry(expectedSession, "killSession");
    $("h1").shouldHave(text("Engine Cockpit"));
  }
  
}
