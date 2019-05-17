package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestBusinessCalendar extends WebTestBase
{
  
  @Test
  void testBusinessCalendarTree(FirefoxDriver driver)
  {
    toBusinessCalendar(driver);
    WebElement defaultCalendar = driver.findElementByXPath("//*[@id=\"calendarTree\"]//a[contains(@id, 'calendarNode')]");
    webAssertThat(() -> assertThat(defaultCalendar.getText()).isEqualTo("Default"));
  }
  
  @Test
  void testBusinessCalendarDetail(FirefoxDriver driver)
  {
    toBusinessCalendarDetail(driver, "Default");
    WebElement weekConfigurationPanel = driver.findElementById("weekConfigurationPanel");
    webAssertThat(() -> assertThat(weekConfigurationPanel.getText()).contains("Week configuration", "Start day of week MONDAY",
            "Free days of week Day", "Weekend SATURDAY", "Working time Time", "morning 08:00:00 - 12:00:00"));
    
    WebElement freeDaysOfYearPanel = driver.findElementById("freeDaysOfYearPanel");
    webAssertThat(() -> assertThat(freeDaysOfYearPanel.getText()).contains("Free days of year", "Description Day"));
    
    WebElement freeEasterRelativeDaysPanel = driver.findElementById("freeEasterRelativeDaysPanel");
    webAssertThat(() -> assertThat(freeEasterRelativeDaysPanel.getText()).contains("Free Easter relative days", "Description Days after Easter"));
    
    WebElement freeDatesPanel = driver.findElementById("freeDatesPanel");
    webAssertThat(() -> assertThat(freeDatesPanel.getText()).contains("Free non-recurring dates", "Description Date"));
  }
  
  private void toBusinessCalendar(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toBusinessCalendar(driver);
    saveScreenshot(driver, "businessCalendar");
  }

  private void toBusinessCalendarDetail(FirefoxDriver driver, String calendarName)
  {
    login(driver);
    Navigation.toBusinessCalendarDetail(driver, calendarName);
    saveScreenshot(driver, "businessCalendarDetail");
  }
}
