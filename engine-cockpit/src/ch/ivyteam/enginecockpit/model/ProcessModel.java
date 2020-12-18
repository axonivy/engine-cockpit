package ch.ivyteam.enginecockpit.model;

import java.util.HashSet;
import java.util.Set;

import ch.ivyteam.enginecockpit.ApplicationBean;
import ch.ivyteam.ivy.application.IProcessModel;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.ProcessModelVersionRelation;
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
    return ((IProcessModel) activity).getApplication().getId();
  }
  
  @Override
  public String getActivityType()
  {
    return AbstractActivity.PM;
  }
  
  @Override
  public boolean isDeleteDisabled()
  {
    return !((IProcessModel) activity).isDeletable();
  }
  
  @Override
  public String getDeleteHint()
  {
    var message = new StringBuilder();
    if (runningCasesCount > 0)
    {
      message.append(runningCasesCount + " running cases");
    }
    var dependentPmvs = getDependentPmvs();
    if (!dependentPmvs.isEmpty())
    {
      if (message.length() > 0)
      {
        message.append(" and ");
      }
      message.append(dependentPmvs.size() + " dependent PMV(s)");
    }
    
    if (message.length() > 0)
    {
      message.insert(0, "PM contains ");
      message.append(". ");
    }
    message.append(super.getDeleteHint());

    return message.toString();
  }
  
  private Set<IProcessModelVersion> getDependentPmvs()
  {
    var dependentPMVs = new HashSet<IProcessModelVersion>();
    var pmvs = ((IProcessModel) activity).getProcessModelVersions();
    pmvs.forEach(pmv -> dependentPMVs.addAll(pmv.getAllRelatedProcessModelVersions(ProcessModelVersionRelation.DEPENDENT)));
    return dependentPMVs;
  }
  
  @Override
  public boolean isDisabled()
  {
    return getName().equals("engine-cockpit");
  }

}
