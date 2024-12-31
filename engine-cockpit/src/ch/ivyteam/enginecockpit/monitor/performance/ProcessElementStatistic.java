package ch.ivyteam.enginecockpit.monitor.performance;

import ch.ivyteam.enginecockpit.monitor.trace.BackgroundMeterUtil;
import ch.ivyteam.ivy.bpm.engine.restricted.model.IProcessElement;
import ch.ivyteam.ivy.bpm.engine.restricted.statistic.IProcessElementExecutionStatistic;

@SuppressWarnings("restriction")
public final class ProcessElementStatistic {

  static final double A_MILLION = 1_000_000.0d;
  private final IProcessElementExecutionStatistic statistic;
  private final Max max;

  public ProcessElementStatistic(IProcessElementExecutionStatistic statistic, Max max) {
    this.statistic = statistic;
    this.max = max;
  }

  public long getOrder() {
    return statistic.getExecutionOrder();
  }

  public String getApplication() {
    return statistic.getApplicationName();
  }

  public String getProcessModel() {
    return statistic.getModelName();
  }

  public String getVersion() {
    return statistic.getVersionName();
  }

  public String getProcess() {
    return processElement().getTopLevelProcess().getFullQualifiedName();
  }

  public String getId() {
    return processElement().getId().getRawPid();
  }

  public String getName() {
    return processElement().getName();
  }

  public String getType() {
    return processElement().getType();
  }

  public long getInternalExecutions() {
    return statistic.getExecutions();
  }

  public String getInternalExecutionsBackground() {
    return BackgroundMeterUtil.background(getInternalExecutions(), max.getInternalExecutions());
  }

  public double getMinInternalExecutionTime() {
    return statistic.getMinProcessEngineExecutionTime() / A_MILLION;
  }

  public String getMinInternalExecutionTimeBackground() {
    return BackgroundMeterUtil.background(getMinInternalExecutionTime(), max.getMinInternalExecutionTime());
  }

  public double getAvgInternalExecutionTime() {
    return avg(getTotalInternalExecutionTime(), getInternalExecutions());
  }

  public String getAvgInternalExecutionTimeBackground() {
    return BackgroundMeterUtil.background(getAvgInternalExecutionTime(), max.getAvgInternalExecutionTime());
  }

  public double getMaxInternalExecutionTime() {
    return statistic.getMaxProcessEngineExecutionTime() / A_MILLION;
  }

  public String getMaxInternalExecutionTimeBackground() {
    return BackgroundMeterUtil.background(getMaxInternalExecutionTime(), max.getMaxInternalExecutionTime());
  }

  public double getTotalInternalExecutionTime() {
    return statistic.getProcessEngineExecutionTime() / A_MILLION;
  }

  public String getTotalInternalExecutionTimeBackground() {
    return BackgroundMeterUtil.background(getTotalInternalExecutionTime(), max.getTotalInternalExecutionTime());
  }

  public long getExternalExecutions() {
    return statistic.getBackgroundExecutions();
  }

  public String getExternalExecutionsBackground() {
    return BackgroundMeterUtil.background(getExternalExecutions(), max.getExternalExecutions());
  }

  public double getMinExternalExecutionTime() {
    return statistic.getMinBackgroundExecutionTime() / A_MILLION;
  }

  public String getMinExternalExecutionTimeBackground() {
    return BackgroundMeterUtil.background(getMinExternalExecutionTime(), max.getMinExternalExecutionTime());
  }

  public double getAvgExternalExecutionTime() {
    return avg(getTotalExternalExecutionTime(), getExternalExecutions());
  }

  public String getAvgExternalExecutionTimeBackground() {
    return BackgroundMeterUtil.background(getAvgExternalExecutionTime(), max.getAvgExternalExecutionTime());
  }

  public double getMaxExternalExecutionTime() {
    return statistic.getMaxBackgroundExecutionTime() / A_MILLION;
  }

  public String getMaxExternalExecutionTimeBackground() {
    return BackgroundMeterUtil.background(getMaxExternalExecutionTime(), max.getMaxExternalExecutionTime());
  }

  public double getTotalExternalExecutionTime() {
    return statistic.getBackgroundExecutionTime() / A_MILLION;
  }

  public String getTotalExternalExecutionTimeBackground() {
    return BackgroundMeterUtil.background(getTotalExternalExecutionTime(), max.getTotalExternalExecutionTime());
  }

  public double getTotalExecutionTime() {
    return getTotalInternalExecutionTime() + getTotalExternalExecutionTime();
  }

  public String getTotalExecutionTimeBackground() {
    return BackgroundMeterUtil.background(getTotalExecutionTime(), max.getTotalExecutionTime());
  }

  private IProcessElement processElement() {
    return statistic.getProcessElement();
  }

  private static double avg(double time, long executions) {
    if (executions == 0) {
      return 0;
    }
    return time / executions;
  }
}
