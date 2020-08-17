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
public class WebTestWebServices
{
  
  @BeforeAll
  static void beforeAll()
  {
    login();
    Navigation.toWebServices();
  }
  
  @Test
  void webServicesPageContent()
  {
    $$(".ui-panel").shouldHave(size(2));
  }  
  
  @Test
  void callsContent()
  {
    $("#soapCalls")
        .shouldHave(text("Web Service Calls Monitor"))
        .find(".jqplot-target").shouldBe(visible);
  }
    
  @Test
  void executionTimeContent()
  {
    $("#soapExecutionTime")
        .shouldHave(text("Web Service Execution Time Monitor"))
        .find(".jqplot-target").shouldBe(visible);
  }
}
