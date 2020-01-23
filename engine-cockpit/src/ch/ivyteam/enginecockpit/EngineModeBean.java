package ch.ivyteam.enginecockpit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.dashboad.LicenceBean;
import ch.ivyteam.enginecockpit.system.SystemDatabaseBean;
import ch.ivyteam.ivy.server.restricted.EngineMode;
import ch.ivyteam.ivy.server.restricted.MaintenanceReason;

@ManagedBean
@SessionScoped
@SuppressWarnings("restriction")
public class EngineModeBean
{
  public boolean isDemo()
  {
    return EngineMode.is(EngineMode.DEMO) || EngineMode.is(EngineMode.DESIGNER_EMBEDDED);
  }
  
  public boolean isMaintenance()
  {
    return EngineMode.is(EngineMode.MAINTENANCE);
  }
  
  public boolean hasDashboardWarning()
  {
    return isDemo() || isMaintenance();
  }
  
  public String getDashboardWarningSummary()
  {
    return isDemo() ? "Demo Mode!" : "Maintenance Mode!";
  }
  
  public String getDashboardWarningDetail()
  {
    return isDemo() ? getWarningMessage() : getMaintenanceReason();
  }
  
  private String getWarningMessage()
  {
    String licenceProblemMsg = new LicenceBean().getProblemMessage();
    if (StringUtils.isNotBlank(licenceProblemMsg))
    {
      return licenceProblemMsg;
    }
    return new SystemDatabaseBean().isPersistentDb() ? "Unfinished setup." : "No persistent database configured.";
  }
  
  public String getMaintenanceReason()
  {
    return MaintenanceReason.getMessage();
  }
}
