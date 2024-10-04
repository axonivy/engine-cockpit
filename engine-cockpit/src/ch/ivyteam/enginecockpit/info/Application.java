package ch.ivyteam.enginecockpit.info;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.IApplicationInternal;
import ch.ivyteam.log.Logger;

public class Application {

  private static final Logger LOGGER = Logger.getLogger(Application.class);

  private final IApplicationInternal app;
  private final String name;
  private final String description;

  public Application(IApplication app) {
    this.app = (IApplicationInternal)app;
    this.name = app.getName();
    this.description = "Axon Ivy Application '" + app.getName() + "'";
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
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
    } catch (Exception e) {
      LOGGER.error("There was an error while try to evaluate the state of the applicaiton '" + name + "'", e);
      return true;
    }
  }
}
