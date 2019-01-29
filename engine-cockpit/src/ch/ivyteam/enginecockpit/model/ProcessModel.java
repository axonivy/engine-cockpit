package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.ivy.application.ActivityState;
import ch.ivyteam.ivy.application.IProcessModel;

public class ProcessModel extends AbstractActivity
{

  public ProcessModel(IProcessModel pm)
  {
    super(pm.getName(), pm.getId());
    ActivityState activityState = pm.getActivityState();
  }

  public ProcessModel(String name, long id)
  {
    super(name, id);
  }
  
  @Override
  public String getIcon()
  {
    return "cogs";
  }

}
