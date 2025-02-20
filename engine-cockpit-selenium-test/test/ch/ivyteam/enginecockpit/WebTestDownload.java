package ch.ivyteam.enginecockpit;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.By.id;

import java.io.File;
import java.time.Duration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.DownloadOptions;
import com.codeborne.selenide.files.FileFilters;
import com.codeborne.selenide.junit5.ScreenShooterExtension;

import ch.ivyteam.enginecockpit.test.FileDownloadExtension;
import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

@IvyWebTest
@ExtendWith({ScreenShooterExtension.class, FileDownloadExtension.class})
class WebTestDownload {

  private static final long TIMEOUT = Duration.ofMinutes(1).toMillis();

  @BeforeAll
  static void setup() {
    EngineCockpitUtil.login();
  }

  @Test
  void errorReport() {
    $(".user-profile > a").shouldBe(visible).click();
    $("#supportReport").shouldBe(visible).click();
    $("#supportReportModal").shouldBe(visible);
    var options = DownloadOptions.file().withTimeout(TIMEOUT).withFilter(FileFilters.withName("support-engine-report.zip"));
    File download = $("#reportForm\\:download").shouldBe(visible).download(options);
    assertThat(download.getName()).isEqualTo("support-engine-report.zip");
    assertThat(download.length() / 1024).isGreaterThan(10);
    $("#reportForm\\:cancel").shouldBe(visible).click();
    $("#supportReportModal").shouldNotBe(visible);
  }

  @Test
  void securityReport() {
    Navigation.toSecuritySystemDetail("default");
    $(By.id("securitySystemConfigForm:downloadSecurityReport")).shouldBe(visible).click();
    $(By.id("securityReportDownloadDialog:securityReportDownloadModal")).shouldBe(visible);
    $(By.id("securityReportDownloadDialog:downloadForm:generateButton")).shouldBe(visible).click();
    var options = DownloadOptions.file().withTimeout(TIMEOUT).withFilter(FileFilters.withName("AxonIvySecurityReport.xlsx"));
    var download = $(By.id("securityReportDownloadDialog:downloadForm:downloadButton")).shouldBe(visible).download(options);
    assertThat(download.getName()).isEqualTo("AxonIvySecurityReport.xlsx");
    assertThat(download.length() / 1024).isGreaterThanOrEqualTo(2);
  }

  @Test
  void allLogs() {
    Navigation.toLogs();
    $(By.id("downloadAllLogs")).shouldBe(visible).click();
    $(By.id("downloadDialog:downloadModal")).shouldBe(visible);
    var options = DownloadOptions.file().withTimeout(TIMEOUT).withFilter(FileFilters.withName("logs.zip"));
    var download = $(By.id("downloadDialog:downloadForm:downloadBtn")).shouldBe(visible).download(options);
    assertThat(download.getName()).isEqualTo("logs.zip");
    assertThat(download.length() / 1024).isGreaterThanOrEqualTo(2);
    $(By.id("downloadDialog:downloadForm:cancel")).shouldBe(visible).click();
    $(By.id("downloadDialog:downloadModal")).shouldNotBe(visible);
  }

  @Test
  void log() {
    Navigation.toLogs();
    var options = DownloadOptions.file().withTimeout(TIMEOUT).withFilter(FileFilters.withName("ivy.log"));
    var download = $(By.id("logView:fileForm:downloadLog"))
        .shouldBe(visible)
        .download(options);
    assertThat(download.getName()).isEqualTo("ivy.log");
    assertThat(download).isNotEmpty();
  }

  @Test
  void allBrandingResourcesOfApp() {
    Navigation.toBranding();
    Tab.APP.switchToTab("test");
    var appName = Tab.APP.getSelectedTab();
    $(By.id("downloadAllResources")).shouldBe(visible).click();
    $(By.id("downloadDialog:downloadModal")).shouldBe(visible)
        .shouldHave(text("Download Branding resources of '" + appName + "'"));
    var options = DownloadOptions.file().withTimeout(TIMEOUT).withFilter(FileFilters.withName("branding-" + appName + ".zip"));
    var download = $(By.id("downloadDialog:downloadForm:downloadBtn")).shouldBe(visible).download(options);
    assertThat(download.getName()).isEqualTo("branding-" + appName + ".zip");
    assertThat(download.length() / 1024).isGreaterThanOrEqualTo(2);
    $(By.id("downloadDialog:downloadForm:cancel")).shouldBe(visible).click();
    $(By.id("downloadDialog:downloadModal")).shouldNotBe(visible);

    Tab.APP.switchToTab("test-ad");
    $(By.id("downloadAllResources")).shouldBe(visible).click();
    $(By.id("downloadDialog:downloadModal")).shouldBe(visible)
        .shouldHave(text("Download Branding resources of 'test-ad'"));
    options = DownloadOptions.file().withTimeout(TIMEOUT);
    $(By.id("downloadDialog:downloadForm:downloadBtn")).shouldBe(visible).download(options);
    $(By.id("msgs_container")).shouldHave(text("No branding resources found for app 'test-ad'"));
  }

  @Test
  void threadsDump() {
    Navigation.toThreads();
    var options = DownloadOptions.file().withTimeout(TIMEOUT).withFilter(FileFilters.withName("ThreadDump.txt"));
    var dump = $(id("form:dump"))
        .shouldBe(visible, enabled)
        .download(options);
    assertThat(dump.toPath()).content().contains("Full thread dump OpenJDK 64-Bit Server VM");
  }

  @Test
  void classHistogramDumpMemory() {
    Navigation.toClassHistogram();
    $(id("form:dump"))
        .shouldBe(visible, enabled)
        .click();
    $(By.id("dumpMemoryDialog")).shouldBe(visible);

    var options = DownloadOptions.file().withTimeout(TIMEOUT).withFilter(FileFilters.withExtension("zip"));
    var dump = $(id("dumpMemory:dump"))
        .shouldBe(visible, enabled)
        .download(options);
    assertThat(dump.toPath()).content().hasSizeGreaterThan(0);
  }
}
