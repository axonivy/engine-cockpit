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
public class WebTestOs
{
  
  @BeforeAll
  static void beforeAll()
  {
    login();
    Navigation.toOs();
  }
  
  @Test
  void osPageContent()
  {
    $$(".ui-panel").shouldHave(size(4));
  }  
  
  @Test
  void cpuContent()
  {
    $("#cpu")
        .shouldHave(text("CPU Load Monitor"))
        .find(".jqplot-grid-canvas").shouldBe(visible);
  }

  @Test
  void memoryContent()
  {
    $("#memory")
        .shouldHave(text("Memory Monitor"))
        .find(".jqplot-grid-canvas").shouldBe(visible);
  }
  
  @Test
  void networkContent()
  {
    $("#network")
        .shouldHave(text("Network Monitor"))
        .find(".jqplot-grid-canvas").shouldBe(visible);
  }
  
  @Test
  void ioContent()
  {
    $("#io")
        .shouldHave(text("IO Monitor"))
        .find(".jqplot-grid-canvas").shouldBe(visible);
  }



}
