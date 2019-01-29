package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.ivy.application.IProcessModelVersion;

public class ProcessModelVersion extends AbstractActivity
{

  public ProcessModelVersion(IProcessModelVersion pmv)
  {
    super(pmv.getVersionName(), pmv.getId());
  }

  public ProcessModelVersion(String name, long id)
  {
    super(name, id);
  }
  
  @Override
  public String getIcon()
  {
    return "cog";
  }

}
