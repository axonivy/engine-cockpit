package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.ivy.application.IProcessModel;

public class ProcessModel extends AbstractActivity
{

  public ProcessModel(IProcessModel pm)
  {
    super(pm.getName(), pm.getId(), pm);
    setOperationState(pm.getActivityOperationState());
  }

  @Override
  public String getIcon()
  {
    return "cogs";
  }

}
