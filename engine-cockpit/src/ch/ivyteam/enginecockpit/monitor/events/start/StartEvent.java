package ch.ivyteam.enginecockpit.monitor.events.start;

import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.events.Event;

public final class StartEvent extends Event {

  public StartEvent(ObjectName name) {
    super(name);
  }

  @Override
  public String getFullRequestPath() {
    return getApplication() +
           "/"+
           getPm() +
           "$" +
           getPmv() +
           "/" +
           getRequestPath();
  }

  public String getRequestPath() {
    return readStringAttribute("requestPath");
  }

  @Override
  public long getExecutions() {
    return readLongAttribute("processExecutions");
  }

  @Override
  public long getErrors() {
    return readLongAttribute("processExecutionErrors");
  }

  public String getMinExecutionTime() {
    long executions = getExecutions();
    if (executions == 0) {
      return NOT_AVAILABLE;
    }
    return formatMicros((Long) readAttribute("processExecutionsMinExecutionTimeInMicroSeconds"));
  }

  public String getAvgExecutionTime() {
    var total = (Long) readAttribute("processExecutionsTotalExecutionTimeInMicroSeconds");
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
    return formatMicros((Long) readAttribute("processExecutionsMaxExecutionTimeInMicroSeconds"));
  }
}
