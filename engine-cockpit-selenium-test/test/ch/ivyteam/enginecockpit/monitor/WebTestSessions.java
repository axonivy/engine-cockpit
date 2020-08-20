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
public class WebTestSessions
{
  
  @BeforeAll
  static void beforeAll()
  {
    login();
    Navigation.toSessions();
  }
  
  @Test
  void sessionsPageContent()
  {
    $$(".ui-panel").shouldHave(size(1));
  }  
  

  @Test
  void sessionsContent()
  {
    $("#sessions")
        .shouldHave(text("Sessions"))
        .find(".jqplot-grid-canvas").shouldBe(visible);
  }

}
