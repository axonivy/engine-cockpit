package ch.ivyteam.enginecockpit.application;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import org.apache.commons.lang3.Strings;

import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.enginecockpit.util.DateUtil;
import ch.ivyteam.ivy.application.ActivityOperationState;
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
  private String nameFilter = "";
  
  private IApplication app;
  private List<ProjectRow> projects;
  private ISecurityContext securityContext;
  private AppState state;
 
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

    state = new AppState(app);
    state.updateReleaseState(app.getReleaseState());
  }

  public ProjectRow toProjectRow(IProcessModelVersion pmv) {
    var detailView = ApplicationDetailLink.getProjectLink(pmv.getApplication().getName(), pmv.getApplication().getSecurityContext().getName(), pmv.getApplication().getVersion(), pmv.getName());
    return new ProjectRow(
            pmv.getName(),
            pmv.getLibraryVersion(),
        DateUtil.formatDate(pmv.getLastChangeDate()),
        detailView);
  }

  public String getAppLink() {
    return ApplicationDetailLink.getApplicationDetailLink(app.getName(), securityContextName);
  }

  public List<ProjectRow> getProjectRows() {
    return projects.stream()
        .filter(row -> matchesNameFilter(row.name()))
        .collect(Collectors.toList());
  }

  public String getNameFilter() {
    return nameFilter;
  }

  public void setNameFilter(String nameFilter) {
    this.nameFilter = nameFilter;
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

  public AppState getState() {
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

  public static record ProjectRow(String name, String version, String lastChanged, String detailView) {
    public String getName() {
      return name; 
    }

    public String getVersion() {
      return version; 
    }

    public String getLastChanged() {
      return lastChanged; 
    }

    public String getDetailView() {
      return detailView;
    }
  }

  private boolean matchesNameFilter(String name) {
    return nameFilter == null || nameFilter.isBlank() || Strings.CI.contains(name, nameFilter);
  }

  public static class AppState {
    private String activityState;
    private String activityStateIcon;
    private String operation;
    private String operationIcon;
    private boolean processing;
    private String releaseStateIcon;


    public AppState() {
      updateState(null);
      updateOperation(null);
    }

    public AppState(IApplication activity) {
      updateState(activity.getActivityState());
      updateOperation(activity.getActivityOperationState());
    }

    public String getState() {
      return activityState;
    }

    public String getStateCssClass() {
      return activityState.toLowerCase();
    }

    public String getStateIcon() {
      return activityStateIcon;
    }

    public String getOperation() {
      return operation;
    }

    public String getReleaseStateIcon() {
      return releaseStateIcon;
    }

    public String getOperationCssClass() {
      return operation.toLowerCase();
    }

    public String getOperationIcon() {
      return operationIcon;
    }

    public boolean isProcessing() {
      return processing;
    }

    private void updateState(ActivityState update) {
      if (update == null) {
        update = ActivityState.INACTIVE;
      }
      this.activityState = update.name();
      switch (update) {
        case ACTIVE -> this.activityStateIcon = "ti ti-circle-check";
        default -> this.activityStateIcon = "ti ti-player-pause";
      }
      ;
    }

    private void updateOperation(ActivityOperationState update) {
      if (update == null) {
        update = ActivityOperationState.INACTIVE;
      }
      this.operation = update.name();
      this.processing = false;
      switch (update) {
        case ACTIVE -> this.operationIcon = "ti ti-circle-check";
        case INACTIVE -> this.operationIcon = "ti ti-player-pause";
        case ERROR -> this.operationIcon = "ti ti-circle-minus";
        default -> {
          this.operationIcon = "ti ti-refresh spinning";
          this.processing = true;
        }
      }
      ;
    }

    public void updateReleaseState(ReleaseState update) {
      if (update == null) {
        update = ReleaseState.DELETED;
      }
      switch (update) {
        case RELEASED -> this.releaseStateIcon = "ti ti-circle-check";
        case DEPRECATED -> this.releaseStateIcon = "ti ti-circle-half-vertical";
        case ARCHIVED -> this.releaseStateIcon = "ti ti-archive";
        case CREATED, PREPARED -> this.releaseStateIcon = "ti ti-speakerphone";
        default -> this.releaseStateIcon = "ti ti-help-circle";
      }
      ;
    }
  }

  
}
