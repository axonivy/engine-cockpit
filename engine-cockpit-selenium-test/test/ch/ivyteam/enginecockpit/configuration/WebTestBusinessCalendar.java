package ch.ivyteam.enginecockpit.configuration;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

@IvyWebTest
public class WebTestBusinessCalendar
{

  @BeforeEach
  void beforeEach()
  {
    login();
    Navigation.toBusinessCalendar();
    Tab.switchToDefault();
  }

  @Test
  void testBusinessCalendarTree()
  {
    $("#apps\\:applicationTabView\\:" + Tab.getSelectedTabIndex() + 
            "\\:treeForm\\:tree\\:0\\:calendarNode").shouldBe(text("Default"));
  }
  
  @Test
  void testBusinessCalendarDetail()
  {
    Navigation.toBusinessCalendarDetail("Luzern");
    $("#weekConfigurationPanel").shouldHave(text("Week configuration"));
    $$(".box").shouldHave(size(7)).first().shouldHave(text("Monday"));
    $$(".box.free").shouldHave(texts("Saturday", "Sunday"));
    $("#weekConfigurationPanel .ui-datatable").shouldHave(
            text("morning"), text("08:00:00 - 12:00:00"),
            text("afternoon"), text("13:00:00 - 17:00:00"));
    $("#freeDatesPanel").shouldHave(text("Free days"), 
            text("Christmas Day"), text("12-25"),
            text("Ascension Day"), text("easter + 39"));
  }
  
}
