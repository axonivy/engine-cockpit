package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.ivy.application.IApplication;

public class Application extends AbstractActivity
{
  
  private String desc;
  private String fileDir;
  private String owner;
  private String activeEnv;
  
  public Application()
  {
    super();
  }

  public Application(IApplication app)
  {
    super(app.getName(), app.getId(), app);
    setOperationState(app.getActivityOperationState());
    disable = app.getName().equals("designer");
    desc = app.getDescription();
    fileDir = app.getFileDirectory();
    owner = app.getOwnerName();
    activeEnv = app.getActiveEnvironment();
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

}
