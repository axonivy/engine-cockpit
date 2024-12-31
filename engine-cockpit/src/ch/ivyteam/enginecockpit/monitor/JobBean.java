package ch.ivyteam.enginecockpit.monitor;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.google.common.collect.Streams;

import ch.ivyteam.enginecockpit.monitor.mbeans.MBean;
import ch.ivyteam.enginecockpit.util.ErrorHandler;
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

    private final MBean bean;

    private Job(ObjectName name) {
      this.bean = MBean.create(HANDLER, name);
    }

    public String getName() {
      return bean.readAttribute("name").asString();
    }

    public String getDescription() {
      return bean.readAttribute("description").asString();
    }

    public String getNextExecutionTime() {
      return bean.readAttribute("nextExecutionTime").asDateString();
    }

    public long getTimeUntilNextExecution() {
      return bean.readAttribute("timeUntilNextExecution").asLong();
    }

    public String getTimeUntilNextExecutionFormated() {
      return bean.readAttribute("timeUntilNextExecution").asMillis();
    }

    public String getConfiguration() {
      if ("Cron Job".equals(bean.getNameKeyProperty("type"))) {
        return bean.readAttribute("expression").asString() + " (" + bean.readAttribute("humanReadableExpression").asString() + ")";
      }
      var atFixRate = bean.readAttribute("atFixedRate").asBoolean();
      var desc = atFixRate ? " (each)" : " (between)";
      return bean.readAttribute("period").asMillis() + desc;
    }

    public long getExecutions() {
      return bean.readAttribute("executions").asLong();
    }

    public long getErrors() {
      return bean.readAttribute("errors").asLong();
    }

    public String getLastError() {
      return bean.readAttribute("lastError").asError().getStackTrace();
    }

    public String getLastErrorTime() {
      return bean.readAttribute("lastErrorTime").asDateString();
    }

    public String getLastSuccessTime() {
      return bean.readAttribute("lastSuccessTime").asDateString();
    }

    public String getMinExecutionTime() {
      return bean.readAttribute("executions").asMinExecutionTime();
    }

    public String getAvgExecutionTime() {
      return bean.readAttribute("executions").asAvgExecutionTime();
    }

    public String getMaxExecutionTime() {
      return bean.readAttribute("executions").asMaxExecutionTime();
    }

    public void schedule() {
      bean.invokeMethod("schedule");
    }
  }
}
