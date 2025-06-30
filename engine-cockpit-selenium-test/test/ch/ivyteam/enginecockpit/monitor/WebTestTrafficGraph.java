package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.openqa.selenium.By.id;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
public class WebTestTrafficGraph {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toTrafficGraph();
  }

  @Test
  void cancelStart() {
    $(id("form:start")).click();
    $(id("startTraces:cancel")).click();
    assertStoppedEmptyButtonState();
  }

  @Test
  void trafficGraph() {
    assertStoppedEmptyButtonState();

    start();
    assertRunningEmptyButtonState();

    recordData();
    assertRunningButtonState();

    stop();
    assertStoppedButtonState();

    $$(By.className("ui-diagram-element")).shouldHave(sizeGreaterThanOrEqual(2));

    clear();
    $$(By.className("ui-diagram-element")).shouldHave(size(1));
    assertStoppedEmptyButtonState();
  }

  private void assertStoppedEmptyButtonState() {
    $(id("form:start")).shouldBe(visible, enabled);
    $(id("form:stop")).shouldBe(visible, disabled);
    $(id("form:clear")).shouldBe(visible, disabled);
    $(id("form:refresh")).shouldBe(visible, disabled);
  }

  private void assertStoppedButtonState() {
    $(id("form:start")).shouldBe(visible, enabled);
    $(id("form:stop")).shouldBe(visible, disabled);
    $(id("form:clear")).shouldBe(visible, enabled);
    $(id("form:refresh")).shouldBe(visible, disabled);
  }

  private void assertRunningEmptyButtonState() {
    $(id("form:start")).shouldBe(visible, disabled);
    $(id("form:stop")).shouldBe(visible, enabled);
    $(id("form:clear")).shouldBe(visible, disabled);
    $(id("form:refresh")).shouldBe(visible, enabled);
  }

  private void assertRunningButtonState() {
    $(id("form:start")).shouldBe(visible, disabled);
    $(id("form:stop")).shouldBe(visible, enabled);
    $(id("form:clear")).shouldBe(visible, enabled);
    $(id("form:refresh")).shouldBe(visible, enabled);
  }

  private static void recordData() {
    recordData(1);
  }

  private static void recordData(int requests) {
    while (requests-- > 0) {
      Navigation.toProcessExecution();
    }
    Navigation.toTrafficGraph();
  }

  private static void start() {
    $(id("form:start")).shouldBe(visible, enabled).click();
    $(id("startTraces:start")).shouldBe(visible, enabled).click();
  }

  private static void stop() {
    $(id("form:stop")).shouldBe(visible, enabled).click();
  }

  public static void clear() {
    $(id("form:clear")).shouldBe(visible, enabled).click();
    Navigation.toSlowRequests();
    $(id("form:clear")).shouldBe(visible, enabled).click();
    Navigation.toTrafficGraph();
  }

  public static void prepareScreenshot() {
    Navigation.toTrafficGraph();
    start();
    recordData(10);
    stop();
  }
}
