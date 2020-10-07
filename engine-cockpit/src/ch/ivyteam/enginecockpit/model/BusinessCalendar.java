package ch.ivyteam.enginecockpit.model;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.application.calendar.CalendarListEntry;
import ch.ivyteam.ivy.application.calendar.FreeDate;
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
  private List<TimeDayConfig> workingTimes;
  private List<String> freeDaysOfWeek;
  private List<TimeDayConfig> freeDays;
  
  public BusinessCalendar(IBusinessCalendarConfiguration calendarConfig)
  {
    this.name = calendarConfig.getName();
    this.firstDayOfWeek = calendarConfig.getFirstDayOfWeek();

    this.workingTimes = new ArrayList<>();
    this.freeDaysOfWeek = new ArrayList<>();
    this.freeDays = new ArrayList<>();
    
    addConfigurationsToLists(calendarConfig);
  }
  
  private void addConfigurationsToLists(IBusinessCalendarConfiguration calendarConfig)
  {
    String calendarName = calendarConfig.getName();

    if(calendarConfig.getParent() != null)
    {
      addConfigurationsToLists(calendarConfig.getParent());
    }
    
    freeDays.addAll(calendarConfig.getFreeDaysOfYear().stream().map(day -> new TimeDayConfig(day, calendarName)).collect(Collectors.toList()));
    freeDays.addAll(calendarConfig.getFreeEasterRelativeDays().stream().map(day -> new TimeDayConfig(day, calendarName)).collect(Collectors.toList()));
    freeDays.addAll(calendarConfig.getFreeDates().stream().map(day -> new TimeDayConfig(day, calendarName)).collect(Collectors.toList()));

    workingTimes.addAll(calendarConfig.getWorkingTimes().stream().map(time -> new TimeDayConfig(time, calendarName)).collect(Collectors.toList()));
    freeDaysOfWeek.addAll(calendarConfig.getFreeDaysOfWeek().stream().map(day -> day.getDayOfWeek().getName()).collect(Collectors.toList()));
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
  
  public List<TimeDayConfig> getWorkingTimes()
  {
    return workingTimes;
  }
  
  public List<WeekDay> getWeek()
  {
    var firstDay = DayOfWeek.valueOf(firstDayOfWeek.getName().toUpperCase());
    var days = new ArrayList<WeekDay>();
    for (var i = 0; i < 7; i++)
    {
      days.add(new WeekDay(firstDay.plus(i)));
    }
    return days;
  }
  
  public List<TimeDayConfig> getFreeDays()
  {
    return freeDays;
  }
  
  public final class WeekDay
  {
    private String dayName;
    private boolean free;
    
    public WeekDay(DayOfWeek day)
    {
      this.dayName = StringUtils.capitalize(day.name().toLowerCase());
      free = freeDaysOfWeek.contains(dayName);
    }
    
    public String getDayName()
    {
      return dayName;
    }
    
    public boolean isFree()
    {
      return free;
    }
  }
  
  public final class TimeDayConfig
  {
    private String desc;
    private String value;
    private String calendarName;
    private String icon;
    
    private TimeDayConfig(CalendarListEntry day)
    {
      this.desc = day.getDescription();
      this.value = day.getValue();
    }
    
    public TimeDayConfig(FreeDayOfYear freeDay, String calendarName)
    {
      this(freeDay);
      this.calendarName = calendarName;
      this.icon = "update";
    }

    public TimeDayConfig(FreeEasterRelativeDay freeDay, String calendarName)
    {
      this(freeDay);
      this.calendarName = calendarName;
      this.icon = "timer";
    }

    public TimeDayConfig(FreeDate freeDay, String calendarName)
    {
      this(freeDay);
      this.calendarName = calendarName;
      this.icon = "schedule";
    }
    
    public TimeDayConfig(WorkingTime time, String calendarName)
    {
      this(time);
      this.calendarName = calendarName;
      this.icon = "alarm";
    }
    
    public String getDesc()
    {
      return desc;
    }
    
    public String getValue()
    {
      return value;
    }

    public String getCalendarName()
    {
      return calendarName;
    }

    public String getIcon()
    {
      return icon;
    }
  }
  
}
