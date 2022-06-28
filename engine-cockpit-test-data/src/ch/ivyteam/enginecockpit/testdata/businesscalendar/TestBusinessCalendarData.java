package ch.ivyteam.enginecockpit.testdata.businesscalendar;

import ch.ivyteam.ivy.application.IApplication;

public class TestBusinessCalendarData {
  @SuppressWarnings("removal")
  public static void setBusinessCalendar() {
    var app = IApplication.current();
    app.findEnvironment("test").setBusinessCalendar(app.getActualEnvironment().getBusinessCalendar().get("Luzern"));
  }
}
