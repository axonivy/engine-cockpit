package ch.ivyteam.enginecockpit.monitor;

import ch.ivyteam.ivy.process.eventstart.AbstractProcessStartEventBean;
import ch.ivyteam.ivy.process.eventstart.IProcessStartEventBeanRuntime;
import ch.ivyteam.ivy.process.extension.ProgramConfig;

public class ThreadBean extends AbstractProcessStartEventBean {

  public ThreadBean() {
    super("ThreadBean", "Description of ThreadBean");
  }

  @Override
  public void initialize(IProcessStartEventBeanRuntime eventRuntime, ProgramConfig configuration) {
    super.initialize(eventRuntime, configuration);
    eventRuntime.threads().boundToEventLifecycle(() -> {
      synchronized (this) {
        try {
          wait();
        } catch (InterruptedException e) {}
      }
    });
  }
}
