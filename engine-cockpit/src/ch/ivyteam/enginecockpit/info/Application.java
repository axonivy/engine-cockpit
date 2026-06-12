package ch.ivyteam.enginecockpit.info;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.ReleaseState;

public class Application {

  private final IApplication app;
  private final String name;
  private final boolean devMode;

  public Application(IApplication app) {
    this.app = app;
    this.name = app.getName();
    this.devMode = app.getSecurityContext().isDevMode();
  }

  public String getName() {
    return name;
  }

  public String getHomeUrl() {
    return AppLink.home(app).getRelative();
  }

  public String getDevWorkflowUrl() {
    return AppLink.devWorkflow(app).getRelative();
  }

  public boolean isDisabled() {
    return app.getReleaseState() != ReleaseState.RELEASED;
  }

  public boolean isDevMode() {
    return devMode;
  }
}
