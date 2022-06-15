package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
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
import com.codeborne.selenide.Condition;

import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
public class WebTestTrafficGraph {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toTrafficGraph();
    stop();
    var clear = $(id("form:clear"));
    if (clear.is(enabled)) {
      clear.click();
    }
  }

  private static void stop() {
    var stop = $(id("form:stop"));
    if (stop.is(enabled)) {
      stop.click();
    }
  }

  @Test
  void buttonState_initial() {
    $(id("form:start")).shouldBe(visible, enabled);
    $(id("form:stop")).shouldBe(visible, disabled);
    $(id("form:clear")).shouldBe(visible, disabled);
    $(id("form:refresh")).shouldBe(visible, disabled);
    $(id("performance:form:loggingWarning")).shouldBe(Condition.hidden);
  }

  @Test
  void buttonState_afterStart() {
    start();
    $(id("form:start")).shouldBe(visible, disabled);
    $(id("form:stop")).shouldBe(visible, enabled);
    $(id("form:clear")).shouldBe(visible, disabled);
    $(id("form:refresh")).shouldBe(visible, enabled);
  }

  @Test
  void buttonState_afterCancelStart() {
    $(id("form:start")).click();
    $(id("startTraces:cancel")).click();
    $(id("form:start")).shouldBe(visible, enabled);
    $(id("form:stop")).shouldBe(visible, disabled);
    $(id("form:clear")).shouldBe(visible, disabled);
    $(id("form:refresh")).shouldBe(visible, disabled);
    $(id("form:loggingWarning")).shouldBe(Condition.hidden);
  }

  @Test
  void buttonState_afterStop() {
    start();
    $(id("form:stop")).click();
    $(id("form:start")).shouldBe(visible, enabled);
    $(id("form:stop")).shouldBe(visible, disabled);
    $(id("form:clear")).shouldBe(visible, disabled);
    $(id("form:refresh")).shouldBe(visible, disabled);
  }

  @Test
  void buttonState_afterRecording() {
    recordData();

    $(id("form:start")).shouldBe(visible, disabled);
    $(id("form:stop")).shouldBe(visible, enabled);
    $(id("form:clear")).shouldBe(visible, enabled);
    $(id("form:refresh")).shouldBe(visible, enabled);
  }

  @Test
  void data_afterRecording() {
    recordData();

    $$(By.className("ui-diagram-element")).shouldHave(size(2));
  }

  @Test
  void data_afterClear() {
    recordData();
    $(id("form:clear")).click();
    $$(By.className("ui-diagram-element")).shouldHave(size(1));
  }

  private static void recordData() {
    recordData(1);
  }

  private static void recordData(int requests) {
    start();
    while (requests-- > 0) {
      Navigation.toProcessExecution();
    }
    Navigation.toTrafficGraph();
  }

  private static void start() {
    $(id("form:start")).click();
    $(id("startTraces:start")).click();
  }

  public static void prepareScreenshot() {
    Navigation.toTrafficGraph();
    stop();
    recordData(10);
  }
}
