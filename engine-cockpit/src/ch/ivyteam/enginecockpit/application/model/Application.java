package ch.ivyteam.enginecockpit.application.model;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.ws.rs.core.UriBuilder;

import ch.ivyteam.enginecockpit.application.ApplicationBean;
import ch.ivyteam.ivy.application.IApplication;
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
    return app.getName();
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

  public boolean isApplication() {
    return true;
  }

  @Override
  public String getDetailView() {
    return UriBuilder.fromPath("application-detail.xhtml")
        .queryParam("appName", getName())
        .build()
        .toString();
  }

  public long getRunningCasesCount() {
    countRunningCases();
    return runningCasesCount;
  }

  public String getIcon() {
    return "module";
  }

  public boolean isNotStartable() {
    // return super.isNotStartable() || isDesignerOrSystem();
    return false;
  }

  public boolean isNotStopable() {
    // return super.isNotStopable() || isDesignerOrSystem();
    return false;
  }

  public boolean isNotLockable() {
    // return super.isNotLockable() || isDesignerOrSystem();
    return false;
  }

  private boolean isDesignerOrSystem() {
    return app.isDesigner() || app.isSystem();
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

  public List<String> isDeletable() {
    return app.isDeletable();
  }
}
