package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.ivy.application.IApplication;

public class Application extends AbstractActivity
{
  
  public Application()
  {
    super();
  }

  public Application(IApplication app)
  {
    super(app.getName(), app.getId(), app);
    setOperationState(app.getActivityOperationState());
    disable = app.getName().equals("designer");
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

}
