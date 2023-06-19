package ch.ivyteam.enginecockpit.monitor.events.intermediate;

import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.events.Event;

public final class IntermediateEvent extends Event {

  public IntermediateEvent(ObjectName name) {
    super(name);
  }

  @Override
  public String getFullRequestPath() {
    return getApplication() +
            "/" +
            getPm() +
            "$" +
            getPmv() +
            "/" +
            getProcessElementId();
  }

  public String getLastFiringTimestamp() {
    return getDateAttribute("lastFiringTimestamp");
  }

  public String getProcessElementId() {
    return readStringAttribute("processElementId");
  }

  @Override
  public long getExecutions() {
    return readLongAttribute("firings");
  }

  @Override
  public boolean showExecutionDuration() {
    return false;
  }

  @Override
  public long getErrors() {
    return readLongAttribute("firingErrors");
  }

  public String getMinExecutionTime() {
    long executions = getExecutions();
    if (executions == 0) {
      return NOT_AVAILABLE;
    }
    return formatMicros((Long) readAttribute("pollsMinExecutionTimeInMicroSeconds"));
  }

  public String getAvgExecutionTime() {
    var total = (Long) readAttribute("pollsTotalExecutionTimeInMicroSeconds");
    if (total == null) {
      return formatMicros(total);
    }
    long executions = getExecutions();
    if (executions == 0) {
      return NOT_AVAILABLE;
    }
    return formatMicros(total / executions);
  }

  public String getMaxExecutionTime() {
    long executions = getExecutions();
    if (executions == 0) {
      return NOT_AVAILABLE;
    }
    return formatMicros((Long) readAttribute("pollsMaxExecutionTimeInMicroSeconds"));
  }
}
