package ch.ivyteam.enginecockpit.monitor.events;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;

import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.util.DateUtil;
import ch.ivyteam.enginecockpit.util.ErrorHandler;
import ch.ivyteam.enginecockpit.util.ErrorValue;
import ch.ivyteam.log.Logger;

public abstract class Event {

  private static final Logger LOGGER = Logger.getPackageLogger(Event.class);
  private static final ErrorHandler HANDLER = new ErrorHandler("msgs", LOGGER);
  protected static final String NOT_AVAILABLE = "n.a.";
  protected static final Object[] EMPTY_PARAMS = new Object[0];
  protected static final String[] EMPTY_TYPES = new String[0];
  private ObjectName name;
  private List<Firing> firings;

  public Event(ObjectName name) {
    this.name = name;
  }

  public abstract long getExecutions();

  public abstract long getErrors();

  public abstract String getFullRequestPath();

  public abstract boolean showExecutionDuration();

  public String getName() {
    return name.getKeyProperty("name");
  }

  public String getBeanName() {
    return readStringAttribute("name");
  }

  public String getBeanDescription() {
    return readStringAttribute("description");
  }

  public String getApplication() {
    return name.getKeyProperty("application");
  }

  public String getPm() {
    return name.getKeyProperty("pm");
  }

  public String getPmv() {
    return name.getKeyProperty("pmv");
  }

  public String getBeanClass() {
    return readStringAttribute("beanClass");
  }

  public String getBeanConfiguration() {
    return readStringAttribute("beanConfiguration");
  }

  public ErrorValue getLastPollError() {
    return getErrorAttribute("lastPollError");
  }

  public boolean isRunning() {
    return (Boolean) readAttribute("running");
  }

  public String getServiceState() {
    return readStringAttribute("serviceState");
  }

  public ErrorValue getLastInitializationError() {
    return getErrorAttribute("lastInitializationError");
  }
  public String getLastStartTimestamp() {
    return getDateAttribute("lastStartTimestamp");
  }

  public ErrorValue getLastStartError() {
    return getErrorAttribute("lastStartError");
  }

  public ErrorValue getLastStopError() {
    return getErrorAttribute("lastStopError");
  }

  protected ErrorValue getErrorAttribute(String attributeName) {
    return new ErrorValue((CompositeData) readAttribute(attributeName));
  }

  public long getPolls() {
    return readLongAttribute("polls");
  }

  public long getPollErrors() {
    return readLongAttribute("pollErrors");
  }

  public String getPollConfiguration() {
    return readStringAttribute("pollConfiguration");
  }

  public String getHumanReadablePollConfiguration() {
    return readStringAttribute("humanReadablePollConfiguration");
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

  public String getMinPollTime() {
    long polls = getPolls();
    if (polls == 0) {
      return NOT_AVAILABLE;
    }
    return formatMicros((Long) readAttribute("pollsMinExecutionTimeInMicroSeconds"));
  }

  public void poll() {
    try {
      ManagementFactory.getPlatformMBeanServer().invoke(name, "pollNow", EMPTY_PARAMS, EMPTY_TYPES);
    } catch (InstanceNotFoundException | ReflectionException | MBeanException ex) {
      HANDLER.showError("Cannot poll bean", ex);
    }
  }

  public void start() {
    try {
      ManagementFactory.getPlatformMBeanServer().invoke(name, "start", EMPTY_PARAMS, EMPTY_TYPES);
    } catch (InstanceNotFoundException | ReflectionException | MBeanException ex) {
      HANDLER.showError("Cannot start bean", ex);
    }
  }

  public String getAvgPollTime() {
    var total = (Long) readAttribute("pollsTotalExecutionTimeInMicroSeconds");
    if (total == null) {
      return formatMicros(total);
    }
    long polls = getPolls();
    if (polls == 0) {
      return NOT_AVAILABLE;
    }
    return formatMicros(total / polls);
  }

  public void stop() {
    try {
      ManagementFactory.getPlatformMBeanServer().invoke(name, "stop", EMPTY_PARAMS, EMPTY_TYPES);
    } catch (InstanceNotFoundException | ReflectionException | MBeanException ex) {
      HANDLER.showError("Cannot stop bean", ex);
    }
  }

  public String getMaxPollTime() {
    long polls = getPolls();
    if (polls == 0) {
      return NOT_AVAILABLE;
    }
    return formatMicros((Long) readAttribute("pollsMaxExecutionTimeInMicroSeconds"));
  }

  protected static String formatMicros(Long value) {
    if (value == null) {
      return NOT_AVAILABLE;
    }
    return format(value, Unit.MICRO_SECONDS);
  }

  private static String format(long value, Unit baseUnit) {
    if (value < 0) {
      return "n.a.";
    }
    Unit unit = baseUnit;
    var scaledValue = baseUnit.convertTo(value, unit);
    while (shouldScaleUp(unit, scaledValue)) {
      unit = unit.scaleUp();
      scaledValue = baseUnit.convertTo(value, unit);
    }
    return scaledValue + " " + unit.symbol();
  }

  protected String formatMillis(long value) {
    return format(value, Unit.MILLI_SECONDS);
  }

  private static boolean shouldScaleUp(Unit unit, long scaledValue) {
    if (Unit.MICRO_SECONDS.equals(unit) || Unit.MILLI_SECONDS.equals(unit)) {
      return scaledValue >= 1000;
    }
    return scaledValue >= 120;
  }

  protected String getDateAttribute(String attributeName) {
    Date date = (Date) readAttribute(attributeName);
    if (date == null) {
      return NOT_AVAILABLE;
    }
    return DateUtil.formatDate(date);
  }

  protected long readLongAttribute(String attribute) {
    return (long) readAttribute(attribute);
  }

  protected String readStringAttribute(String attribute) {
    return (String) readAttribute(attribute);
  }

  protected Object readAttribute(String attribute) {
    try {
      return ManagementFactory.getPlatformMBeanServer().getAttribute(name, attribute);
    } catch (InstanceNotFoundException | AttributeNotFoundException | ReflectionException | MBeanException ex) {
      HANDLER.showError("Cannot read attribute " + attribute, ex);
      return "";
    }
  }

  public void refresh() {
    CompositeData[] data = (CompositeData[]) readAttribute("firingHistory");
    firings = new ArrayList<>(Stream.of(data).map(this::toFiring).toList());
  }

  public List<Firing> getFirings() {
    return firings;
  }

  private Firing toFiring(Object firing) {
    var execution = (CompositeData) firing;
    return new Firing(
            (Date) execution.get("firingTimestamp"),
            (long) execution.get("firingTimeInMicroSeconds"),
            (String) execution.get("firingReason"),
            new ErrorValue((CompositeData) execution.get("error")));
  }
}
