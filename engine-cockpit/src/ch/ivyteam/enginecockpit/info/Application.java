package ch.ivyteam.enginecockpit.info;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.app.link.AppLink;

public record Application(String name, boolean devMode, String homeUrl, String devWorkflowUrl) {

  public Application(IApplication app) {
    var name = app.name();
    var devMode = app.securityContext().isDevMode();
    var homeUrl = AppLink.home(app).getRelative();
    var devWorkflowUrl = AppLink.devWorkflow(app).getRelative();
    this(name, devMode, homeUrl, devWorkflowUrl);
  }

  public String getName() {
    return name;
  }

  public String getHomeUrl() {
    return homeUrl;
  }

  public String getDevWorkflowUrl() {
    return devWorkflowUrl;
  }

  public boolean isDevMode() {
    return devMode;
  }
}
