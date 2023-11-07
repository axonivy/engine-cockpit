package ch.ivyteam.enginecockpit.monitor;

import ch.ivyteam.ivy.process.eventstart.AbstractProcessStartEventBean;
import ch.ivyteam.ivy.process.eventstart.IProcessStartEventBeanRuntime;
import ch.ivyteam.ivy.process.extension.ProgramConfig;

public class ThreadErrorBean extends AbstractProcessStartEventBean {

  public ThreadErrorBean() {
    super("ThreadErrorBean", "Description of ThreadErrorBean");
  }

  @Override
  public void initialize(IProcessStartEventBeanRuntime eventRuntime, ProgramConfig configuration) {
    super.initialize(eventRuntime, configuration);
    eventRuntime.threads().boundToEventLifecycle(() -> {
      throw new RuntimeException("Error in event bean thread");
    });
  }
}
