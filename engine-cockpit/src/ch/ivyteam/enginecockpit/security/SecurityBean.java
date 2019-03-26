package ch.ivyteam.enginecockpit.security;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.SecuritySystem;
import ch.ivyteam.enginecockpit.util.SecuritySystemConfig;
import ch.ivyteam.enginecockpit.util.SynchronizationLogger;
import ch.ivyteam.ivy.security.ISecurityContext;

@ManagedBean
@ViewScoped
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
    loadSecuritySystems();
  }
  
  private void loadSecuritySystems() 
  {
    systems = SecuritySystemConfig.getConfigurationNames(SecuritySystemConfig.SECURITY_SYSTEMS).stream()
            .map(securitySystem -> new SecuritySystem(securitySystem, 
                    getSecurityContextForSecuritySystem(securitySystem), getAppsForSecuritySystem(securitySystem)))
            .collect(Collectors.toList());
    addIvySecuritySystem();
    
  }
  
  private void addIvySecuritySystem()
  {
    List<String> appsForSecuritySystem = getAppsForIvySecuritySystem();
    if (!appsForSecuritySystem.isEmpty())
    {
      systems.add(new SecuritySystem(SecuritySystemConfig.IVY_SECURITY_SYSTEM, getSecurityContextForIvySecuritySystem(), appsForSecuritySystem));
    }
  }
  
  private Optional<ISecurityContext> getSecurityContextForIvySecuritySystem()
  {
    Optional<ISecurityContext> securityContextForSecuritySystem = getSecurityContextForSecuritySystem("");
    if (securityContextForSecuritySystem.isPresent())
    {
      return securityContextForSecuritySystem;
    }
    return getSecurityContextForSecuritySystem(SecuritySystemConfig.IVY_SECURITY_SYSTEM);
  }
  
  private Optional<ISecurityContext> getSecurityContextForSecuritySystem(String securitySystem)
  {
    return managerBean.getIApplicaitons().stream()
            .filter(app -> StringUtils.equals(getSecuritySystemNameFromAppConfig(app.getName()), securitySystem))
            .findFirst()
            .map(app -> app.getSecurityContext());
  }
  
  private List<String> getAppsForIvySecuritySystem()
  {
    List<String> appsForSecuritySystem = getAppsForSecuritySystem("");
    appsForSecuritySystem.addAll(getAppsForSecuritySystem(SecuritySystemConfig.IVY_SECURITY_SYSTEM));
    return appsForSecuritySystem;
  }
  
  private List<String> getAppsForSecuritySystem(String securitySystem)
  {
    return managerBean.getIApplicaitons().stream()
            .filter(app -> StringUtils.equals(getSecuritySystemNameFromAppConfig(app.getName()), securitySystem))
            .map(app -> app.getName())
            .collect(Collectors.toList());
  }
  
  private String getSecuritySystemNameFromAppConfig(String appName)
  {
    return SecuritySystemConfig.getConfiguration(SecuritySystemConfig.getAppConfigPrefix(appName));
  }

  public List<SecuritySystem> getSecuritySystems()
  {
    return systems;
  }
  
  public Collection<String> getAvailableSecuritySystems()
  {
    Collection<String> names = SecuritySystemConfig.getConfigurationNames(SecuritySystemConfig.SECURITY_SYSTEMS);
    names.add(SecuritySystemConfig.IVY_SECURITY_SYSTEM);
    return names;
  }
  
  public void triggerSynchronization(List<String> appNames)
  {
    appNames.forEach(appName -> triggerSynchronization(appName));
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
    Optional<SecuritySystem> findAny = systems.stream().filter(s -> s.getAppNames().contains(managerBean.getSelectedApplication().getName())).findAny();
    if (!findAny.isPresent())
    {
      return true;
    }
    return findAny.get().getSecuritySystemProvider().equals(SecuritySystemConfig.IVY_SECURITY_SYSTEM);
  }
  
  public boolean isSyncRunningForSelectedApp()
  {
    return isSyncRunning(managerBean.getSelectedApplication().getName());
  }
  
  public boolean isSyncRunning(List<String> appNames)
  {
    return appNames.stream().anyMatch(appName -> isSyncRunning(appName) == true);
  }

  public boolean isSyncRunning(String appName)
  {
    if (StringUtils.isBlank(appName))
    {
      return false;
    }
    return managerBean.getManager().findApplication(appName).getSecurityContext().isSynchronizationRunning();
  }
  
  public boolean isAnySyncRunning()
  {
    return managerBean.getIApplicaitons().stream()
            .filter(app -> app.getSecurityContext().isSynchronizationRunning() == true)
            .findAny().isPresent();
  }
  
  public boolean isNewLogAwailable()
  {
    return synchronizationLogger.isNewLogAwailable();
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
