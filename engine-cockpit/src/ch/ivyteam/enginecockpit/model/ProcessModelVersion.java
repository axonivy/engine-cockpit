package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.ReleaseState;

public class ProcessModelVersion extends AbstractActivity
{
  private ReleaseState releaseState;

  public ProcessModelVersion(IProcessModelVersion pmv)
  {
    super(pmv.getVersionName(), pmv.getId(), pmv);
    setOperationState(pmv.getActivityOperationState());
    releaseState = pmv.getReleaseState();
    disable = pmv.getProcessModel().getApplication().getName().equals("designer");
  }

  @Override
  public String getIcon()
  {
    return "cog";
  }
  
  @Override
  public boolean isPmv()
  {
    return true;
  }
  
  @Override
  public ReleaseState getReleaseState()
  {
    return releaseState;
  }

}
