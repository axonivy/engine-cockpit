package ch.ivyteam.enginecockpit.application.model;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.ws.rs.core.UriBuilder;

import ch.ivyteam.enginecockpit.application.ApplicationsBean;
import ch.ivyteam.ivy.application.ActivityState;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.ReleaseState;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.application.app.convert.AppProjectConverter;
import ch.ivyteam.ivy.application.app.link.AppLink;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.workflow.IWorkflowProcessModelVersion;
import ch.ivyteam.ivy.workflow.WorkflowNavigationUtil;

@SuppressWarnings("removal")
public class Application extends AppTreeItem {

  private String fileDir;
  private String secSystem = ISecurityContext.DEFAULT;
  private long runningCasesCount = -1;
  private IApplication app;
  private List<WebServiceProcess> webServiceProcesses;
  private String name;

  public Application() {
    super(null, null);
  }

  @Override
  public String getSecurityContextName() {
    return secSystem;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDisplayName() {
    return name + " (v" + version() + ")";
  }

  public long getId() {
    return app.getId();
  }

  public void setName(String name) {
    this.name = name;
  }

  public Application(IApplication app) {
    this(app, null);
  }

  public Application(IApplication app, ApplicationsBean bean) {
    super(bean, app);
    this.app = app;
    this.name = app.getName();
    fileDir = this.app.getDirectory().toString();
    secSystem = app.getSecurityContext().getName();
  }

  @Override
  public String getDetailView() {
    return getDetailViewLink(app.getSecurityContext().getName(), getName(), version());
  }

    public static String getDetailViewLink(String securityContextName, String appName, int appVersion) {
    return UriBuilder.fromPath("application.xhtml")
      .queryParam("context", securityContextName)
      .queryParam("app", appName)
        .queryParam("appVersion", appVersion)
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

  @Override
  public String getIcon() {
    return "ti ti-cube";
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
  public ReleaseState getReleaseState() {
    return app.getReleaseState();
  }

  @Override
  public boolean isReleasable() {
    return app.getReleaseState() != ReleaseState.RELEASED;
  }

  @Override
  public String getReleaseStateIcon() {
    return switch (getReleaseState()) {
      case RELEASED          -> "ti ti-circle-check";
      case DEPRECATED        -> "ti ti-circle-half-vertical";
      case ARCHIVED          -> "ti ti-archive";
      case CREATED, PREPARED -> "ti ti-speakerphone";
      default                -> "ti ti-help-circle";
    };
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

  public void setSecSystem(String secSystem) {
    this.secSystem = secSystem;
  }

  public String getSecSystem() {
    return secSystem;
  }

  public ISecurityContext getSecurityContext() {
    return app.getSecurityContext();
  }

  @Override
  public long getApplicationId() {
    return getId();
  }

  @Override
  public String getActivityType() {
    return AppTreeItem.APP;
  }

  @Override
  public void convert() {
    execute(() -> AppProjectConverter.of(app).run(new ProjectConversionLog()), "convert", true);
  }

  public boolean canConvert() {
    if (app != null) {
      return AppProjectConverter.of(app).canConvert();
    }
    return true;
  }

  @Override
  public boolean isNotConvertable() {
    if (app != null) {
      return !AppProjectConverter.of(app).canConvert();
    }
    return true;
  }

  @Override
  public void delete() {
    execute(() -> IApplicationRepository.instance().delete(getName(), version()), "delete", false);
  }

  public String getDeleteHint() {
    var message = new StringBuilder();
    if (runningCasesCount > 0) {
      message.append(Ivy.cm().content("/applications/DeleteRunningCasesHintMessage")
          .replace("activityType", getActivityType()).replace("runningCases", String.valueOf(runningCasesCount)).get());
    }
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

  public IApplication app() {
    return app;
  }

  @SuppressWarnings("deprecation")
  public List<WebServiceProcess> getWebServiceProcesses() {
    if (webServiceProcesses == null) {
      webServiceProcesses = app.getProcessModelVersions()
          .map(IWorkflowProcessModelVersion::of)
          .filter(Objects::nonNull)
          .flatMap(pmv -> pmv.getWebServiceProcesses().stream())
          .map(WebServiceProcess::new)
          .collect(Collectors.toList());
    }
    return webServiceProcesses;
  }

  public String getWarningMessageForNoReleasedPmv() {
    return Ivy.cm().co("/applications/ApplicationWarningMessageForNoReleasedPmv");
  }

  @Override
  public List<String> isDeletable() {
    return app.isDeletable();
  }

  @Override
  public String getVersion() {
    if (app == null) {
      return "";
    }
    return String.valueOf(app.getVersion());
  }

  public int version() {
    return app.getVersion();
  }

  @Override
  public long getProcessModelVersionId() {
    return -1;
  }
}
