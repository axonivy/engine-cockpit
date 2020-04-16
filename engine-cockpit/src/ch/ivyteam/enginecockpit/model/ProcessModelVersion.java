package ch.ivyteam.enginecockpit.model;

import java.text.SimpleDateFormat;

import ch.ivyteam.enginecockpit.ApplicationBean;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.ReleaseState;
import ch.ivyteam.ivy.application.value.QualifiedVersion;
import ch.ivyteam.ivy.environment.Ivy;

public class ProcessModelVersion extends AbstractActivity
{
  private ReleaseState releaseState;
  private IProcessModelVersion pmv;
  private QualifiedVersion qualifiedVersion;
  private String lastChangeDate;

  public ProcessModelVersion(IProcessModelVersion pmv)
  {
    this(pmv, null);
  }
  
  public ProcessModelVersion(IProcessModelVersion pmv, ApplicationBean bean)
  {
    super(pmv.getVersionName(), pmv.getId(), pmv, bean);
    setOperationState(pmv.getActivityOperationState());
    releaseState = pmv.getReleaseState();
    disable = pmv.getProcessModel().getApplication().getName().equals("designer");
    qualifiedVersion = pmv.getLibrary().getQualifiedVersion();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    lastChangeDate = formatter.format(pmv.getLastChangeDate());
    this.pmv = pmv;
  }
  
  @Override
  public void updateStats()
  {
    super.updateStats();
    if (pmv != null)
    {
      releaseState = pmv.getReleaseState();
    }
  }
  
  @Override
  public long getRunningCasesCount()
  {
    return Ivy.wf().getRunningCasesCount(pmv);
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
  
  @Override
  public String getReleaseStateLowerCase()
  {
    return releaseState.toString().toLowerCase();
  }
  
  public void setReleaseState(ReleaseState state)
  {
    this.releaseState = state;
  }
  
  @Override
  public void release()
  {
    pmv.release();
    super.release();
  }
  
  @Override
  public void delete()
  {
    pmv.delete();
    super.delete();
  }
  
  @Override
  public boolean isReleaseDisabled()
  {
    return releaseState == ReleaseState.RELEASED || releaseState == ReleaseState.DELETED;
  }
  
  @Override
  public boolean isDeleteDisabled()
  {
    return pmv.isRequired() || (releaseState != ReleaseState.PREPARED && releaseState != ReleaseState.ARCHIVED);
  }
  
  @Override
  public long getApplicationId()
  {
    return pmv.getProcessModel().getApplication().getId();
  }
  
  @Override
  public String getActivityType()
  {
    return AbstractActivity.PMV;
  }
  
  public String getQualifiedVersion()
  {
    return qualifiedVersion.getRawVersion();
  }
  
  public String getLastChangeDate()
  {
    return lastChangeDate;
  }
  
}
