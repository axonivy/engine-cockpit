package ch.ivyteam.enginecockpit.monitor;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Stream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;

import ch.ivyteam.enginecockpit.monitor.performance.jfr.JfrBean;
import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.util.DateUtil;
import ch.ivyteam.enginecockpit.util.ErrorHandler;
import ch.ivyteam.enginecockpit.util.ErrorValue;
import ch.ivyteam.log.Logger;

@ManagedBean
@ViewScoped
public class StartEventDetailBean {
  private static final String NOT_AVAILABLE = "n.a.";
  private static final Object[] EMPTY_PARAMS = new Object[0];
  private static final String[] EMPTY_TYPES = new String[0];
  private static final Logger LOGGER = Logger.getPackageLogger(JfrBean.class);
  private static final ErrorHandler HANDLER = new ErrorHandler("msgs", LOGGER);

  private ObjectName objectName;
  private String application;
  private String pm;
  private String pmv;
  private String name;

  private List<Firing> firings;

  public StartEventDetailBean() {
  }

  public String onLoad() {
    var hashtable = new Hashtable<String, String>();
    hashtable.put("type", "Process Start Event Bean");
    hashtable.put("application", application);
    hashtable.put("pm", pm);
    hashtable.put("pmv", pmv);
    hashtable.put("name", name);
    try {
      this.objectName = new ObjectName("ivy Engine", hashtable);
    } catch (MalformedObjectNameException ex) {
      HANDLER.showError("Cannot create MBean name", ex);
    }
    refresh();
    return null;
  }

  public String getBeanClass() {
    return readStringAttribute("beanClass");
  }

  public String getBeanName() {
    return readStringAttribute("name");
  }

  public String getBeanDescription() {
    return readStringAttribute("description");
  }

  public String getFullRequestPath() {
    return getApplication() +
           "/"+
           getPm() +
           "$" +
           getPmv() +
           "/" +
           readStringAttribute("requestPath");
  }

  public String getBeanConfiguration() {
    return readStringAttribute("beanConfiguration");
  }

  public String getApplication() {
    return application;
  }

  public void setApplication(String application) {
    this.application = application;
  }

  public String getPm() {
    return pm;
  }

  public void setPm(String pm) {
    this.pm = pm;
  }

  public String getPmv() {
    return pmv;
  }

  public void setPmv(String pmv) {
    this.pmv = pmv;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isRunning() {
    return (Boolean)readAttribute("running");
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

  public ErrorValue getLastPollError() {
    return getErrorAttribute("lastPollError");
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

  public long getExecutions() {
    return readLongAttribute("processExecutions");
  }

  public long getErrors() {
    return readLongAttribute("processExecutionErrors");
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

  public String getMinExecutionTime() {
    long executions = getExecutions();
    if (executions == 0) {
      return NOT_AVAILABLE;
    }
    return formatMicros((Long)readAttribute("processExecutionsMinExecutionTimeInMicroSeconds"));
  }

  public String getAvgExecutionTime() {
    var total = (Long)readAttribute("processExecutionsTotalExecutionTimeInMicroSeconds");
    if (total == null) {
      return formatMicros(total);
    }
    long executions = getExecutions();
    if (executions == 0) {
      return NOT_AVAILABLE;
    }
    return formatMicros(total/executions);
  }

  public String getMaxExecutionTime() {
    long executions = getExecutions();
    if (executions == 0) {
      return NOT_AVAILABLE;
    }
    return formatMicros((Long)readAttribute("processExecutionsMaxExecutionTimeInMicroSeconds"));
  }

  public String getMinPollTime() {
    long polls = getPolls();
    if (polls == 0) {
      return NOT_AVAILABLE;
    }
    return formatMicros((Long)readAttribute("pollsMinExecutionTimeInMicroSeconds"));
  }

  public String getAvgPollTime() {
    var total = (Long)readAttribute("pollsTotalExecutionTimeInMicroSeconds");
    if (total == null) {
      return formatMicros(total);
    }
    long polls = getPolls();
    if (polls == 0) {
      return NOT_AVAILABLE;
    }
    return formatMicros(total/polls);
  }

  public String getMaxPollTime() {
    long polls = getPolls();
    if (polls == 0) {
      return NOT_AVAILABLE;
    }
    return formatMicros((Long)readAttribute("pollsMaxExecutionTimeInMicroSeconds"));
  }

  public void poll() {
    try {
      ManagementFactory.getPlatformMBeanServer().invoke(objectName, "pollNow", EMPTY_PARAMS, EMPTY_TYPES);
    } catch (InstanceNotFoundException | ReflectionException | MBeanException ex) {
      HANDLER.showError("Cannot poll bean", ex);
    }
  }


  public void start() {
    try {
      ManagementFactory.getPlatformMBeanServer().invoke(objectName, "start", EMPTY_PARAMS, EMPTY_TYPES);
    } catch (InstanceNotFoundException | ReflectionException | MBeanException ex) {
      HANDLER.showError("Cannot start bean", ex);
    }
  }

  public void stop() {
    try {
      ManagementFactory.getPlatformMBeanServer().invoke(objectName, "stop", EMPTY_PARAMS, EMPTY_TYPES);
    } catch (InstanceNotFoundException | ReflectionException | MBeanException ex) {
      HANDLER.showError("Cannot stop bean", ex);
    }
  }

  public void refresh() {
    CompositeData[] data = (CompositeData[])readAttribute("firingHistory");
    firings = new ArrayList<>(Stream.of(data).map(StartEventDetailBean::toFiring).toList());
  }

  public List<Firing> getFirings() {
    return firings;
  }

  private static Firing toFiring(Object firing) {
    var execution = (CompositeData)firing;
    return new Firing(
        (Date)execution.get("firingTimestamp"),
        (long)execution.get("firingTimeInMicroSeconds"),
        (String)execution.get("firingReason"),
        new ErrorValue((CompositeData)execution.get("error")));
  }
  private static String formatMillis(long value) {
    return format(value, Unit.MILLI_SECONDS);
  }

  private static String formatMicros(Long value) {
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
    return scaledValue+" "+ unit.symbol();
  }

  private static boolean shouldScaleUp(Unit unit, long scaledValue) {
    if (Unit.MICRO_SECONDS.equals(unit) || Unit.MILLI_SECONDS.equals(unit)) {
      return scaledValue >= 1000;
    }
    return scaledValue >= 100;
  }


  private ErrorValue getErrorAttribute(String attributeName) {
    return new ErrorValue((CompositeData)readAttribute(attributeName));
  }

  private String getDateAttribute(String attributeName) {
    Date date = (Date)readAttribute(attributeName);
    if (date == null) {
      return NOT_AVAILABLE;
    }
    return DateUtil.formatDate(date);
  }

  private long readLongAttribute(String attribute) {
    return (long)readAttribute(attribute);
  }

  private String readStringAttribute(String attribute) {
    return (String)readAttribute(attribute);
  }

  Object readAttribute(String attribute) {
    try {
      return ManagementFactory.getPlatformMBeanServer().getAttribute(objectName, attribute);
    } catch (InstanceNotFoundException | AttributeNotFoundException | ReflectionException | MBeanException ex) {
      HANDLER.showError("Cannot read attribute " + attribute, ex);
      return "";
    }
  }

  public static final class Firing {
    private Date timestamp;
    private long duration;
    private String reason;
    private ErrorValue error;

    public Firing(Date timestamp, long duration, String reason, ErrorValue error) {
      this.timestamp = timestamp;
      this.duration = duration;
      this.reason = reason;
      this.error = error;
    }

    public String getTimestamp() {
      return DateUtil.formatDate(timestamp);
    }

    public String getDuration() {
      return formatMicros(duration);
    }

    public String getReason() {
      return reason;
    }

    public ErrorValue getError() {
      return error;
    }
  }
}
