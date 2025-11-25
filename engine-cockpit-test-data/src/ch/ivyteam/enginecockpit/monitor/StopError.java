package ch.ivyteam.enginecockpit.monitor;

import ch.ivyteam.ivy.process.eventstart.AbstractProcessStartEventBean;
import ch.ivyteam.ivy.service.ServiceException;

public class StopError extends AbstractProcessStartEventBean {

  public StopError() {
    super("StopError", "Description of StopError");
  }

  @Override
  public void stop() throws ServiceException {
    throw new ServiceException("Exception in stop method");
  }
}
