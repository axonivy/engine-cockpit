package ch.ivyteam.enginecockpit.monitor;

import ch.ivyteam.ivy.process.eventstart.AbstractProcessStartEventBean;
import ch.ivyteam.ivy.service.ServiceException;

public class StartError extends AbstractProcessStartEventBean {

  public StartError() {
    super("StartError", "Description of StartError");
  }

  @Override
  public void start() throws ServiceException {
    throw new ServiceException("Exception in start method");
  }
}
