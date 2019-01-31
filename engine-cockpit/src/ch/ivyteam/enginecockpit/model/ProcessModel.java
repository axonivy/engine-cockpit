package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.enginecockpit.ApplicationBean;
import ch.ivyteam.ivy.application.IProcessModel;

public class ProcessModel extends AbstractActivity
{

  public ProcessModel(IProcessModel pm)
  {
    this(pm, null);
  }
  
  public ProcessModel(IProcessModel pm, ApplicationBean bean)
  {
    super(pm.getName(), pm.getId(), pm, bean);
    setOperationState(pm.getActivityOperationState());
    disable = pm.getApplication().getName().equals("designer");
  }

  @Override
  public String getIcon()
  {
    return "cogs";
  }

}
