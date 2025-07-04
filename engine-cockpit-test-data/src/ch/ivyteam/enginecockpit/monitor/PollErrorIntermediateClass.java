package ch.ivyteam.enginecockpit.monitor;

import ch.ivyteam.ivy.process.extension.ProgramConfig;
import ch.ivyteam.ivy.process.intermediateevent.AbstractProcessIntermediateEventBean;
import ch.ivyteam.ivy.process.intermediateevent.IProcessIntermediateEventBeanRuntime;

public class PollErrorIntermediateClass extends AbstractProcessIntermediateEventBean {

  public PollErrorIntermediateClass() {
    super("PollErrorIntermediateClass", "This IntermediateClass throws an error in poll method", String.class);
  }

  @Override
  public void initialize(IProcessIntermediateEventBeanRuntime eventRuntime, ProgramConfig configuration) {
    super.initialize(eventRuntime, configuration);
    eventRuntime.threads().boundToEventLifecycle(() -> {});

  }

  @Override
  public void poll() {
    getEventBeanRuntime().poll().disable();
    throw new RuntimeException("Exception in Poll Method");
  }
}
