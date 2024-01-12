package ch.ivyteam.enginecockpit.application.model;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.application.ApplicationBean;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.IApplicationConfigurationManager;
import ch.ivyteam.ivy.application.IApplicationInternal;
import ch.ivyteam.ivy.application.IProcessModel;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.workflow.IWorkflowProcessModelVersion;
import ch.ivyteam.ivy.workflow.WorkflowNavigationUtil;

@SuppressWarnings("removal")
public class Application extends AbstractActivity {

  private String fileDir;
  private String secSystem = ISecurityContext.DEFAULT;
  private long runningCasesCount = -1;
  private IApplicationInternal app;
  private List<WebServiceProcess> webServiceProcesses;

  public Application() {
    super("", 0, null, null);
  }

  public Application(IApplication app) {
    this(app, null);
  }

  public Application(IApplication app, ApplicationBean bean) {
    super(app.getName(), app.getId(), app, bean);
    this.app = (IApplicationInternal) app;
    fileDir = app.getFileDirectory();
    secSystem = app.getSecurityContext().getName();
  }

  @Override
  public boolean isApplication() {
    return true;
  }

  @Override
  public String getDetailView() {
    return "application-detail.xhtml?appName=" + getName();
  }

  @Override
  public long getRunningCasesCount() {
    countRunningCases();
    return runningCasesCount;
  }

  @Override
  public String getIcon() {
    return "module";
  }

  @Override
  public boolean isProtected() {
    return getName().equals("designer");
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
      var message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot evaluate the state of the application '" + app.getName() + "'", ex.getMessage());
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

  @Override
  public long getApplicationId() {
    return getId();
  }

  @Override
  public String getActivityType() {
    return AbstractActivity.APP;
  }

  @Override
  public void delete() {
    execute(() -> IApplicationConfigurationManager.instance().deleteApplication(getName()), "delete", false);
  }

  @Override
  public String getDeleteHint() {
    var message = new StringBuilder();
    if (runningCasesCount > 0) {
      message.append(getActivityType()).append(" has ").append(runningCasesCount).append(" running cases. ");
      message.append("They will also be deleted. ");
    }
    message.append(super.getDeleteHint());
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

  public List<WebServiceProcess> getWebServiceProcesses() {
    if (webServiceProcesses == null) {
      webServiceProcesses = app.getProcessModels().stream()
              .map(IProcessModel::getReleasedProcessModelVersion)
              .map(IWorkflowProcessModelVersion::of)
              .filter(Objects::nonNull)
              .flatMap(pmv -> pmv.getWebServiceProcesses().stream())
              .map(WebServiceProcess::new)
              .collect(Collectors.toList());
    }
    return webServiceProcesses;
  }
}
