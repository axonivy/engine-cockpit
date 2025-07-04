package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.CollectionCondition.sizeLessThan;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.Wait;
import static com.codeborne.selenide.Selenide.refresh;
import static org.openqa.selenium.By.id;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.junit5.ScreenShooterExtension;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
@ExtendWith(ScreenShooterExtension.class)
public class WebTestThreads {

  private static final Duration TWENTY_SECONDS = Duration.ofSeconds(20);

  private Table threadsTable;

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toThreads();
    threadsTable = new Table(By.id("form:threadTable"));
    Wait()
        .withTimeout(TWENTY_SECONDS)
        .ignoring(AssertionError.class)
        .until(webDriver -> {
          refresh();
          threadsTable.rows().shouldBe(sizeGreaterThan(20));
          return true;
        });
  }

  @Test
  void refreshButton() {
    String oldTableContent = threadsTable.body().text();

    Wait()
        .withTimeout(TWENTY_SECONDS)
        .ignoring(AssertionError.class)
        .until(webDriver -> {
          $(id("form:refresh"))
              .shouldBe(visible, enabled)
              .click();
          threadsTable.body().shouldNotBe(text(oldTableContent));
          return true;
        });
  }

  @Test
  void filter() {
    int all = threadsTable.rows().size();
    threadsTable.searchFilterShould(visible, enabled);
    threadsTable.search("http");
    threadsTable.rows().shouldBe(sizeLessThan(all));
  }

  @Test
  void deadlock() {
    threadsTable.body().$$(By.className("deadlocked")).shouldBe(size(0));

    EngineCockpitUtil.deadlock();
    EngineCockpitUtil.openDashboard();
    Navigation.toThreads();

    Wait().withTimeout(TWENTY_SECONDS)
        .ignoring(AssertionError.class)
        .until(webDriver -> {
          $(id("form:refresh"))
              .shouldBe(visible, enabled)
              .click();
          threadsTable.body().$$(By.className("deadlocked")).shouldBe(size(4));
          return true;
        });
  }

  @Test
  void openDetails() {
    threadsTable.tableEntry(1, 1).shouldBe(visible, enabled).click();
    $("#threadDialog").shouldBe(visible);
    $("#thread\\:close").shouldBe(visible, enabled).click();
    $("#threadDialog").shouldNotBe(visible);
  }

  @Test
  void copyStacktrace() {
    threadsTable.tableEntry(1, 1).shouldBe(visible, enabled).click();
    $("#threadDialog").shouldBe(visible);
    var stackTrace = $("#thread\\:stackTrace").text();
    $("#thread\\:copyStack").shouldBe(visible, enabled).click();
    Selenide.confirm(stackTrace);
  }
}
