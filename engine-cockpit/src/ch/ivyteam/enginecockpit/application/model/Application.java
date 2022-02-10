package ch.ivyteam.enginecockpit.application.model;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.ivyteam.enginecockpit.application.ApplicationBean;
import ch.ivyteam.enginecockpit.security.system.SecuritySystemConfig;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.IApplicationConfigurationManager;
import ch.ivyteam.ivy.application.IApplicationInternal;
import ch.ivyteam.ivy.application.IProcessModel;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.workflow.IWorkflowProcessModelVersion;

@SuppressWarnings({"restriction", "removal"})
public class Application extends AbstractActivity {

  private String desc;
  private String fileDir;
  private String owner;
  private String activeEnv;
  private long runningCasesCount;
  private IConfiguration configuration;
  private IApplication app;
  private List<WebServiceProcess> webServiceProcesses;

  public Application() {
    super("", 0, null, null);
  }

  public Application(IApplication app) {
    this(app, null);
  }

  public Application(IApplication app, ApplicationBean bean) {
    super(app.getName(), app.getId(), app, bean);
    this.app = app;
    desc = app.getDescription();
    fileDir = app.getFileDirectory();
    owner = app.getOwnerName();
    activeEnv = app.getActualEnvironment().getName();
    configuration = ((IApplicationInternal) app).getConfiguration();
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

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public String getFileDir() {
    return fileDir;
  }

  public void setFileDir(String fileDir) {
    this.fileDir = fileDir;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public String getActiveEnv() {
    return activeEnv;
  }

  public void setActiveEnv(String activeEnv) {
    this.activeEnv = activeEnv;
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
    return configuration.getOrDefault(SecuritySystemConfig.SECURITY_STSTEM);
  }

  public void setSecuritySystem(String securitySystemName) {
    configuration.set(SecuritySystemConfig.SECURITY_STSTEM, securitySystemName);
  }

  private void countRunningCases() {
    if (app != null && runningCasesCount == 0) {
      runningCasesCount = app.getProcessModels().stream()
              .flatMap(pm -> pm.getProcessModelVersions().stream())
              .mapToLong(pmv -> Ivy.wf().getRunningCasesCount(pmv)).sum();
    }
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
