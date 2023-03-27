package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.By.id;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
public class WebTestThreads {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toThreads();
  }

  @Test
  void refreshButton() {
    $(id("form:refresh"))
        .shouldBe(visible, enabled)
        .click();
  }

  @Test
  void dumpButton() throws IOException {
    DownloadsFolder downloadFolder = Selenide.webdriver().driver().browserDownloadsFolder();
    downloadFolder.cleanupBeforeDownload();
    assertThat(downloadFolder.files()).isEmpty();

    $(id("form:dump"))
        .shouldBe(visible, enabled)
        .click();

    assertThat(downloadFolder.files()).hasSize(1);
    String content = Files.readString(downloadFolder.files().get(0).toPath());
    assertThat(content).contains("Full thread dump OpenJDK 64-Bit Server VM");
  }

  @Test
  void filter() {
    int all = $$(tableBody() +" tr").size();
    $(id("form:threadTable:globalFilter"))
        .shouldBe(visible, enabled)
        .sendKeys("http");
    $$(tableBody()+ " tr").shouldHave(CollectionCondition.sizeLessThan(all));
  }

  @Test
  void openDetails() {
    $(getFirstColumnElement()).shouldBe(visible, enabled).click();
    $("#threadDialog").shouldBe(visible);
    $("#thread\\:cancel").shouldBe(visible, enabled).click();
    $("#threadDialog").shouldNotBe(visible);
  }

  @Test
  void copyStacktrace() {
    $(getFirstColumnElement()).shouldBe(visible, enabled).click();
    $("#threadDialog").shouldBe(visible);
    var stackTrace = $("#thread\\:stackTrace").text();
    $("#thread\\:copyStack").shouldBe(visible, enabled).click();
    Selenide.confirm(stackTrace);
  }

  private String getFirstColumnElement() {
    return tableBody()+" tr td:first-of-type a";
  }

  private String tableBody() {
    return "#form\\:threadTable tbody";
  }
}
