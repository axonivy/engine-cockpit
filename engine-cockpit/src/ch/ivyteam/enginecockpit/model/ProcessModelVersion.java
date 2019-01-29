package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.ivy.application.IProcessModelVersion;

public class ProcessModelVersion extends AbstractActivity
{

  public ProcessModelVersion(IProcessModelVersion pmv)
  {
    super(pmv.getVersionName(), pmv.getId(), pmv);
    setOperationState(pmv.getActivityOperationState());
  }

  @Override
  public String getIcon()
  {
    return "cog";
  }

}
