package ch.ivyteam.enginecockpit.application;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import ch.ivyteam.enginecockpit.application.model.StateOfActivity;
import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.enginecockpit.util.DateUtil;
import ch.ivyteam.ivy.application.ActivityState;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.ReleaseState;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.application.app.link.AppLink;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityContextRepository;

@Named
@ViewScoped
public class ApplicationVersionBean {

  private String securityContextName;
  private String appName;
  private IApplication app;
  private Integer appVersion;

  private final ManagerBean managerBean;
  private List<ProjectRow> projects;
  private String fileDir;
  private ISecurityContext securityContext;
  private StateOfActivity state;

  public ApplicationVersionBean() {
    managerBean = ManagerBean.instance();
  }

  public void setSecurityContextName(String securityContextName) {
    this.securityContextName = securityContextName;
  }

  public String getSecurityContextName() {
    return securityContextName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppVersion(Integer appVersion) {
    this.appVersion = appVersion;
  }

  public Integer getAppVersion() {
    return appVersion;
  }

  public void onload() {
    securityContext = ISecurityContextRepository.instance().all().stream()
        .filter(context -> securityContextName.equals(context.getName()))
        .findAny()
        .orElse(null);
    if (securityContext == null) {
      ResponseHelper.notFound("Security context not found: " + securityContextName);
      return;
    }

    app =  IApplicationRepository.of(securityContext).all().stream()
        .filter(app -> app.getName().equals(appName))
        .filter(app -> appVersion.equals(app.getVersion()))
        .findAny()
        .orElse(null);

    if (app == null) {
      ResponseHelper.notFound("Application not found: " + appName);
      return;
    }

    if (appVersion == null) {
      appVersion = app.getVersion();
    }

    projects = app.getProcessModelVersions()
        .map(pmv -> new ProjectRow(
            pmv.getName(),
            pmv.getLibraryVersion(),
            DateUtil.formatDate(pmv.getLastChangeDate())))
        .sorted(Comparator.comparing(ProjectRow::getName, String.CASE_INSENSITIVE_ORDER))
        .collect(Collectors.toList());
  }

  public List<ProjectRow> getProjectRows() {
    return projects;
  }

  public IApplication getApplication() {
    return app;
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

  public String getFileDir() {
    return fileDir;
  }

  public void setFileDir(String fileDir) {
    this.fileDir = fileDir;
  }

  public SecuritySystem getSecuritySystem() {
    return new SecuritySystem(securityContext);
  }

  public boolean isNotStartable() {
    return app.getActivityState() == ActivityState.ACTIVE;
  }

  public boolean isNotStopable() {
    return app.getActivityState() == ActivityState.INACTIVE;
  }

  public StateOfActivity getState() {
    if (state == null) {
      updateState();
    }
    return state;
  }

  public void updateStats() {
    if (securityContext == null || appName == null || appVersion == null) {
      return;
    }

    app = IApplicationRepository.of(securityContext).all().stream()
        .filter(app -> app.getName().equals(appName))
        .filter(app -> appVersion.equals(app.getVersion()))
        .findAny()
        .orElse(app);

    updateState();
  }

  private void updateState() {
    if (app == null) {
      state = new StateOfActivity();
      return;
    }

    state = new StateOfActivity(app);
    state.updateReleaseState(app.getReleaseState());
  }


  public void deleteProject(String projectName) {
    if (app == null || projectName == null || projectName.isBlank()) {
      return;
    }

    var project = app.findProcessModelVersion(projectName);
    if (project == null) {
      Message.error()
          .clientId("applicationMessage")
          .summary(Ivy.cm().co("/common/Error"))
          .detail("Project '" + projectName + "' not found.")
          .show();

      return;
    }

    try {
      app.delete(project);
      managerBean.reloadApplications();
      Message.info()
          .clientId("applicationMessage")
          .summary(Ivy.cm().co("/common/Delete"))
          .detail(projectName)
          .show();
    } catch (RuntimeException ex) {
      Message.error()
          .clientId("applicationMessage")
          .summary(Ivy.cm().co("/common/Error"))
          .detail(ex.getMessage())
          .exception(ex)
          .show();
    }
  }

  public static class ProjectRow {

    private final String name;
    private final String version;
    private final String lastChanged;

    public ProjectRow(String name, String version, String lastChanged) {
      this.name = name;
      this.version = version;
      this.lastChanged = lastChanged;
    }

    public String getName() {
      return name;
    }

    public String getVersion() {
      return version;
    }

    public String getLastChanged() {
      return lastChanged;
    }
  }
}
