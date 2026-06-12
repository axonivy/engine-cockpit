package ch.ivyteam.enginecockpit.testdata.businesscalendar;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.calendar.IDefaultBusinessCalendar;

public class TestBusinessCalendarData {

  public static void setBusinessCalendar() {
    var app = IApplication.current();
    var calendar = IDefaultBusinessCalendar.of(app);
    IDefaultBusinessCalendar.set(app, calendar.get("Luzern"));
  }
}
