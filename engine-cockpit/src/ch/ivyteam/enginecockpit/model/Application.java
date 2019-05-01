package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.enginecockpit.ApplicationBean;
import ch.ivyteam.enginecockpit.util.SecuritySystemConfig;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.environment.Ivy;

public class Application extends AbstractActivity
{
  
  private String desc;
  private String fileDir;
  private String owner;
  private String activeEnv;
  private long runningCasesCount;
  
  public Application()
  {
    super();
  }

  public Application(IApplication app)
  {
    this(app, null);
  }
  
  public Application(IApplication app, ApplicationBean bean)
  {
    super(app.getName(), app.getId(), app, bean);
    setOperationState(app.getActivityOperationState());
    disable = app.getName().equals("designer");
    desc = app.getDescription();
    fileDir = app.getFileDirectory();
    owner = app.getOwnerName();
    activeEnv = app.getActualEnvironment().getName();
    runningCasesCount = app.getProcessModels().stream()
            .flatMap(pm -> pm.getProcessModelVersions().stream())
            .mapToLong(pmv -> Ivy.wf().getRunningCasesCount(pmv)).sum();
  }
  
  @Override
  public boolean isApplication()
  {
    return true;
  }
  
  @Override
  public String getDetailView()
  {
    return "application-detail.xhtml?appName=" + getName();
  }

  @Override
  public long getRunningCasesCount()
  {
    return runningCasesCount;
  }
  
  @Override
  public String getIcon()
  {
    return "cube";
  }

  public String getDesc()
  {
    return desc;
  }

  public void setDesc(String desc)
  {
    this.desc = desc;
  }

  public String getFileDir()
  {
    return fileDir;
  }

  public void setFileDir(String fileDir)
  {
    this.fileDir = fileDir;
  }

  public String getOwner()
  {
    return owner;
  }

  public void setOwner(String owner)
  {
    this.owner = owner;
  }

  public String getActiveEnv()
  {
    return activeEnv;
  }

  public void setActiveEnv(String activeEnv)
  {
    this.activeEnv = activeEnv;
  }
  
  @Override
  public long getApplicationId()
  {
    return getId();
  }
  
  @Override
  public String getActivityType()
  {
    return AbstractActivity.APP;
  }

  public String getSecuritySystemName()
  {
    return SecuritySystemConfig.getOrBlank(SecuritySystemConfig.getAppPrefix(getName()));
  }

  public void setSecuritySystem(String securitySystemName)
  {
    SecuritySystemConfig.setOrRemove(SecuritySystemConfig.getAppPrefix(getName()), securitySystemName);
  }

}
