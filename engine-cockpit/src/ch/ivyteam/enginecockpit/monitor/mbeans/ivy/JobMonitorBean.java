package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.format;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.monitor.Series;

@ManagedBean
@ViewScoped
public class JobMonitorBean {
  private static final ObjectName JOB_MANAGER;

  static {
    try {
      JOB_MANAGER = new ObjectName("ivy Engine:type=Job Manager");
    } catch (MalformedObjectNameException ex) {
      throw new IllegalArgumentException("Wrong object name", ex);
    }
  }

  private final Monitor executionsMonitor;
  private final Monitor executionTimeMonitor;

  public JobMonitorBean() {
    executionsMonitor = Monitor.build().name("Jobs Executed").icon("dns").toMonitor();
    executionTimeMonitor = Monitor.build().name("Job Execution Time").icon("timer").yAxisLabel("Time").toMonitor();

    var jobExecutions = new ExecutionCounter(JOB_MANAGER.getCanonicalName(), "jobExecutions", "errors");

    executionsMonitor.addInfoValue(format("%5d", jobExecutions.deltaExecutions()));
    executionsMonitor.addInfoValue(format("Total %5d", jobExecutions.executions()));
    executionsMonitor.addInfoValue(format("Errors %5d", jobExecutions.deltaErrors()));
    executionsMonitor.addInfoValue(format("Errors Total %5d", jobExecutions.errors()));

    executionsMonitor.addSeries(Series.build(jobExecutions.deltaExecutions(), "Executed").toSeries());
    executionsMonitor.addSeries(Series.build(jobExecutions.deltaErrors(), "Errors").toSeries());

    executionTimeMonitor.addInfoValue(format("Min %t", jobExecutions.deltaMinExecutionTime()));
    executionTimeMonitor.addInfoValue(format("Avg %t", jobExecutions.deltaAvgExecutionTime()));
    executionTimeMonitor.addInfoValue(format("Max %t", jobExecutions.deltaMaxExecutionTime()));
    executionTimeMonitor.addInfoValue(format("Total %t", jobExecutions.executionTime()));

    executionTimeMonitor.addSeries(Series.build(jobExecutions.deltaMinExecutionTime(), "Min").toSeries());
    executionTimeMonitor.addSeries(Series.build(jobExecutions.deltaAvgExecutionTime(), "Avg").toSeries());
    executionTimeMonitor.addSeries(Series.build(jobExecutions.deltaMaxExecutionTime(), "Max").toSeries());
  }

  public Monitor getExecutionsMonitor() {
    return executionsMonitor;
  }

  public Monitor getExecutionTimeMonitor() {
    return executionTimeMonitor;
  }
}
