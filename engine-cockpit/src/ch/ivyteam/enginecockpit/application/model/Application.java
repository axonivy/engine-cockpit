package ch.ivyteam.enginecockpit.application.model;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.ws.rs.core.UriBuilder;

import ch.ivyteam.enginecockpit.application.ApplicationBean;
import ch.ivyteam.ivy.application.ActivityState;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.ReleaseState;
import ch.ivyteam.ivy.application.restricted.IApplicationInternal;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.workflow.WorkflowNavigationUtil;

@SuppressWarnings("removal")
public class Application implements AppTreeItem {

  private String fileDir;
  private String secSystem = ISecurityContext.DEFAULT;
  private long runningCasesCount = -1;
  private IApplicationInternal app;
  private List<WebServiceProcess> webServiceProcesses;

  public Application() {}

  @Override
  public String getName() {
    if (app == null) {
      return "";
    }
    return app.getName() + " (v" + app.getVersion() + ")";
  }

  public long getId() {
    return app.getId();
  }

  public Application(IApplication app) {
    this(app, null);
  }

  public Application(IApplication app, ApplicationBean bean) {
    this.app = (IApplicationInternal) app;
    fileDir = app.getFileDirectory();
    secSystem = app.getSecurityContext().getName();
  }

  @Override
  public String getDetailView() {
    return UriBuilder.fromPath("application-detail.xhtml")
        .queryParam("appName", getName())
        .build()
        .toString();
  }

  @Override
  public boolean isApp() {
    return true;
  }

  public long getRunningCasesCount() {
    countRunningCases();
    return runningCasesCount;
  }

  public String getIcon() {
    return "module";
  }

  @Override
  public boolean isNotStartable() {
    return app.getActivityState() == ActivityState.ACTIVE;
  }

  @Override
  public boolean isNotStopable() {
    return app.getActivityState() == ActivityState.INACTIVE;
  }

  @Override
  public void activate() {
    app.activate();
  }

  @Override
  public void deactivate() {
    app.deactivate();
  }

  @Override
  public void release() {
    app.release();
  }

  @Override
  public void lock() {
    app.lock();
  }

  @Override
  public ReleaseState getReleaseState() {
    return app.getReleaseState();
  }

  @Override
  public boolean isNotLockable() {
    return app.getActivityState() == ActivityState.LOCKED;
  }

  @Override
  public boolean isReleasable() {
    return app.getReleaseState() != ReleaseState.RELEASED;
  }

  @Override
  public String getReleaseStateIcon() {
    return switch (getReleaseState()) {
      case RELEASED -> "check-circle-1";
      case DEPRECATED -> "delete";
      case ARCHIVED -> "archive";
      case CREATED, PREPARED -> "advertising-megaphone-2";
      default -> "question-circle";
    };
  }

  private boolean isDesignerOrSystem() {
    return app.isDesigner();
  }

  public String getHomeUrl() {
    return app.getHomeLink().getRelative();
  }

  public String getDevWorkflowUrl() {
    return app.getDevWorkflowLink().getRelative();
  }

  public boolean isDisabled() {
    try {
      return !app.hasAnyActiveAndReleasedPmv();
    } catch (Exception ex) {
      var message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
          Ivy.cm().content("/applications/DisableErrorMessage").replace("application", app.getName()).get(),
          ex.getMessage());
      FacesContext.getCurrentInstance().addMessage(null, message);
      return true;
    }
  }

  public String getFileDir() {
    return fileDir;
  }

  public void setFileDir(String fileDir) {
    this.fileDir = fileDir;
  }

  public void setSecSystem(String secSystem) {
    this.secSystem = secSystem;
  }

  public String getSecSystem() {
    return secSystem;
  }

  public ISecurityContext getSecurityContext() {
    return app.getSecurityContext();
  }

  public long getApplicationId() {
    return getId();
  }

  public String getActivityType() {
    return AbstractActivity.APP;
  }

  public void convert() {
    // execute(() -> app.convertProjects(new ProjectConversionLog()), "convert", true);
  }

  public boolean canConvert() {
    return true;
  }
  //
  // if(app!=null)
  // {
  // return !app.hasProjectsToConvert();
  // }return true;
  // }

  public void delete() {
    // execute(() -> IApplicationRepository.instance().delete(getName()), "delete", false);
  }

  public String getDeleteHint() {
    var message = new StringBuilder();
    if (runningCasesCount > 0) {
      message.append(Ivy.cm().content("/applications/DeleteRunningCasesHintMessage")
          .replace("activityType", getActivityType()).replace("runningCases", String.valueOf(runningCasesCount)).get());
    }
    // message.append(super.getDeleteHint());
    return message.toString();
  }

  public String getSecuritySystemName() {
    return app.getSecurityContext().getName();
  }

  private void countRunningCases() {
    if (app != null && runningCasesCount < 0) {
      runningCasesCount = WorkflowNavigationUtil.getWorkflowContext(app).getRunningCasesCount(app);
    }
  }

  public IApplicationInternal app() {
    return app;
  }

  @SuppressWarnings("deprecation")
  public List<WebServiceProcess> getWebServiceProcesses() {
    if (webServiceProcesses == null) {
      // webServiceProcesses = app.getProcessModels().stream()
      // .map(IProcessModel::getReleasedProcessModelVersion)
      // .map(IWorkflowProcessModelVersion::of)
      // .filter(Objects::nonNull)
      // .flatMap(pmv -> pmv.getWebServiceProcesses().stream())
      // .map(WebServiceProcess::new)
      // .collect(Collectors.toList());
    }
    return webServiceProcesses;
  }

  @SuppressWarnings("restriction")
  public boolean hasReleasedProcessModelVersion() {
    // return app.getProcessModels()
    // .stream()
    // .map(ch.ivyteam.ivy.application.internal.ProcessModel.class::cast)
    // .allMatch(ch.ivyteam.ivy.application.internal.ProcessModel::hasReleasedProcessModelVersion);
    return true;
  }

  public String getWarningMessageForNoReleasedPmv() {
    return Ivy.cm().co("/applications/ApplicationWarningMessageForNoReleasedPmv");
  }

  @Override
  public List<String> isDeletable() {
    return app.isDeletable();
  }

  public int version() {
    return app.getVersion();
  }
}
