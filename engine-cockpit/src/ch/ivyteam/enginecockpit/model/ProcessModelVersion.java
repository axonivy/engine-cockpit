package ch.ivyteam.enginecockpit.model;

import java.text.SimpleDateFormat;

import ch.ivyteam.enginecockpit.ApplicationBean;
import ch.ivyteam.ivy.application.ILibrary;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.ReleaseState;
import ch.ivyteam.ivy.environment.Ivy;

public class ProcessModelVersion extends AbstractActivity
{
  private ReleaseState releaseState;
  private IProcessModelVersion pmv;
  private String lastChangeDate;
  private Library lib;

  public ProcessModelVersion(IProcessModelVersion pmv)
  {
    this(pmv, null);
  }
  
  public ProcessModelVersion(IProcessModelVersion pmv, ApplicationBean bean)
  {
    super(pmv.getVersionName(), pmv.getId(), pmv, bean);
    setOperationState(pmv.getActivityOperationState());
    releaseState = pmv.getReleaseState();
    lib = new Library(pmv.getLibrary());
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    if (pmv.getLastChangeDate() != null)
    {
      lastChangeDate = formatter.format(pmv.getLastChangeDate());
    }
    this.pmv = pmv;
  }

  @Override
  public String getDetailView()
  {
    return "pmv-detail.xhtml?appName=" + pmv.getApplication().getName() + "&pmName=" + pmv.getName() + "&pmvVersion=" + pmv.getVersionNumber();
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
    return lib.version;
  }
  
  public String getLastChangeDate()
  {
    return lastChangeDate;
  }
 
  public String getLibraryId()
  {
    return lib.id;
  }
  
  public boolean isLibraryResolved()
  {
    return lib.resolved;
  }
  
  public String getLibraryResolvedTooltip()
  {
    return (isLibraryResolved() ? "All" : "Not all") + " direct and indirect required libraries are available in the system.";
  }
  
  @Override
  public boolean isDisabled()
  {
    return getName().startsWith("engine-cockpit");
  }
  
  private class Library
  {
    private String id = "Unknown id";
    private String version = "Unknown version";
    private boolean resolved = false;
    
    private Library(ILibrary lib)
    {
      if (lib != null)
      {
        id = lib.getId();
        version = lib.getQualifiedVersion().getRawVersion();
        resolved = lib.isResolved();
      }
    }
  }
  
}
