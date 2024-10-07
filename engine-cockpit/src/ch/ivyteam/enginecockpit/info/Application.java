package ch.ivyteam.enginecockpit.info;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.IApplicationInternal;
import ch.ivyteam.log.Logger;

public class Application {

  private static final Logger LOGGER = Logger.getLogger(Application.class);

  private final IApplicationInternal app;
  private final String name;

  public Application(IApplication app) {
    this.app = (IApplicationInternal)app;
    this.name = app.getName();
  }

  public String getName() {
    return name;
  }

  public String getHomeUrl() {
    return app.getHomeLink().getRelative();
  }

  public String getDevWorkflowUrl() {
    return app.getDevWorkflowLink().getRelative();
  }

  public boolean isDisabled() {
    try {
      return !app.hasAnyActiveAndReleasedPmv();
    } catch (Exception ex) {
      LOGGER.error("Error while try to evaluate the state of the applicatioon '" + name + "'", ex);
      return true;
    }
  }
}
