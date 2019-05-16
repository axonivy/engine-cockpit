package ch.ivyteam.enginecockpit.model;

import java.util.List;
import java.util.stream.Collectors;

import ch.ivyteam.ivy.application.calendar.FreeDate;
import ch.ivyteam.ivy.application.calendar.FreeDayOfWeek;
import ch.ivyteam.ivy.application.calendar.FreeDayOfYear;
import ch.ivyteam.ivy.application.calendar.FreeEasterRelativeDay;
import ch.ivyteam.ivy.application.calendar.IBusinessCalendarConfiguration;
import ch.ivyteam.util.date.Weekday;

public class BusinessCalendar
{
  private String name;
  private Weekday firstDayOfWeek;
  private List<String> workingTimes;
  private List<FreeDayOfWeek> freeDaysOfWeek;
  private List<FreeDayOfYear> freeDaysOfYear;
  private List<FreeEasterRelativeDay> freeEasterRelativeDays;
  private List<FreeDate> freeDates;
  
  public BusinessCalendar(IBusinessCalendarConfiguration calendarConfig)
  {
    this.name = calendarConfig.getName();
    this.firstDayOfWeek = calendarConfig.getFirstDayOfWeek();
    this.workingTimes = calendarConfig.getWorkingTimes().stream().map(time -> time.toString()).collect(Collectors.toList());

    this.freeDaysOfWeek = calendarConfig.getFreeDaysOfWeek();
    this.freeDaysOfYear = calendarConfig.getFreeDaysOfYear();
    this.freeEasterRelativeDays = calendarConfig.getFreeEasterRelativeDays();
    this.freeDates = calendarConfig.getFreeDates();
  }
  
  public String getName()
  {
    return name;
  }
  
  public Weekday getFirstDayOfWeek()
  {
    return firstDayOfWeek;
  }
  
  public List<String> getWorkingTimes()
  {
    return workingTimes;
  }
  
  public List<FreeDayOfWeek> getFreeDaysOfWeek()
  {
    return freeDaysOfWeek;
  }
  
  public List<FreeDayOfYear> getFreeDaysOfYear()
  {
    return freeDaysOfYear;
  }
  
  public List<FreeEasterRelativeDay> getFreeEasterRelativeDays()
  {
    return freeEasterRelativeDays;
  }
  
  public List<FreeDate> getFreeDates()
  {
    return freeDates;
  }

}
