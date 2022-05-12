package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
public class WebTestMemory {

  @BeforeAll
  static void beforeAll() {
    login();
    Navigation.toMemory();
  }

  @Test
  void memoryPageContent() {
    $$(".card").shouldHave(size(3));
  }

  @Test
  void gcContent() {
    $("#gc")
            .shouldHave(text("Garbage Collection"))
            .find(".jqplot-grid-canvas").shouldBe(visible);
  }

  @Test
  void heapMemoryContent() {
    $("#heapMemory")
            .shouldHave(text("Heap Memory"))
            .find(".jqplot-grid-canvas").shouldBe(visible);
  }

  @Test
  void nonHeapMemoryContent() {
    $("#nonHeapMemory")
            .shouldHave(text("Non Heap Memory"))
            .find(".jqplot-grid-canvas").shouldBe(visible);
  }
}
