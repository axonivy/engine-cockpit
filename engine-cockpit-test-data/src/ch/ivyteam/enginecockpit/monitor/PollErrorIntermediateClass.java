package ch.ivyteam.enginecockpit.monitor;

import ch.ivyteam.ivy.process.intermediateevent.AbstractProcessIntermediateEventBean;
import ch.ivyteam.ivy.process.intermediateevent.IProcessIntermediateEventBeanRuntime;

public class PollErrorIntermediateClass extends AbstractProcessIntermediateEventBean {

  public PollErrorIntermediateClass() {
    super("PollErrorIntermediateClass", "This IntermediateClass throws an error in poll method", String.class);
  }

  @Override
  public void initialize(IProcessIntermediateEventBeanRuntime eventRuntime, String configuration) {
    super.initialize(eventRuntime, configuration);
    eventRuntime.threads().boundToEventLifecycle(() -> {});
    
  }
  @Override
  public void poll() {
    throw new RuntimeException("Exception in Poll Method");
  }
}
