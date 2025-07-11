package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.openDashboard;
import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.text;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.DownloadOptions;

import ch.ivyteam.enginecockpit.test.FileDownloadExtension;
import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
@ExtendWith({FileDownloadExtension.class})
class WebTestDocuments {

  private static final long TIMEOUT = Duration.ofMinutes(1).toMillis();

  @BeforeEach
  void beforeEach() {
    login();
    EngineCockpitUtil.createBlob();

    openDashboard();
    Navigation.toDocuments();
    Tab.SECURITY_SYSTEM.switchToDefault();
  }

  @Test
  void blobs() {
    var blobs = table();
    blobs.tableEntry(1, 1).should(matchText(".*-.*-.*"));
    blobs.tableEntry(1, 3).should(text("24 bytes"));
    blobs.tableEntry(1, 4).should(text("text/plain"));
  }

  @Test
  void filter() {
    var blobs = table();
    blobs.search("louis.txt");
    blobs.rows().should(CollectionCondition.sizeGreaterThanOrEqual(1));
  }

  @Test
  void download() {
    var blobs = table();
    var options = DownloadOptions.file().withTimeout(TIMEOUT);
    var download = blobs.tableEntry(1, 9).download(options);
    assertThat(download.getName()).isEqualTo("louis.txt");
    assertThat(download).content().isEqualTo("louis schreibt man mit s");
  }

  private Table table() {
    return new Table(By.id("tabs:securitySystemTabView:0:form:blobsTable"), false);
  }
}
