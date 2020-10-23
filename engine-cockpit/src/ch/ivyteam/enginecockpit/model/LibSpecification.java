package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.ivy.application.ILibrarySpecification;
import ch.ivyteam.ivy.application.value.VersionRange;

public class LibSpecification
{

  private String id;
  private boolean resolved;
  private String version;
  private ProcessModelVersion resolvedPmv;
  private String resolvedPmvName;

  public LibSpecification(ILibrarySpecification spec)
  {
    id = spec.getId();
    resolved = spec.isResolved();
    version = VersionRange.of(spec).toString();
    var resolvedLib = spec.getResolvedLibrary();
    if (resolvedLib != null)
    {
      resolvedPmv = new ProcessModelVersion(resolvedLib.getProcessModelVersion());
      resolvedPmvName = resolvedPmv.getName() + " (" + resolvedPmv.getQualifiedVersion() + " / " + 
              resolvedPmv.getOperationState() + " / " + resolvedPmv.getReleaseState() + ")";
    }
  }
  
  public String getId()
  {
    return id;
  }
  
  public boolean isResolved()
  {
    return resolved;
  }
  
  public String getVersion()
  {
    return version;
  }
  
  public String getResolvedPmvName()
  {
    return resolvedPmvName;
  }
  
  public ProcessModelVersion getResolvedPmv()
  {
    return resolvedPmv;
  }
  
  public String getResolvedTooltip()
  {
    if (isResolved())
    {
      return "'" + getResolvedPmvName() + "' resolved for this specification";
    }
    return "No project could be found for this specification.";
  }

}
