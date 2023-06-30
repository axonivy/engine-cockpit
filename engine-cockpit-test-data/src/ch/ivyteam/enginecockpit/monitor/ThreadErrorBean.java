package ch.ivyteam.enginecockpit.monitor;

import ch.ivyteam.ivy.process.eventstart.AbstractProcessStartEventBean;
import ch.ivyteam.ivy.process.eventstart.IProcessStartEventBeanRuntime;

public class ThreadErrorBean extends AbstractProcessStartEventBean {

  public ThreadErrorBean() {
    super("ThreadErrorBean", "Description of ThreadErrorBean");
  }

  @Override
  public void initialize(IProcessStartEventBeanRuntime eventRuntime, String configuration) {
    super.initialize(eventRuntime, configuration);
    eventRuntime.threads().boundToEventLifecycle(() -> {
      throw new RuntimeException("Error in event bean thread");
    });
  }
}
