package ch.ivyteam.enginecockpit.monitor.events;

import java.util.List;
import java.util.stream.Collectors;

import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.mbeans.MBean;
import ch.ivyteam.enginecockpit.util.ErrorHandler;
import ch.ivyteam.enginecockpit.util.ErrorValue;
import ch.ivyteam.log.Logger;

public abstract class Event {

  private static final Logger LOGGER = Logger.getPackageLogger(Event.class);
  private static final ErrorHandler HANDLER = new ErrorHandler("msgs", LOGGER);
  protected MBean bean;
  private List<Firing> firings;
  private List<EventBeanThread> threads;

  public Event(ObjectName name) {
    bean = MBean.create(HANDLER, name);
  }

  public abstract long getExecutions();

  public abstract long getErrors();

  public abstract String getFullRequestPath();

  public abstract boolean showExecutionDuration();

  public String getName() {
    return bean.getNameKeyProperty("name");
  }

  public String getBeanName() {
    return bean.readAttribute("name").asString();
  }

  public String getBeanDescription() {
    return bean.readAttribute("description").asString();
  }

  public String getApplication() {
    return bean.getNameKeyProperty("application");
  }

  public String getPm() {
    return bean.getNameKeyProperty("pm");
  }

  public String getPmv() {
    return bean.getNameKeyProperty("pmv");
  }

  public String getBeanClass() {
    return bean.readAttribute("beanClass").asString();
  }

  public String getBeanConfiguration() {
    var entries = bean.readAttribute("beanConfiguration")
      .asList(item -> (String)item.get("key")+"="+item.get("value"));
    return entries.stream().collect(Collectors.joining("\n"));
  }

  public ErrorValue getLastPollError() {
    return bean.readAttribute("lastPollError").asError();
  }

  public boolean isRunning() {
    return bean.readAttribute("running").asBoolean();
  }

  public String getServiceState() {
    return bean.readAttribute("serviceState").asString();
  }

  public ErrorValue getLastInitializationError() {
    return bean.readAttribute("lastInitializationError").asError();
  }

  public String getLastStartTimestamp() {
    return bean.readAttribute("lastStartTimestamp").asDateString();
  }

  public ErrorValue getLastStartError() {
    return bean.readAttribute("lastStartError").asError();
  }

  public ErrorValue getLastStopError() {
    return bean.readAttribute("lastStopError").asError();
  }

  public long getPolls() {
    return bean.readAttribute("polls").asLong();
  }

  public long getPollErrors() {
    return bean.readAttribute("pollErrors").asLong();
  }

  public String getPollConfiguration() {
    return bean.readAttribute("pollConfiguration").asString();
  }

  public String getHumanReadablePollConfiguration() {
    return bean.readAttribute("humanReadablePollConfiguration").asString();
  }

  public String getNextPollTime() {
    return bean.readAttribute("nextPollTime").asDateString();
  }

  public long getTimeUntilNextPoll() {
    return bean.readAttribute("timeUntilNextPoll").asLong();
  }

  public String getTimeUntilNextPollFormated() {
    return bean.readAttribute("timeUntilNextPoll").asMillis();
  }

  public String getMinPollTime() {
    return bean.readAttribute("polls").asMinExecutionTime();
  }

  public String getAvgPollTime() {
    return bean.readAttribute("polls").asAvgExecutionTime();
  }

  public String getMaxPollTime() {
    return bean.readAttribute("polls").asMaxExecutionTime();
  }

  public void poll() {
    bean.invokeMethod("pollNow");
  }

  public void start() {
    bean.invokeMethod("start");
  }

  public void stop() {
    bean.invokeMethod("stop");
  }


  public void refresh() {
    firings = bean.readAttribute("firingHistory").asList(Firing::new);
    threads = bean.readAttribute("threads").asList(EventBeanThread::new);
  }

  public List<Firing> getFirings() {
    return firings;
  }
  
  public List<EventBeanThread> getThreads() {
    return threads;
  }
}
