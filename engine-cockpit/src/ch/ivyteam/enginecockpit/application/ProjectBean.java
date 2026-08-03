package ch.ivyteam.enginecockpit.application;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import ch.ivyteam.enginecockpit.application.model.ProjectStateDto;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.util.DateUtil;
import ch.ivyteam.ivy.application.app.ApplicationRepository;
import ch.ivyteam.ivy.application.project.MavenCoordinates;
import ch.ivyteam.ivy.application.project.Project;
import ch.ivyteam.ivy.project.model.ProjectVersion;
import ch.ivyteam.ivy.security.ISecurityContextRepository;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.ws.rs.core.UriBuilder;

@Named
@ViewScoped
public class ProjectBean implements Serializable {

  private String context;
  private String appName;
  private int version;
  private String projectName;

  private ProjectDto project;
  private String deployedProject;
  private List<ProjectDto> dependentProjects;
  private List<ProjectDto> requiredProjects;

  public void setProjectName(String project) {
    this.projectName = project;
  }

  public String getProjectName() {
    return projectName;
  }

  public String getContext() {
    return context;
  }

  public void setContext(String context) {
    this.context = context;
  }

  public void setApp(String app) {
    this.appName = app;
  }

  public String getApp() {
    return appName;
  }

  public void setVersion(String version) {
    this.version = Integer.parseInt(version);
  }

  public String getVersion() {
    return String.valueOf(version);
  }

  public void onload() {
    var securityContext = ISecurityContextRepository.instance().get(context);
    if (securityContext == null) {
      ResponseHelper.notFound("Security context not found: " + context);
      return;
    }

    var app = ApplicationRepository.of(securityContext).findByNameAndVersion(appName, version).orElse(null);
    if (app == null) {
      ResponseHelper.notFound("Application '" + appName + "' with version '" + version + "' not found");
      return;
    }

    var iProject = app.projects().find(projectName);
    if (iProject == null) {
      ResponseHelper.notFound("Process Model Version '" + projectName + "' for version '" + version + "' in app '"
          + appName + "' not found");
      return;
    }

    project = new ProjectDto(iProject);
    dependentProjects = iProject.allDependentProjects()
        .map(ProjectDto::new)
        .collect(Collectors.toList());
    requiredProjects = iProject.allRequiredProjects()
        .map(ProjectDto::new)
        .collect(Collectors.toList());
  }

  public ProjectDto getProject() {
    return project;
  }

  public static String getLink(String context, String app, int version, String project) {
    return UriBuilder.fromPath("project.xhtml")
        .queryParam("context", context)
        .queryParam("app", app)
        .queryParam("version", version)
        .queryParam("project", project)
        .build()
        .toString();
  }

  public String getLink() {
    return getLink(context, appName, version, projectName);
  }

  public String getApplicationLink() {
    return ApplicationBean.getLink(context, appName);
  }

  public String getApplicationVersionLink() {
    return ApplicationVersionBean.getLink(context, appName, version);
  }

  public String getDeployedProject() {
    return deployedProject;
  }

  public List<ProjectDto> getDependentProjects() {
    return dependentProjects;
  }

  public List<ProjectDto> getRequiredProjects() {
    return requiredProjects;
  }

  public static class ProjectDto {

    private final Project project;
    private final MavenCoordinates mavenCoordinates;
    private final String lastChanged;
    private final ProjectStateDto state; 

    public ProjectDto(Project project) {
      lastChanged = DateUtil.formatDate(project.getLastChangeDate());
      this.project = project;
      this.mavenCoordinates = project.mavenCoordinates();
      this.state = new ProjectStateDto(project.state());
    }

    public String getName() {
      return project.name();
    }

    public ProjectStateDto getState() {
      return state;
    }

    public String getLink() {
      return ProjectBean.getLink(
          project.app().securityContext().name(),
          project.app().name(),
          project.app().version(),
          project.name());
    }

    public String getMavenVersion() {
      return mavenCoordinates.version();
    }

    public String getMavenId() {
      return mavenCoordinates.id();
    }

    public int getProjectVersion() {
      return ProjectVersion.of(project.model()).version();
    }

    public String getLastChanged() {
      return lastChanged;
    }
  }
}
