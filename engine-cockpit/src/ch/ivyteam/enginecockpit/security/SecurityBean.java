package ch.ivyteam.enginecockpit.security;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.SecuritySystem;
import ch.ivyteam.enginecockpit.util.SynchronizationLogger;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@ManagedBean
@ViewScoped
@SuppressWarnings("restriction")
public class SecurityBean
{
  private List<SecuritySystem> systems;
  
  private SynchronizationLogger synchronizationLogger = new SynchronizationLogger();

  private ManagerBean managerBean;

  public SecurityBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
    systems = managerBean.getIApplicaitons().stream()
            .map(app -> new SecuritySystem(app.getSecurityContext(), app.getName()))
            .collect(Collectors.toList());
  }

  public List<SecuritySystem> getSecuritySystems()
  {
    return systems;
  }
  
  public Set<String> getAvailableSecuritySystems()
  {
	Set<String> names = IConfiguration.get().getNames("SecuritySystems");
	names.add("ivy Security System");
	return names;
  }

  public void triggerSynchronization(String appName)
  {
    managerBean.getManager().findApplication(appName).getSecurityContext()
            .triggerSynchronization(synchronizationLogger);
  }
  
  public void triggerSyncForSelectedApp()
  {
    triggerSynchronization(managerBean.getSelectedApplication().getName());
  }
  
  public boolean isIvySecurityForSelectedApp()
  {
    Optional<SecuritySystem> findAny = systems.stream().filter(s -> s.getAppName().equals(managerBean.getSelectedApplication().getName())).findAny();
    if (!findAny.isPresent())
    {
      return true;
    }
    return findAny.get().getSecuritySystemProvider().equals("ivy Security System");
  }
  
  public boolean isSyncRunningForSelectedApp()
  {
    return isSyncRunning(managerBean.getSelectedApplication().getName());
  }

  public boolean isSyncRunning(String appName)
  {
    return managerBean.getManager().findApplication(appName).getSecurityContext().isSynchronizationRunning();
  }
  
  public boolean isAnySyncRunningOrNewLog()
  {
    return managerBean.getIApplicaitons().stream()
            .filter(app -> app.getSecurityContext().isSynchronizationRunning() == true)
            .findAny().isPresent();
  }
  
  public String getLogs()
  {
    StringBuilder sb = new StringBuilder();
    synchronizationLogger.getSynchronizationLogMessages().stream().forEach(msg -> sb.append(msg).append("\n"));
    if(sb.length() > 2) {
      sb.setLength(sb.length() - 2);
    }
    return sb.toString();
  }

}
