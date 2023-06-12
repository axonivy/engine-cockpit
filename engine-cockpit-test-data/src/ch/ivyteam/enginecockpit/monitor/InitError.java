package ch.ivyteam.enginecockpit.monitor;

import ch.ivyteam.ivy.process.eventstart.AbstractProcessStartEventBean;
import ch.ivyteam.ivy.process.eventstart.IProcessStartEventBeanRuntime;


public class InitError extends AbstractProcessStartEventBean {

  /**
   * Constructor
   */
  public InitError() {
    super("InitError", "Description of InitError");
  }

  @Override
  public void initialize(IProcessStartEventBeanRuntime eventRuntime, String configuration) {
    throw new RuntimeException(""
            + "Exception in initialize method");
  }
}
