package ch.ivyteam.enginecockpit.application;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.util.DateUtil;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.app.ApplicationRepository;
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
    dependentProjects = iProject.getAllDependentProcessModelVersions()
        .map(ProjectDto::new)
        .collect(Collectors.toList());
    requiredProjects = iProject.getAllRequiredProcessModelVersions()
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

    private final IProcessModelVersion project;
    private final String lastChanged;

    public ProjectDto(IProcessModelVersion project) {
      lastChanged = DateUtil.formatDate(project.getLastChangeDate());
      this.project = project;
    }

    public String getName() {
      return project.getName();
    }

    public String getLink() {
      return ProjectBean.getLink(
          project.getApplication().securityContext().name(),
          project.getApplication().name(),
          project.getApplication().version(),
          project.getName());
    }

    public String getQualifiedVersion() {
      return project.getLibraryVersion();
    }

    public String getLastChanged() {
      return lastChanged;
    }

    public String getLibraryId() {
      return project.getLibraryId();
    }

    public String getLibraryVersion() {
      return project.getLibraryVersion();
    }

    public int getProjectVersion() {
      return ProjectVersion.of(project.project()).version();
    }
  }
}
