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
public class WebTestRequests
{
  
  @BeforeAll
  static void beforeAll()
  {
    login();
    Navigation.toRequests();
  }
  
  @Test
  void requestsPageContent()
  {
    $$(".ui-panel").shouldHave(size(4));
  }  
  
  @Test
  void requestsContent()
  {
    $("#requests")
        .shouldHave(text("Requests Monitor"))
        .find(".jqplot-grid-canvas").shouldBe(visible);
  }

  @Test
  void errorsContent()
  {
    $("#errors")
        .shouldHave(text("Errors Monitor"))
        .find(".jqplot-grid-canvas").shouldBe(visible);
  }
  
  @Test
  void bytesContent()
  {
    $("#bytes")
        .shouldHave(text("Bytes Monitor"))
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
