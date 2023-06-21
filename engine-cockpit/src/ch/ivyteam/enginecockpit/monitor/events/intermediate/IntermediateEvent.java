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
    return bean.readAttribute("lastFiringTimestamp").asDateString();
  }

  public String getProcessElementId() {
    return bean.readAttribute("processElementId").asString();
  }

  @Override
  public long getExecutions() {
    return bean.readAttribute("firings").asLong();
  }

  @Override
  public boolean showExecutionDuration() {
    return false;
  }

  @Override
  public long getErrors() {
    return bean.readAttribute("firingErrors").asLong();
  }
}
