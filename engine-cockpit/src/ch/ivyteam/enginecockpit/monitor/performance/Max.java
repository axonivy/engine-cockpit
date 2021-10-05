package ch.ivyteam.enginecockpit.monitor.performance;

import static ch.ivyteam.enginecockpit.monitor.performance.ProcessElementStatistic.A_MILLION;

final class Max {
  private final double totalExecutionTime ;

  private final long internalExecutions;
  private final double totalInternalExecutionTime;
  private final double minInternalExecutionTime;
  private final double avgInternalExecutionTime;
  private final double maxInternalExecutionTime;

  private final long externalExecutions;
  private final double totalExternalExecutionTime;
  private final double minExternalExecutionTime;
  private final double avgExternalExecutionTime;
  private final double maxExternalExecutionTime;

  Max (long totalExecutionTime,
       long internalExecutions, long totalInternalExecutionTime, long minInternalExecutionTime, long avgInternalExecutionTime, long maxInternalExecutionTime,
       long externalExecutions, long totalExternalExecutionTime, long minExternalExecutionTime, long avgExternalExecutionTime, long maxExternalExecutionTime) {

    this.totalExecutionTime = totalExecutionTime / A_MILLION;

    this.internalExecutions = internalExecutions;
    this.totalInternalExecutionTime = totalInternalExecutionTime / A_MILLION;
    this.minInternalExecutionTime = minInternalExecutionTime / A_MILLION;
    this.avgInternalExecutionTime = avgInternalExecutionTime / A_MILLION;
    this.maxInternalExecutionTime = maxInternalExecutionTime / A_MILLION;

    this.externalExecutions = externalExecutions;
    this.totalExternalExecutionTime = totalExternalExecutionTime / A_MILLION;
    this.minExternalExecutionTime = minExternalExecutionTime / A_MILLION;
    this.avgExternalExecutionTime = avgExternalExecutionTime / A_MILLION;
    this.maxExternalExecutionTime = maxExternalExecutionTime / A_MILLION;
  }

  double getTotalExecutionTime() {
    return totalExecutionTime;
  }

  long getInternalExecutions() {
    return internalExecutions;
  }

  double getTotalInternalExecutionTime() {
    return totalInternalExecutionTime;
  }

  double getMinInternalExecutionTime() {
    return minInternalExecutionTime;
  }

  double getAvgInternalExecutionTime() {
    return avgInternalExecutionTime;
  }

  double getMaxInternalExecutionTime() {
    return maxInternalExecutionTime;
  }

  long getExternalExecutions() {
    return externalExecutions;
  }

  double getTotalExternalExecutionTime() {
    return totalExternalExecutionTime;
  }

  double getMinExternalExecutionTime() {
    return minExternalExecutionTime;
  }

  double getAvgExternalExecutionTime() {
    return avgExternalExecutionTime;
  }

  double getMaxExternalExecutionTime() {
    return maxExternalExecutionTime;
  }
}
