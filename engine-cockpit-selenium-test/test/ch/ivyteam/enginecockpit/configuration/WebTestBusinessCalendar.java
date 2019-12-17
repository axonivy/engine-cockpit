package ch.ivyteam.enginecockpit.configuration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

public class WebTestBusinessCalendar extends WebTestBase
{
  
  @Test
  void testBusinessCalendarTree()
  {
    toBusinessCalendar();
    $("#form\\:card\\:apps\\:applicationTabView\\:" + Tab.getSelectedTabIndex() + 
            "\\:treeForm\\:tree\\:0\\:calendarNode").shouldBe(text("Default"));
  }
  
  @Test
  void testBusinessCalendarDetail()
  {
    toBusinessCalendarDetail("Default");
    $("#weekConfigurationPanel").shouldHave(text("Week configuration"), 
            text("Start day of week\nMONDAY"),
            text("Free days of week Day"), 
            text("weekend1 SATURDAY"), 
            text("Working time Time"), 
            text("morning 08:00:00 - 12:00:00"));
    $("#freeDaysOfYearPanel").shouldHave(text("Free days of year"), 
            text("Description Day"));
    $("#freeEasterRelativeDaysPanel").shouldHave(text("Free Easter relative days"), 
            text("Description Days after Easter"));
    $("#freeDatesPanel").shouldHave(text("Free non-recurring dates"), 
            text("Description Date"));
  }
  
  private void toBusinessCalendar()
  {
    login();
    Navigation.toBusinessCalendar();
  }

  private void toBusinessCalendarDetail(String calendarName)
  {
    login();
    Navigation.toBusinessCalendarDetail(calendarName);
  }
}
