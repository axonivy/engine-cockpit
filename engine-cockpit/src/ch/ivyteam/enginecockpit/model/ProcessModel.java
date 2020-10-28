package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.enginecockpit.ApplicationBean;
import ch.ivyteam.ivy.application.IProcessModel;
import ch.ivyteam.ivy.environment.Ivy;

public class ProcessModel extends AbstractActivity
{
  private long runningCasesCount;
  private IProcessModel pm;
  
  public ProcessModel(IProcessModel pm, ApplicationBean bean)
  {
    super(pm.getName(), pm.getId(), pm, bean);
    this.pm = pm;
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
    return "module-three-2";
  }
  
  @Override
  public long getApplicationId()
  {
    return ((IProcessModel) activity).getApplication().getId();
  }
  
  @Override
  public String getActivityType()
  {
    return AbstractActivity.PM;
  }
  
  @Override
  public void delete()
  {
    execute(() -> pm.getApplication().deleteProcessModel(getName()), "delete", false);
  }
  
  @Override
  public boolean isProtected()
  {
    return getName().equals("engine-cockpit");
  }

}
