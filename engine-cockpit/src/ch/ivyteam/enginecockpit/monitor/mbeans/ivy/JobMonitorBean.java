package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.format;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.monitor.Series;
import ch.ivyteam.enginecockpit.util.CmsUtil;
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
        .addInfoValue(format(CmsUtil.coWithDefault("/common/Total", "Total") + " %5d", jobExecutions.executions()));
    executionsMonitor
        .addInfoValue(format(CmsUtil.coWithDefault("/common/Errors", "Errors") + " %5d", jobExecutions.deltaErrors()));
    executionsMonitor
        .addInfoValue(format(CmsUtil.coWithDefault("/common/ErrorsTotal", "Errors Total") + " %5d", jobExecutions.errors()));

    executionsMonitor
        .addSeries(Series.build(jobExecutions.deltaExecutions(), Ivy.cm().co("/liveStats/Executed")).toSeries());
    executionsMonitor.addSeries(Series.build(jobExecutions.deltaErrors(), Ivy.cm().co("/common/Errors")).toSeries());

    executionTimeMonitor.addInfoValue(
        format(CmsUtil.coWithDefault("/liveStats/Min", "Min") + " %t", jobExecutions.deltaMinExecutionTime()));
    executionTimeMonitor.addInfoValue(
        format(CmsUtil.coWithDefault("/liveStats/Avg", "Avg") + " %t", jobExecutions.deltaAvgExecutionTime()));
    executionTimeMonitor.addInfoValue(
        format(CmsUtil.coWithDefault("/liveStats/Max", "Max") + " %t", jobExecutions.deltaMaxExecutionTime()));
    executionTimeMonitor
        .addInfoValue(format(CmsUtil.coWithDefault("/common/Total", "Total") + " %t", jobExecutions.executionTime()));

    executionTimeMonitor.addSeries(Series
        .build(jobExecutions.deltaMinExecutionTime(), CmsUtil.coWithDefault("/liveStats/Min", "Min")).toSeries());
    executionTimeMonitor.addSeries(Series
        .build(jobExecutions.deltaAvgExecutionTime(), CmsUtil.coWithDefault("/liveStats/Avg", "Avg")).toSeries());
    executionTimeMonitor.addSeries(Series
        .build(jobExecutions.deltaMaxExecutionTime(), CmsUtil.coWithDefault("/liveStats/Max", "Max")).toSeries());
  }

  public Monitor getExecutionsMonitor() {
    return executionsMonitor;
  }

  public Monitor getExecutionTimeMonitor() {
    return executionTimeMonitor;
  }
}
