package ch.ivyteam.enginecockpit.testdata.businesscalendar;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.IApplicationInternal;

public class TestBusinessCalendarData {

  public static void setBusinessCalendar() {
    var app = (IApplicationInternal) IApplication.current();
    app.setBusinessCalendar(app.getBusinessCalendar().get("Luzern"));
  }
}
