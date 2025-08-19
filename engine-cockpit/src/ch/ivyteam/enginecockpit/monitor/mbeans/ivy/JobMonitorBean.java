package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.format;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.monitor.Series;
import ch.ivyteam.ivy.environment.Ivy;

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
    executionsMonitor = Monitor.build().name(Ivy.cm().co("/liveStats/JobsExecuted")).icon("dns").toMonitor();
    executionTimeMonitor = Monitor.build().name(Ivy.cm().co("/liveStats/JobExecutionTime")).icon("timer")
        .yAxisLabel(Ivy.cm().co("/common/Time")).toMonitor();

    var jobExecutions = new ExecutionCounter(JOB_MANAGER.getCanonicalName(), "jobExecutions", "errors");

    executionsMonitor.addInfoValue(format("%5d", jobExecutions.deltaExecutions()));
    executionsMonitor
        .addInfoValue(format(Ivy.cm().co("/common/Total") + " %5d", jobExecutions.executions()));
    executionsMonitor
        .addInfoValue(format(Ivy.cm().co("/common/Errors") + " %5d", jobExecutions.deltaErrors()));
    executionsMonitor
        .addInfoValue(format(Ivy.cm().co("/common/ErrorsTotal") + " %5d", jobExecutions.errors()));

    executionsMonitor
        .addSeries(Series.build(jobExecutions.deltaExecutions(), Ivy.cm().co("/liveStats/Executed")).toSeries());
    executionsMonitor.addSeries(Series.build(jobExecutions.deltaErrors(), Ivy.cm().co("/common/Errors")).toSeries());

    executionTimeMonitor.addInfoValue(
        format(Ivy.cm().co("/liveStats/Min") + " %t", jobExecutions.deltaMinExecutionTime()));
    executionTimeMonitor.addInfoValue(
        format(Ivy.cm().co("/liveStats/Avg") + " %t", jobExecutions.deltaAvgExecutionTime()));
    executionTimeMonitor.addInfoValue(
        format(Ivy.cm().co("/liveStats/Max") + " %t", jobExecutions.deltaMaxExecutionTime()));
    executionTimeMonitor
        .addInfoValue(format(Ivy.cm().co("/common/Total") + " %t", jobExecutions.executionTime()));

    executionTimeMonitor.addSeries(Series
        .build(jobExecutions.deltaMinExecutionTime(), Ivy.cm().co("/liveStats/Min")).toSeries());
    executionTimeMonitor.addSeries(Series
        .build(jobExecutions.deltaAvgExecutionTime(), Ivy.cm().co("/liveStats/Avg")).toSeries());
    executionTimeMonitor.addSeries(Series
        .build(jobExecutions.deltaMaxExecutionTime(), Ivy.cm().co("/liveStats/Max")).toSeries());
  }

  public Monitor getExecutionsMonitor() {
    return executionsMonitor;
  }

  public Monitor getExecutionTimeMonitor() {
    return executionTimeMonitor;
  }
}
