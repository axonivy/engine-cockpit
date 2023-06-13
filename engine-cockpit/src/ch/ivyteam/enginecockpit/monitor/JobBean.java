package ch.ivyteam.enginecockpit.monitor;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;

import com.google.common.collect.Streams;

import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.util.DateUtil;
import ch.ivyteam.enginecockpit.util.ErrorHandler;
import ch.ivyteam.enginecockpit.util.ErrorValue;
import ch.ivyteam.log.Logger;

@ManagedBean
@ViewScoped
public class JobBean {
  private static final Logger LOGGER = Logger.getPackageLogger(JobBean.class);
  private static final ErrorHandler HANDLER = new ErrorHandler("msgs", LOGGER);

  private List<Job> jobs;
  private List<Job> filteredJobs;
  private String filter;
  private Job selected;

  public JobBean() {
    refresh();
  }

  public void refresh() {
    try {
      var cronJobs = ManagementFactory.getPlatformMBeanServer()
              .queryNames(new ObjectName("ivy Engine:type=Cron Job,name=*"), null)
              .stream()
              .map(Job::new);
      var periodicalJobs = ManagementFactory.getPlatformMBeanServer()
              .queryNames(new ObjectName("ivy Engine:type=Periodical Job,name=*"), null)
              .stream()
              .map(Job::new);

      var allJobs = Streams
          .concat(cronJobs, periodicalJobs)
          .toList();
      jobs = new ArrayList<>(allJobs);
    } catch (MalformedObjectNameException ex) {
      throw new RuntimeException(ex);
    }
  }

  public List<Job> getJobs() {
    return jobs;
  }

  public List<Job> getFilteredJobs() {
    return filteredJobs;
  }

  public void setFilteredJobs(List<Job> filteredJobs) {
    this.filteredJobs = filteredJobs;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public Job getSelected() {
    return selected;
  }

  public void setSelected(Job selected) {
    this.selected = selected;
  }

  public static final class Job {

    private static final String NOT_AVAILABLE = "n.a.";
    private static final Object[] EMPTY_PARAMS = new Object[0];
    private static final String[] EMPTY_TYPES = new String[0];
    private ObjectName name;

    private Job(ObjectName name) {
      this.name = name;
    }

    public String getName() {
      return readStringAttribute("name");
    }

    public String getDescription() {
      return readStringAttribute("description");
    }

    public String getNextExecutionTime() {
      return DateUtil.formatDate((Date)readAttribute("nextExecutionTime"));
    }

    public long getTimeUntilNextExecution() {
      return readLongAttribute("timeUntilNextExecution");
    }

    public String getTimeUntilNextExecutionFormated() {
      return formatMillis(readLongAttribute("timeUntilNextExecution"));
    }

    public String getConfiguration() {
      if ("Cron Job".equals(name.getKeyProperty("type"))) {
        return readStringAttribute("expression") + " (" + readStringAttribute("humanReadableExpression")+")";
      }
      var atFixRate = (boolean)readAttribute("atFixedRate");
      var desc = atFixRate ? " (each)" : " (between)";
      return formatMillis(readLongAttribute("period")) + desc;
    }

    public long getExecutions() {
      return readLongAttribute("executions");
    }

    public long getErrors() {
      return readLongAttribute("errors");
    }

    public String getLastError() {
      return new ErrorValue((CompositeData)readAttribute("lastError"))
          .getStackTrace();
    }

    public String getLastErrorTime() {
      return getDateAttribute("lastErrorTime");
    }

    public String getLastSuccessTime() {
      return getDateAttribute("lastSuccessTime");
    }

    public String getMinExecutionTime() {
      long executions = getExecutions();
      if (executions == 0) {
        return NOT_AVAILABLE;
      }
      return formatMicros((Long)readAttribute("executionsMinExecutionTimeInMicroSeconds"));
    }

    public String getAvgExecutionTime() {
      var total = (Long)readAttribute("executionsTotalExecutionTimeInMicroSeconds");
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
      return formatMicros((Long)readAttribute("executionsMaxExecutionTimeInMicroSeconds"));
    }

    public void schedule() {
      try {
        ManagementFactory.getPlatformMBeanServer().invoke(name, "schedule", EMPTY_PARAMS, EMPTY_TYPES);
      } catch (InstanceNotFoundException | ReflectionException | MBeanException ex) {
        HANDLER.showError("Cannot schedule job for execution", ex);
      }
    }

    private String formatMillis(long value) {
      return format(value, Unit.MILLI_SECONDS);
    }

    private String formatMicros(Long value) {
      if (value == null) {
        return NOT_AVAILABLE;
      }
      return format(value, Unit.MICRO_SECONDS);
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
        return ManagementFactory.getPlatformMBeanServer().getAttribute(name, attribute);
      } catch (InstanceNotFoundException | AttributeNotFoundException | ReflectionException | MBeanException ex) {
        HANDLER.showError("Cannot read attribute " + attribute, ex);
        return null;
      }
    }
  }
}
