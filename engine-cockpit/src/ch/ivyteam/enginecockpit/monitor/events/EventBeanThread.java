package ch.ivyteam.enginecockpit.monitor.events;

import javax.management.openmbean.CompositeData;

import ch.ivyteam.enginecockpit.util.ErrorValue;

public class EventBeanThread {
  
  private final String name;
  private final Long javaThreadId;
  private final String state;
  private final ErrorValue lastError;

  public EventBeanThread(CompositeData thread) {
    this.name = (String)thread.get("name");
    this.javaThreadId = (Long)thread.get("javaThreadId");
    this.state = (String)thread.get("state");
    this.lastError = new ErrorValue((CompositeData) thread.get("lastError"));
  }

  public Long getJavaThreadId() {
    return javaThreadId;
  }

  public String getState() {
    return state;
  }

  public String getName() {
    return name;
  }

  public ErrorValue getLastError() {
    return lastError;
  }
  
  public boolean getIsRunning() {
    return "RUNNING".equals(state);
  }
}
