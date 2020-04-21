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
public class WebTestEmail
{
  
  @BeforeAll
  static void beforeAll()
  {
    login();
    Navigation.toMonitorEmail();
  }
  
  @Test
  void emailPageContent()
  {
    $$(".ui-panel").shouldHave(size(2));
  }  
  
  @Test
  void sentContent()
  {
    $("#mailsSent")
        .shouldHave(text("Mails Sent Monitor"))
        .find(".jqplot-grid-canvas").shouldBe(visible);
  }
    
  @Test
  void executionTimeContent()
  {
    $("#mailsExecutionTime")
        .shouldHave(text("Mail Sending Execution Time Monitor"))
        .find(".jqplot-grid-canvas").shouldBe(visible);
  }
}
