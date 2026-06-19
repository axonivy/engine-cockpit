package ch.ivyteam.enginecockpit.application;

import java.io.Serializable;
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
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.ReleaseState;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.application.app.link.AppLink;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityContextRepository;

@Named
@ViewScoped
public class ApplicationVersionBean implements Serializable {

  private String securityContextName;
  private String appName;
  private int appVersion;
  
  private IApplication app;
  private List<ProjectRow> projects;
  private ISecurityContext securityContext;
  private StateOfActivity state;
 
  private final ManagerBean managerBean;

  public ApplicationVersionBean() {
    managerBean = ManagerBean.instance();
  }

  public void setContext(String securityContextName) {
    this.securityContextName = securityContextName;
  }

  public String getContext() {
    return securityContextName;
  }

  public void setApp(String appName) {
    this.appName = appName;
  }

  public String getApp() {
    return appName;
  }

  public void setVersion(int appVersion) {
    this.appVersion = appVersion;
  }

  public int getVersion() {
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
        .filter(app -> appVersion == app.getVersion())
        .findAny()
        .orElse(null);

    if (app == null) {
      ResponseHelper.notFound("Application not found: " + appName);
      return;
    }

    projects = app.getProcessModelVersions()
        .map(this::toProjectRow)
        .sorted(Comparator.comparing(ProjectRow::name, String.CASE_INSENSITIVE_ORDER))
        .collect(Collectors.toList());

    state = new StateOfActivity(app);
    state.updateReleaseState(app.getReleaseState());
  }

  public ProjectRow toProjectRow(IProcessModelVersion pmv) {
    return new ProjectRow(
            pmv.getName(),
            pmv.getLibraryVersion(),
            DateUtil.formatDate(pmv.getLastChangeDate()));
  }

  public String getAppLink() {
    return ApplicationDetailLink.getApplicationDetailLink(app.getName(), securityContextName);
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
    return app.getDirectory().toString();
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
    return state;
  }

  public void deleteProject(String projectName) {
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

  public static record ProjectRow(String name, String version, String lastChanged) {
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
