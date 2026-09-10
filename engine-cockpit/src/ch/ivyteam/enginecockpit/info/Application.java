package ch.ivyteam.enginecockpit.info;

import ch.ivyteam.ivy.application.app.link.AppLink;

public record Application(String name, String homeUrl, String devWorkflowUrl) {

  public Application(ch.ivyteam.ivy.application.app.Application app) {
    var name = app.name();
    var homeUrl = AppLink.home(app).getRelative();
    var devWorkflowUrl = AppLink.devWorkflow(app).getRelative();
    this(name, homeUrl, devWorkflowUrl);
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
}
