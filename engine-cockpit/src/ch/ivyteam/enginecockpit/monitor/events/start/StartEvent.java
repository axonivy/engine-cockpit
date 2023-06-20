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
    return bean.readAttribute("requestPath").asString();
  }

  @Override
  public long getExecutions() {
    return bean.readAttribute("processExecutions").asLong();
  }

  @Override
  public boolean showExecutionDuration() {
    return true;
  }

  @Override
  public long getErrors() {
    return bean.readAttribute("processExecutionErrors").asLong();
  }

  public String getMinExecutionTime() {
    return bean.readAttribute("processExecutions").asMinExecutionTime();
  }

  public String getAvgExecutionTime() {
    return bean.readAttribute("processExecutions").asAvgExecutionTime();
  }

  public String getMaxExecutionTime() {
    return bean.readAttribute("processExecutions").asMaxExecutionTime();
  }
}
