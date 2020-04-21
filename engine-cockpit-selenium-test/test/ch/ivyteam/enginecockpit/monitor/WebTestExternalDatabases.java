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
public class WebTestExternalDatabases
{
  
  @BeforeAll
  static void beforeAll()
  {
    login();
    Navigation.toMonitorExternalDatabases();
  }
  
  @Test
  void externalDatabasesPageContent()
  {
    $$(".ui-panel").shouldHave(size(3));
  }  
  
  @Test
  void connectionsContent()
  {
    $("#externalDatabaseConnections")
        .shouldHave(text("External Database Connections Monitor"))
        .find(".jqplot-target").shouldBe(visible);
  }

  @Test
  void queriesContent()
  {
    $("#externalDatabaseQueries")
        .shouldHave(text("External Database Queries"))
        .find(".jqplot-target").shouldBe(visible);
  }
    
  @Test
  void executionTimeContent()
  {
    $("#externalDatabaseExecutionTime")
        .shouldHave(text("External Database Query Execution Time Monitor"))
        .find(".jqplot-target").shouldBe(visible);
  }
}
