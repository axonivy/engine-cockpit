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

import com.google.common.collect.Streams;

import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.util.DateUtil;

@ManagedBean
@ViewScoped
public class JobBean {

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
      return format(readLongAttribute("timeUntilNextExecution"));
    }

    public String getConfiguration() {
      if ("Cron Job".equals(name.getKeyProperty("type"))) {
        return readStringAttribute("expression") + " (" + readStringAttribute("humanReadableExpression")+")";
      }
      var atFixRate = (boolean)readAttribute("atFixedRate");
      var desc = atFixRate ? " (each)" : " (between)";
      return format(readLongAttribute("period")) + desc;
    }

    public long getExecutions() {
      return readLongAttribute("executions");
    }

    public long getErrors() {
      return readLongAttribute("errors");
    }

    public void schedule() {
      try {
        ManagementFactory.getPlatformMBeanServer().invoke(name, "schedule", EMPTY_PARAMS, EMPTY_TYPES);
      } catch (InstanceNotFoundException | ReflectionException | MBeanException ex) {
        throw new RuntimeException(ex);
      }
    }

    private String format(long value) {
      var unit = Unit.MILLI_SECONDS;
      var scaledValue = Unit.MILLI_SECONDS.convertTo(value, unit);
      while (scaledValue > 100) {
        unit= unit.scaleUp();
        scaledValue = Unit.MILLI_SECONDS.convertTo(value, unit);
      }
      return scaledValue+" "+ unit.symbol();
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
        throw new RuntimeException(ex);
      }
    }
  }
}
