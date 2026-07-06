package ch.ivyteam.enginecockpit.application;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.util.DateUtil;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.project.model.ProjectVersion;
import ch.ivyteam.ivy.security.ISecurityContextRepository;
import ch.ivyteam.ivy.workflow.IWorkflowContext;

@Named
@ViewScoped
public class ProjectBean implements Serializable {

  private String securityContextName;
  private String appName;
  private String appVersion;
  private String project;
  private ProjectDto pmv;
  private String deployedProject;
  private List<ProjectDto> dependendPmvs;
  private List<ProjectDto> requriedPmvs;

  public void setProject(String project) {
    this.project = project;
  }

  public String getProject() {
    return project;
  }

  public String getContext() {
    return securityContextName;
  }

  public void setContext(String securityContextName) {
    this.securityContextName = securityContextName;
  }

  public void setApp(String appName) {
    this.appName = appName;
  }

  public String getApp() {
    return appName;
  }

  public void setVersion(String appVersion) {
    this.appVersion = appVersion;
  }

  public String getVersion() {
    return appVersion;
  }

  public void onload() {
    var securityContext = ISecurityContextRepository.instance().all().stream()
        .filter(context -> securityContextName.equals(context.getName()))
        .findAny()
        .orElse(null);
    if (securityContext == null) {
      ResponseHelper.notFound("Security context not found: " + securityContextName);
      return;
    }

    var apps = IApplicationRepository.of(securityContext);
    var app = apps.all().stream()
        .filter(a -> a.getName().equals(appName))
        .filter(a -> String.valueOf(a.getVersion()).equals(appVersion))
        .findAny()
        .orElse(null);
    if (app == null) {
      ResponseHelper.notFound("Application '" + appName + "' with version '" + appVersion + "' not found");
      return;
    }

    var iPmv = app.findProcessModelVersion(project);
    if (iPmv == null) {
      ResponseHelper.notFound("Process Model Version '" + project + "' for version '" + appVersion + "' in app '"
          + appName + "' not found");
      return;
    }

    pmv = new ProjectDto(iPmv);
    dependendPmvs = iPmv.getAllDependentProcessModelVersions()
        .map(ProjectDto::new)
        .collect(Collectors.toList());
    requriedPmvs = iPmv.getAllRequiredProcessModelVersions()
        .map(ProjectDto::new)
        .collect(Collectors.toList());
  }

  public ProjectDto getPmv() {
    return pmv;
  }

  public String getDeployedProject() {
    return deployedProject;
  }

  public List<ProjectDto> getDependentPmvs() {
    return dependendPmvs;
  }

  public List<ProjectDto> getRequiredPmvs() {
    return requriedPmvs;
  }

  public String getApplicationDetailLink() {
    return ApplicationDetailLink.getApplicationDetailLink(appName, securityContextName);
  }

  public static class ProjectDto {

    private final IProcessModelVersion pmv;
    private final String lastChanged;
    private int runningCasesCount = -1;

    public ProjectDto(IProcessModelVersion pmv) {
      this(pmv, null);
    }

    public String getName() {
      return pmv.getName();
    }

    public ProjectDto(IProcessModelVersion pmv, ApplicationsBean bean) {
      lastChanged = DateUtil.formatDate(pmv.getLastChangeDate());
      this.pmv = pmv;
    }

    public String getDetailView() {
      return ApplicationDetailLink.getProjectLink(pmv.getApplication().getName(),
          pmv.getApplication().getSecurityContext().getName(), pmv.getApplication().getVersion(), pmv.getName());
    }

    public long getRunningCasesCount() {
      countRunningCases();
      return runningCasesCount;
    }

    public String getIcon() {
      return "ti ti-packages";
    }

    public String getQualifiedVersion() {
      return pmv.getLibraryVersion();
    }

    public String getLastChanged() {
      return lastChanged;
    }

    public String getLibraryId() {
      return pmv.getLibraryId();
    }

    public String getLibraryVersion() {
      return pmv.getLibraryVersion();
    }

    public int getProjectVersion() {
      return ProjectVersion.of(pmv.project()).version();
    }

    private void countRunningCases() {
      if (pmv != null && runningCasesCount < 0) {
        runningCasesCount = IWorkflowContext.current().getRunningCasesCount(pmv);
      }
    }
  }
}
