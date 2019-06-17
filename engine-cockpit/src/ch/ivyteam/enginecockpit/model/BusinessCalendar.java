package ch.ivyteam.enginecockpit.model;

import java.util.ArrayList;
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
  private List<String> environments = new ArrayList<>();
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

    this.workingTimes = new ArrayList<>();
    this.freeDaysOfWeek = new ArrayList<>();
    this.freeDaysOfYear = new ArrayList<>();
    this.freeEasterRelativeDays = new ArrayList<>();
    this.freeDates = new ArrayList<>();
    
    addConfigurationsToLists(calendarConfig);
  }
  
  private void addConfigurationsToLists(IBusinessCalendarConfiguration calendarConfig)
  {
    String calendarName = calendarConfig.getName();

    if(calendarConfig.getParent() != null)
    {
      addConfigurationsToLists(calendarConfig.getParent());
    }
    
    calendarConfig.getWorkingTimes().stream().map(time -> new Row(time, calendarName)).forEach(row -> this.workingTimes.add(row));
    calendarConfig.getFreeDaysOfWeek().stream().map(day -> new Row(day, calendarName)).forEach(row -> this.freeDaysOfWeek.add(row));
    calendarConfig.getFreeDaysOfYear().stream().map(day -> new Row(day, calendarName)).forEach(row -> this.freeDaysOfYear.add(row));
    calendarConfig.getFreeEasterRelativeDays().stream().map(day -> new Row(day, calendarName)).forEach(row -> this.freeEasterRelativeDays.add(row));
    calendarConfig.getFreeDates().stream().map(day -> new Row(day, calendarName)).forEach(row -> this.freeDates.add(row));
  }
  
  public void addEnvironment(String environment)
  {
    environments.add(environment);
  }
  
  public String getEnvironments()
  {
    return environments.stream().collect(Collectors.joining(", "));
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
    private String calendarName;
    
    public Row(FreeDayOfWeek day, String calendarName)
    {
      this.description = day.getDescription();
      this.value = day.getDayOfWeek().toString();
      this.calendarName = calendarName;
    }
    
    public Row(WorkingTime time, String calendarName)
    {
      this.description = time.getDescription();
      this.value = time.getValue();
      this.calendarName = calendarName;
    }

    public Row(FreeDayOfYear freeDay, String calendarName)
    {
      this.description = freeDay.getDescription();
      String day = String.valueOf(freeDay.getDay());
      String month = String.valueOf(freeDay.getMonth());
      this.value = day + "." + month + ".";
      this.calendarName = calendarName;
    }

    public Row(FreeEasterRelativeDay freeDay, String calendarName)
    {
      this.description = freeDay.getDescription();
      this.value = String.valueOf(freeDay.getDaysSinceEaster());
      this.calendarName = calendarName;
    }

    public Row(FreeDate freeDate, String calendarName)
    {
      this.description = freeDate.getDescription();
      this.value = freeDate.getDate().toString();
      this.calendarName = calendarName;
    }

    public String getDescription()
    {
      return description;
    }
    
    public String getValue()
    {
      return value;
    }
    
    public String getCalendarName()
    {
      return calendarName;
    }
  }
}
