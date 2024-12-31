package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.Wait;
import static org.openqa.selenium.By.id;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.junit5.ScreenShooterExtension;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
@ExtendWith(ScreenShooterExtension.class)
public class WebTestClassHistogram {

  private static final Duration TWENTY_SECONDS = Duration.ofSeconds(20);

  private Table classTable;

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toClassHistogram();
    classTable = new Table(By.id("form:classTable"));
  }

  @Test
  void view() {
    if (!classTable.body().text().contains("Press refresh")) {
      clear();
      Navigation.toClassHistogram();
    }
    classTable.body().shouldHave(text("Press refresh to load class histogram. Note that this triggers a full garbage collection and can influence the performance of the engine"));
  }

  @Test
  void refreshButton() {
    refresh();

    String oldTableContent = classTable.body().text();

    Wait()
        .withTimeout(TWENTY_SECONDS)
        .ignoring(AssertionError.class)
        .until(webDriver -> {
          refresh();
          classTable.body().shouldNotBe(text(oldTableContent));
          return true;
        });
  }

  private void refresh() {
    $(id("form:refresh"))
        .shouldBe(visible, enabled)
        .click();
  }

  @Test
  void filter() {
    refresh();
    classTable.rows().shouldBe(CollectionCondition.sizeGreaterThan(1));
    classTable.searchFilterShould(visible, enabled);
    classTable.search("ch.ivyteam.ivy.Advisor");
    classTable.rows().shouldBe(CollectionCondition.size(1));
    classTable.tableEntry(1, 3).shouldBe(text("1"));
    classTable.search("");
  }

  @Test
  void clearButton() {
    refresh();
    classTable.rows().shouldBe(CollectionCondition.sizeGreaterThan(1));
    clear();
    classTable.body().shouldHave(text("Press refresh to load class histogram. Note that this triggers a full garbage collection and can influence the performance of the engine"));
  }

  private void clear() {
    $(id("form:clear"))
        .shouldBe(visible, enabled)
        .click();
  }

  @Test
  void dumpMemory_cancel() {
    $(id("form:dump"))
        .shouldBe(visible, enabled)
        .click();

    $(By.id("dumpMemoryDialog")).shouldBe(visible);
    $(id("dumpMemory:cancel"))
        .shouldBe(visible, enabled)
        .click();
    $(By.id("dumpMemoryDialog")).shouldBe(not(visible));
  }
}
