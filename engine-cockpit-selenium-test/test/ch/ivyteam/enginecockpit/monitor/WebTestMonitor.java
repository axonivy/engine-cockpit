package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
public class WebTestMonitor
{
  
  @BeforeEach
  void beforeEach()
  {
    login();
  }
  
  @Test
  void resourcesMonitorContent()
  {
    Navigation.toResourcesMonitor();
    $$(".ui-panel").shouldHave(size(4));
  }
  
  @Test
  void logsViewContent()
  {
    Navigation.toLogs();
    $$(".ui-panel").shouldHave(size(5));
    $("#consoleLogView\\:logPanel_content").shouldBe(visible);
    $("#ivyLogView\\:logPanel").shouldHave(text("ivy.log"));
    $("#ivyLogView\\:logPanel .ui-panel-title > span").click();
    $("#ivyLogView\\:logPanel_content").shouldBe(visible);
    $("#ivyLogView\\:logPanel_toggler").click();
    $("#ivyLogView\\:logPanel_content").shouldNotBe(visible);
  }
  
  @Test
  void logsChangeDate()
  {
    Navigation.toLogs();
    String originDate = $("#logDateForm\\:calendar_input").shouldBe(visible).getValue();
    $("#ivyLogView\\:logPanel").shouldHave(text(originDate));
    
    $("#logDateForm\\:calendar > button").shouldBe(visible).click();
    $("#ui-datepicker-div").should(visible);
    $("#logDateForm\\:calendar_input").clear();
    $("#logDateForm\\:calendar_input").sendKeys("2020-04-01");
    $("#logDateForm\\:calendar_input").sendKeys(Keys.ENTER);
    $("#ivyLogView\\:logPanel").shouldHave(text("2020-04-01"));
  }
  
}
