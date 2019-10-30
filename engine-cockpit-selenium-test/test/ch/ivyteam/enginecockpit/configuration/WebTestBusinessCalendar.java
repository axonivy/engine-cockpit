package ch.ivyteam.enginecockpit.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestBusinessCalendar extends WebTestBase
{
  
  @Test
  void testBusinessCalendarTree()
  {
    toBusinessCalendar();
    webAssertThat(() -> assertThat(
            driver.findElementByXPath("//*[@id='form:card:apps:applicationTabView:0:treeForm:tree']//a[contains(@id, 'calendarNode')]").getText()).isEqualTo("Default (Default)"));
  }
  
  @Test
  void testBusinessCalendarDetail()
  {
    toBusinessCalendarDetail("Default");
    webAssertThat(() -> assertThat(
            driver.findElementById("weekConfigurationPanel").getText()).contains("Week configuration", "Start day of week\nMONDAY",
            "Free days of week Day", "weekend1 SATURDAY", "Working time Time", "morning 08:00:00 - 12:00:00"));
    
    webAssertThat(() -> assertThat(
            driver.findElementById("freeDaysOfYearPanel").getText()).contains("Free days of year", "Description Day"));
    
    webAssertThat(() -> assertThat(
            driver.findElementById("freeEasterRelativeDaysPanel").getText()).contains("Free Easter relative days", "Description Days after Easter"));
    
    webAssertThat(() -> assertThat(
            driver.findElementById("freeDatesPanel").getText()).contains("Free non-recurring dates", "Description Date"));
  }
  
  private void toBusinessCalendar()
  {
    login();
    Navigation.toBusinessCalendar(driver);
    saveScreenshot("businessCalendar");
  }

  private void toBusinessCalendarDetail(String calendarName)
  {
    login();
    Navigation.toBusinessCalendarDetail(driver, calendarName);
    saveScreenshot("businessCalendarDetail");
  }
}
