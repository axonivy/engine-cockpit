package ch.ivyteam.enginecockpit.model;

import java.util.List;
import java.util.stream.Collectors;

import ch.ivyteam.ivy.application.calendar.FreeDate;
import ch.ivyteam.ivy.application.calendar.FreeDayOfWeek;
import ch.ivyteam.ivy.application.calendar.FreeDayOfYear;
import ch.ivyteam.ivy.application.calendar.FreeEasterRelativeDay;
import ch.ivyteam.ivy.application.calendar.IBusinessCalendarConfiguration;
import ch.ivyteam.ivy.application.calendar.WorkingTime;
import ch.ivyteam.util.date.Weekday;

public class BusinessCalendar
{
  private String name;
  private Weekday firstDayOfWeek;
  private List<Row> workingTimes;
  private List<Row> freeDaysOfWeek;
  private List<Row> freeDaysOfYear;
  private List<Row> freeEasterRelativeDays;
  private List<Row> freeDates;
  
  public BusinessCalendar(IBusinessCalendarConfiguration calendarConfig)
  {
    this.name = calendarConfig.getName();
    this.firstDayOfWeek = calendarConfig.getFirstDayOfWeek();
    this.workingTimes = calendarConfig.getWorkingTimes().stream().map(time -> new Row(time)).collect(Collectors.toList());
    this.freeDaysOfWeek = calendarConfig.getFreeDaysOfWeek().stream().map(day -> new Row(day)).collect(Collectors.toList());
    this.freeDaysOfYear = calendarConfig.getFreeDaysOfYear().stream().map(day -> new Row(day)).collect(Collectors.toList());
    this.freeEasterRelativeDays = calendarConfig.getFreeEasterRelativeDays().stream().map(day -> new Row(day)).collect(Collectors.toList());
    this.freeDates = calendarConfig.getFreeDates().stream().map(day -> new Row(day)).collect(Collectors.toList());
  }
  
  public String getName()
  {
    return name;
  }
  
  public Weekday getFirstDayOfWeek()
  {
    return firstDayOfWeek;
  }
  
  public List<Row> getWorkingTimes()
  {
    return workingTimes;
  }
  
  public List<Row> getFreeDaysOfWeek()
  {
    return freeDaysOfWeek;
  }
  
  public List<Row> getFreeDaysOfYear()
  {
    return freeDaysOfYear;
  }
  
  public List<Row> getFreeEasterRelativeDays()
  {
    return freeEasterRelativeDays;
  }
  
  public List<Row> getFreeDates()
  {
    return freeDates;
  }
  
  public static final class Row
  {
    private String description;
    private String value;
    
    public Row(FreeDayOfWeek day)
    {
      this.description = day.getDescription();
      this.value = day.getDayOfWeek().toString();
    }
    
    public Row(WorkingTime time)
    {
      this.description = time.getDescription();
      this.value = time.toString();
    }

    public Row(FreeDayOfYear freeDay)
    {
      this.description = freeDay.getDescription();
      String day = String.valueOf(freeDay.getDay());
      String month = String.valueOf(freeDay.getMonth());

      this.value = day + "." + month + ".";
    }

    public Row(FreeEasterRelativeDay freeDay)
    {
      this.description = freeDay.getDescription();
      this.value = String.valueOf(freeDay.getDaysSinceEaster());
    }

    public Row(FreeDate freeDay)
    {
      this.description = freeDay.getDescription();
      this.value = freeDay.getDate().toString();
    }

    public String getDescription()
    {
      return description;
    }
    
    public String getValue()
    {
      return value;
    }
  }
}
