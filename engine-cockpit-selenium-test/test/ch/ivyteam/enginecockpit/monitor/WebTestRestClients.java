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
public class WebTestRestClients
{
  
  @BeforeAll
  static void beforeAll()
  {
    login();
    Navigation.toMonitorRestClients();
  }
  
  @Test
  void restClientsPageContent()
  {
    $$(".ui-panel").shouldHave(size(3));
  }  

  @Test
  void connectionsContent()
  {
    $("#restConnections")
        .shouldHave(text("REST Client Connections Monitor"))
        .find(".jqplot-target").shouldBe(visible);
  }

  @Test
  void callsContent()
  {
    $("#restCalls")
        .shouldHave(text("REST Client Calls Monitor"))
        .find(".jqplot-target").shouldBe(visible);
  }
    
  @Test
  void executionTimeContent()
  {
    $("#restExecutionTime")
        .shouldHave(text("REST Client Execution Time Monitor"))
        .find(".jqplot-target").shouldBe(visible);
  }

}
