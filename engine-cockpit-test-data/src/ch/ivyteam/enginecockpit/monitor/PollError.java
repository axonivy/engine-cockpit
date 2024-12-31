package ch.ivyteam.enginecockpit.monitor;

import ch.ivyteam.ivy.process.eventstart.AbstractProcessStartEventBean;

public class PollError extends AbstractProcessStartEventBean {

  public PollError() {
    super("PollError", "Description of PollError");
  }

  @Override
  public void poll() {
    getEventBeanRuntime().poll().disable();
    throw new RuntimeException("Exception in Poll Method");
  }
}
