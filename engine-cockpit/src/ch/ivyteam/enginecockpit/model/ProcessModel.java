package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.enginecockpit.ApplicationBean;
import ch.ivyteam.ivy.application.IProcessModel;
import ch.ivyteam.ivy.environment.Ivy;

public class ProcessModel extends AbstractActivity
{
  private long runningCasesCount;
  
  public ProcessModel(IProcessModel pm)
  {
    this(pm, null);
  }
  
  public ProcessModel(IProcessModel pm, ApplicationBean bean)
  {
    super(pm.getName(), pm.getId(), pm, bean);
    setOperationState(pm.getActivityOperationState());
    disable = pm.getApplication().getName().equals("designer");
    runningCasesCount = pm.getProcessModelVersions().stream()
            .mapToLong(pmv -> Ivy.wf().getRunningCasesCount(pmv)).sum();
  }

  @Override
  public long getRunningCasesCount()
  {
    return runningCasesCount;
  }
  
  @Override
  public String getIcon()
  {
    return "cogs";
  }
  
  @Override
  public long getApplicationId()
  {
    Ivy.log().info(((IProcessModel) activity).getApplication().getId());
    return ((IProcessModel) activity).getApplication().getId();
  }
  
  @Override
  public int getActivityType()
  {
    return AbstractActivity.PM;
  }

}
