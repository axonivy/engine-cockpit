package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.ivy.application.IProcessModel;

public class ProcessModel extends AbstractActivity
{

  public ProcessModel(IProcessModel pm)
  {
    super(pm.getName(), pm.getId(), pm);
    setOperationState(pm.getActivityOperationState());
    disable = pm.getApplication().getName().equals("designer");
  }

  @Override
  public String getIcon()
  {
    return "cogs";
  }

}
