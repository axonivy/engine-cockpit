package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.By.id;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Condition;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestJfr {

  private Table recordingsTable;

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toJfr();
    recordingsTable = new Table(By.id("form:recordingsTable"));
  }

  @AfterEach
  void afterEeach() {
    var entries = recordingsTable.getFirstColumnEntries();
    for (var entry : entries) {
      recordingsTable.clickButtonForEntry(entry, "close");
    }
  }

  @Test
  void start() {
    recordingsTable.rows().shouldBe(texts("No recordings found"));

    $(id("form:start")).shouldBe(visible, enabled).click();
    $(id("startRecording:start")).shouldBe(visible, enabled).click();

    recordingsTable.rows().shouldBe(texts("Axon Ivy"));
    var entry = recordingsTable.getFirstColumnEntries().get(0);
    recordingsTable.tableEntry(entry, 2).shouldBe(Condition.text("Axon Ivy"));
    recordingsTable.tableEntry(entry, 3).shouldBe(text("RUNNING"));
    recordingsTable.buttonForEntryShouldBeDisabled(entry, "stop", false);
    recordingsTable.buttonForEntryShouldBeDisabled(entry, "download", true);
    recordingsTable.buttonForEntryShouldBeDisabled(entry, "close", false);
  }

  @Test
  void stop() {
    recordingsTable.rows().shouldBe(texts("No recordings found"));

    $(id("form:start")).shouldBe(visible, enabled).click();
    $(id("startRecording:start")).shouldBe(visible, enabled).click();

    recordingsTable.rows().shouldBe(texts("Axon Ivy"));
    var entry = recordingsTable.getFirstColumnEntries().get(0);
    recordingsTable.tableEntry(entry, 3).shouldBe(text("RUNNING"));
    recordingsTable.clickButtonForEntry(entry, "stop");
    recordingsTable.tableEntry(1, 3).shouldBe(text("STOPPED"));
    recordingsTable.buttonForEntryShouldBeDisabled(entry, "stop", true);
    recordingsTable.buttonForEntryShouldBeDisabled(entry, "download", false);
    recordingsTable.buttonForEntryShouldBeDisabled(entry, "close", false);
  }

  @Test
  void close() {
    recordingsTable.rows().shouldBe(texts("No recordings found"));

    $(id("form:start")).shouldBe(visible, enabled).click();
    $(id("startRecording:start")).shouldBe(visible, enabled).click();
    recordingsTable.tableEntry(1, 3).shouldBe(text("RUNNING"));
    var entry = recordingsTable.getFirstColumnEntries().get(0);
    recordingsTable.clickButtonForEntry(entry, "close");

    recordingsTable.rows().shouldBe(texts("No recordings found"));
  }

  @Test
  void download() {
    recordingsTable.rows().shouldBe(texts("No recordings found"));

    $(id("form:start")).shouldBe(visible, enabled).click();
    $(id("startRecording:start")).shouldBe(visible, enabled).click();

    recordingsTable.rows().shouldBe(texts("Axon Ivy"));
    var entry = recordingsTable.getFirstColumnEntries().get(0);
    recordingsTable.tableEntry(entry, 3).shouldBe(text("RUNNING"));
    recordingsTable.clickButtonForEntry(entry, "stop");
    recordingsTable.clickButtonForEntry(entry, "download");
  }

  @Test
  void name() {
    recordingsTable.rows().shouldBe(texts("No recordings found"));

    $(id("form:start")).shouldBe(visible, enabled).click();
    $(id("startRecording:name")).shouldBe(visible, enabled).sendKeys("My recording");
    $(id("startRecording:start")).shouldBe(visible, enabled).click();

    recordingsTable.tableEntry(1, 2).shouldBe(text("My recording"));
    recordingsTable.tableEntry(1, 5).shouldBe(exactText("Unlimited"));
  }

  @Test
  void duration() {
    recordingsTable.rows().shouldBe(texts("No recordings found"));

    $(id("form:start")).shouldBe(visible, enabled).click();
    $(id("startRecording:more_toggler")).shouldBe(visible, enabled).click();
    $(id("startRecording:duration")).shouldBe(visible, enabled).sendKeys("10 s");
    $(id("startRecording:more_toggler")).shouldBe(visible, enabled).click();
    $(id("startRecording:start")).shouldBe(visible, enabled).click();
    recordingsTable.tableEntry(1, 5).shouldBe(not(exactText("Unlimited")));
  }

  @Test
  void add_upload_configuration() {
    recordingsTable.rows().shouldBe(texts("No recordings found"));

    $(id("form:start")).shouldBe(visible, enabled).click();
    var jfcFile = new File(System.getProperty("user.dir") + "/resources/ivy.jfc").toPath();
    $(id("startRecording:uploadConfig_input")).shouldBe(enabled).sendKeys(jfcFile.toString());

    $(id("startRecording:configuration_label")).shouldBe(visible, enabled, text("Axon Ivy"));
    $(id("startRecording:start")).shouldBe(visible, enabled).click();
    recordingsTable.rows().shouldBe(texts("Axon Ivy"));
  }

}
