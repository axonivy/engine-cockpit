package ch.ivyteam.enginecockpit;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.FileDownloadMode;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
public class WebTestDownload
{

  @BeforeAll
  static void setup()
  {
    Selenide.closeWebDriver();
    Configuration.proxyEnabled = true;
    Configuration.fileDownload = FileDownloadMode.PROXY;
    EngineCockpitUtil.login();
  }
  
  @Test
  void errorReport() throws FileNotFoundException
  {
    $("#sessionUser").shouldBe(visible).click();
    $("#engineReport").shouldBe(visible).click();
    $("#engineReportModal").shouldBe(visible);
    File download = $("#reportForm\\:download").shouldBe(visible).download();
    assertThat(download.getName()).isEqualTo("engine-report.zip");
    assertThat(download.length() / 1024).isGreaterThan(30);
  }
  
  @Test
  void allLogs() throws FileNotFoundException
  {
    Navigation.toLogs();
    $("#downloadAllLogs").shouldBe(visible).click();
    $("#logsDownloadModal").shouldBe(visible);
    File download = $("#logForm\\:allLogs").shouldBe(visible).download();
    assertThat(download.getName()).isEqualTo("logs.zip");
    assertThat(download.length() / 1024).isGreaterThan(10);
  }
  
  @Test
  void log() throws FileNotFoundException
  {
    Navigation.toLogs();
    $("#ivyLogView\\:logPanel_toggler").shouldBe(visible).click();
    File download = $("#ivyLogView\\:fileForm\\:showLog").shouldBe(visible).download();
    assertThat(download.getName()).isEqualTo("ivy.log");
    assertThat(download).isNotEmpty();
  }

}
