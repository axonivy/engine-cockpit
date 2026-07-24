package ch.ivyteam.enginecockpit.testdata.businesscalendar;

import ch.ivyteam.ivy.application.app.Application;
import ch.ivyteam.ivy.application.calendar.IDefaultBusinessCalendar;

public class TestBusinessCalendarData {

  public static void setBusinessCalendar() {
    var app = Application.current();
    var calendar = IDefaultBusinessCalendar.of(app);
    IDefaultBusinessCalendar.set(app, calendar.get("Luzern"));
  }
}
