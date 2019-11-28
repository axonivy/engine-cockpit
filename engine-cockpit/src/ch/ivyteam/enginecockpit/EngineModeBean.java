package ch.ivyteam.enginecockpit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

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
    return isDemo() ? "No license installed" : getMaintenanceReason();
  }
  
  public String getMaintenanceReason()
  {
    return MaintenanceReason.getMessage();
  }
}
