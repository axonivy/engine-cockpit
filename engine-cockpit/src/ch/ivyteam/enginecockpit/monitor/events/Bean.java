package ch.ivyteam.enginecockpit.monitor.events;

import java.lang.management.ManagementFactory;
import java.util.Date;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.util.DateUtil;

public abstract class Bean {

  private static final String NOT_AVAILABLE = "n.a.";
  private static final Object[] EMPTY_PARAMS = new Object[0];
  private static final String[] EMPTY_TYPES = new String[0];
  private ObjectName name;

  public Bean(ObjectName name) {
    this.name = name;
  }

  public String getName() {
    return name.getKeyProperty("name");
  }

  public String getBeanName() {
    return readStringAttribute("name");
  }

  public String getBeanDescription() {
    return readStringAttribute("description");
  }

  public abstract String getFullRequestPath();

  public String getApplication() {
    return name.getKeyProperty("application");
  }

  public String getPm() {
    return name.getKeyProperty("pm");
  }

  public String getPmv() {
    return name.getKeyProperty("pmv");
  }

  public boolean isRunning() {
    return (Boolean)readAttribute("running");
  }

  public String getNextPollTime() {
    return getDateAttribute("nextPollTime");
  }

  public long getTimeUntilNextPoll() {
    return readLongAttribute("timeUntilNextPoll");
  }

  public String getTimeUntilNextPollFormated() {
    return formatMillis(readLongAttribute("timeUntilNextPoll"));
  }

  public abstract long getExecutions();
  public abstract long getErrors();

  public void poll() {
    try {
      ManagementFactory.getPlatformMBeanServer().invoke(name, "pollNow", EMPTY_PARAMS, EMPTY_TYPES);
    } catch (InstanceNotFoundException | ReflectionException | MBeanException ex) {
      throw new RuntimeException(ex);
    }
  }

  public void start() {
    try {
      ManagementFactory.getPlatformMBeanServer().invoke(name, "start", EMPTY_PARAMS, EMPTY_TYPES);
    } catch (InstanceNotFoundException | ReflectionException | MBeanException ex) {
      throw new RuntimeException(ex);
    }
  }

  public void stop() {
    try {
      ManagementFactory.getPlatformMBeanServer().invoke(name, "stop", EMPTY_PARAMS, EMPTY_TYPES);
    } catch (InstanceNotFoundException | ReflectionException | MBeanException ex) {
      throw new RuntimeException(ex);
    }
  }

  protected String formatMillis(long value) {
    return format(value, Unit.MILLI_SECONDS);
  }

  private String format(long value, Unit baseUnit) {
    Unit unit = baseUnit;
    var scaledValue = baseUnit.convertTo(value, unit);
    while (shouldScaleUp(unit, scaledValue)) {
      unit = unit.scaleUp();
      scaledValue = baseUnit.convertTo(value, unit);
    }
    return scaledValue+" "+ unit.symbol();
  }

  private boolean shouldScaleUp(Unit unit, long scaledValue) {
    if (Unit.MICRO_SECONDS.equals(unit) || Unit.MILLI_SECONDS.equals(unit)) {
      return scaledValue >= 1000;
    }
    return scaledValue >= 100;
  }

  protected String getDateAttribute(String attributeName) {
    Date date = (Date)readAttribute(attributeName);
    if (date == null) {
      return NOT_AVAILABLE;
    }
    return DateUtil.formatDate(date);
  }

  protected long readLongAttribute(String attribute) {
    return (long)readAttribute(attribute);
  }

  protected String readStringAttribute(String attribute) {
    return (String)readAttribute(attribute);
  }

  protected Object readAttribute(String attribute) {
    try {
      return ManagementFactory.getPlatformMBeanServer().getAttribute(name, attribute);
    } catch (InstanceNotFoundException | AttributeNotFoundException | ReflectionException | MBeanException ex) {
      throw new RuntimeException(ex);
    }
  }
}
