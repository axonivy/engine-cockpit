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
public class WebTestSystemDatabase
{
  
  @BeforeAll
  static void beforeAll()
  {
    login();
    Navigation.toSystemDatabase();
  }
  
  @Test
  void systemDatabasePageContent()
  {
    $$(".ui-panel").shouldHave(size(3));
  }  
  
  @Test
  void connectionsContent()
  {
    $("#connections")
        .shouldHave(text("Connections Monitor"))
        .find(".jqplot-grid-canvas").shouldBe(visible);
  }

  @Test
  void transactionsContent()
  {
    $("#transactions")
        .shouldHave(text("Transactions Monitor"))
        .find(".jqplot-grid-canvas").shouldBe(visible);
  }
    
  @Test
  void processingTimeContent()
  {
    $("#processingTime")
        .shouldHave(text("Processing Time Monitor"))
        .find(".jqplot-grid-canvas").shouldBe(visible);
  }
}
