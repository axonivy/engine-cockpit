package ch.ivyteam.enginecockpit.monitor.performance;

import ch.ivyteam.ivy.bpm.engine.restricted.statistic.IProcessElementExecutionStatistic;

@SuppressWarnings("restriction")
final class MaxBuilder {
  private long totalExecutionTime;

  private long internalExecutions;
  private long internalExecutionTime;
  private long minInternalExecutionTime;
  private long avgInternalExecutionTime;
  private long maxInternalExecutionTime;

  private long externalExecutions;
  private long externalExecutionTime;
  private long minExternalExecutionTime;
  private long avgExternalExecutionTime;
  private long maxExternalExecutionTime;

  void add(IProcessElementExecutionStatistic stat) {
    totalExecutionTime = Math.max(totalExecutionTime, stat.getProcessEngineExecutionTime() + stat.getBackgroundExecutionTime());
    internalExecutions = Math.max(internalExecutions, stat.getExecutions());
    internalExecutionTime = Math.max(internalExecutionTime, stat.getProcessEngineExecutionTime());
    minInternalExecutionTime = Math.max(minInternalExecutionTime, stat.getMinProcessEngineExecutionTime());
    maxInternalExecutionTime = Math.max(maxInternalExecutionTime, stat.getMaxProcessEngineExecutionTime());
    avgInternalExecutionTime = Math.max(avgInternalExecutionTime, avg(stat.getProcessEngineExecutionTime(), stat.getExecutions()));
    externalExecutions = Math.max(externalExecutions, stat.getBackgroundExecutions());
    externalExecutionTime = Math.max(externalExecutionTime, stat.getBackgroundExecutionTime());
    minExternalExecutionTime = Math.max(minExternalExecutionTime, stat.getMinBackgroundExecutionTime());
    maxExternalExecutionTime = Math.max(maxExternalExecutionTime, stat.getMaxBackgroundExecutionTime());
    avgExternalExecutionTime = Math.max(avgExternalExecutionTime, avg(stat.getBackgroundExecutionTime(), stat.getBackgroundExecutions()));
  }

  Max toMax() {
    return new Max(
        totalExecutionTime,
        internalExecutions, internalExecutionTime, minInternalExecutionTime, avgInternalExecutionTime, maxInternalExecutionTime,
        externalExecutions, externalExecutionTime, minExternalExecutionTime, avgExternalExecutionTime, maxExternalExecutionTime);
  }

  private static long avg(long time, long executions) {
    if (executions == 0) {
      return 0;
    }
    return time / executions;
  }
}
