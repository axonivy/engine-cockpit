package ch.ivyteam.enginecockpit.application;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.Strings;

import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.enginecockpit.util.DateUtil;
import ch.ivyteam.ivy.application.ActivityOperationState;
import ch.ivyteam.ivy.application.ActivityState;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.ReleaseState;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityContextRepository;

@Named
@ViewScoped
public class ApplicationVersionBean implements Serializable {

  private String contextName;
  private String appName;
  private int appVersion;
  private String nameFilter = "";
  
  private IApplication app;
  private List<ProjectRow> projects;
  private ISecurityContext context;
 
  public void setContext(String contextName) {
    this.contextName = contextName;
  }

  public String getContext() {
    return contextName;
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
    context = ISecurityContextRepository.instance().all().stream()
        .filter(context -> contextName.equals(context.getName()))
        .findAny()
        .orElse(null);
    if (context == null) {
      ResponseHelper.notFound("Security context not found: " + contextName);
      return;
    }

    app =  IApplicationRepository.of(context).all().stream()
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

  }

  public ProjectRow toProjectRow(IProcessModelVersion pmv) {
    return new ProjectRow(
            pmv.getName(),
            pmv.getLibraryVersion(),
        DateUtil.formatDate(pmv.getLastChangeDate()),
        ProjectBean.getLink(contextName, appName, appVersion, pmv.getName()));
  }

  public static String getLink(String context, String app, int version) {
    return UriBuilder.fromPath("application-version.xhtml")
        .queryParam("context", context)
        .queryParam("app", app)
        .queryParam("version", version)
        .build()
        .toString();
  }

  public String getLink() {
    return getLink(contextName, appName, appVersion);
  }

  public String getApplicationLink() {
    return ApplicationBean.getLink(contextName, appName);
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

  public String getFileDir() {
    return app.getDirectory().toString();
  }

  public SecuritySystem getSecuritySystem() {
    return new SecuritySystem(context);
  }

  public boolean isNotStartable() {
    return app.getActivityState() == ActivityState.ACTIVE;
  }

  public boolean isNotStopable() {
    return app.getActivityState() == ActivityState.INACTIVE;
  }

  public AppState getState() {
    var state = new AppState(app);
    state.updateReleaseState(app.getReleaseState());
    return state;
  }

  public void activate() {
    execute(app::activate, "activate");
  }

  public void deactivate() {
    execute(app::deactivate, "deactivate");
  }

  private static void execute(Runnable operation, String actionKey) {
    try {
      operation.run();
    } catch (RuntimeException ex) {
      Message.error()
          .clientId("applicationMessage")
          .summary(Ivy.cm().co("/common/Error"))
          .detail(ex.getMessage())
          .exception(ex)
          .show();
    }
  }

  public static record ProjectRow(String name, String version, String lastChanged, String link) {
    public String getName() {
      return name; 
    }

    public String getVersion() {
      return version; 
    }

    public String getLastChanged() {
      return lastChanged; 
    }

    public String getLink() {
      return link;
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
