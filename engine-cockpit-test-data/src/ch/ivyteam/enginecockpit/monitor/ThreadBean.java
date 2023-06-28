/**
 * 
 */
package ch.ivyteam.enginecockpit.monitor;

import ch.ivyteam.ivy.process.eventstart.AbstractProcessStartEventBean;
import ch.ivyteam.ivy.process.eventstart.IProcessStartEventBeanRuntime;

public class ThreadBean extends AbstractProcessStartEventBean {

  public ThreadBean() {
    super("ThreadBean", "Description of ThreadBean");
  }
  
  @Override
  public void initialize(IProcessStartEventBeanRuntime eventRuntime, String configuration) {
    super.initialize(eventRuntime, configuration);
    eventRuntime.threads().boundToEventLifecycle(() -> {
      synchronized(this) {
        try {
          wait();
        } catch (InterruptedException e) {
        }
      }
    });
  }
}
