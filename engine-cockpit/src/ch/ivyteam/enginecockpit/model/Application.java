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
    super(app.getName(), app.getId());
  }

  public Application(String name, long id)
  {
    super(name, id);
  }

  @Override
  public String getIcon()
  {
    return "cube";
  }

}
