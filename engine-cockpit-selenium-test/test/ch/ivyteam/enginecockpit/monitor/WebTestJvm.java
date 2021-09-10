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
public class WebTestJvm {

  @BeforeAll
  static void beforeAll() {
    login();
    Navigation.toJvm();
  }

  @Test
  void jvmPageContent() {
    $$(".ui-panel").shouldHave(size(3));
  }

  @Test
  void cpuContent() {
    $("#cpu")
            .shouldHave(text("CPU Load"))
            .find(".jqplot-grid-canvas").shouldBe(visible);
  }

  @Test
  void classesContent() {
    $("#classes")
            .shouldHave(text("Classes"))
            .find(".jqplot-grid-canvas").shouldBe(visible);
  }

  @Test
  void threadsContent() {
    $("#threads")
            .shouldHave(text("Threads"))
            .find(".jqplot-grid-canvas").shouldBe(visible);
  }

}
