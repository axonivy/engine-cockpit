package ch.ivyteam.enginecockpit.security;

import java.util.Arrays;
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
  
  private String newSecuritySystemName;
  private String newSecuritySystemProvider;
  private List<String> providers = Arrays.asList("Microsoft Active Directory", "Novell eDirectory", "ivy Security System");
  
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
    List<String> appsWithExternalSecuritySystem = systems.stream().flatMap(sys -> sys.getAppNames().stream()).collect(Collectors.toList());
    List<String> appsWithIvySecuritySystem = managerBean.getApplications().stream()
            .filter(app -> !appsWithExternalSecuritySystem.contains(app.getName()))
            .map(app -> app.getName())
            .collect(Collectors.toList());
    if (!appsWithIvySecuritySystem.isEmpty())
    {
      systems.add(new SecuritySystem(SecuritySystemConfig.IVY_SECURITY_SYSTEM, Optional.empty(), appsWithIvySecuritySystem));
    }
  }
  
  private Optional<ISecurityContext> getSecurityContextForSecuritySystem(String securitySystem)
  {
    return managerBean.getIApplications().stream()
            .filter(app -> StringUtils.equals(getSecuritySystemNameFromAppConfig(app.getName()), securitySystem))
            .findFirst()
            .map(app -> app.getSecurityContext());
  }
  
  private List<String> getAppsForSecuritySystem(String securitySystem)
  {
    return managerBean.getIApplications().stream()
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
    return managerBean.isIvySecuritySystem();
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
    return managerBean.getIApplications().stream()
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
  
  public String getNewSecuritySystemName()
  {
    return newSecuritySystemName;
  }
  
  public void setNewSecuritySystemName(String name)
  {
    this.newSecuritySystemName = name;
  }
  
  public String getNewSecuritySystemProvider()
  {
    return newSecuritySystemProvider;
  }
  
  public void setNewSecuritySystemProvider(String provider)
  {
    this.newSecuritySystemProvider = provider;
  }
  
  public List<String> getProviders()
  {
    return providers;
  }
  
  public void createNewSecuritySystem()
  {
    SecuritySystemConfig.setConfiguration(SecuritySystemConfig.getConfigPrefix(newSecuritySystemName) + 
            SecuritySystemConfig.ConfigKey.PROVIDER, newSecuritySystemProvider);
    loadSecuritySystems();
  }

}
