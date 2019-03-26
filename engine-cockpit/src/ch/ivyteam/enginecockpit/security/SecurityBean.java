package ch.ivyteam.enginecockpit.security;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.SecuritySystem;
import ch.ivyteam.enginecockpit.util.SynchronizationLogger;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISecurityContext;

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
    loadSecuritySystems();
//    IConfiguration.get().getNames("SecuritySystems").forEach(sec -> Ivy.log().info(sec));
//    systems = managerBean.getIApplicaitons().stream()
//            .map(app -> new SecuritySystem(app.getSecurityContext(), app.getName()))
//            .collect(Collectors.toList());
  }
  
  private void loadSecuritySystems() 
  {
    systems = IConfiguration.get().getNames("SecuritySystems").stream()
            .map(securitySystem -> new SecuritySystem(securitySystem, 
                    getSecurityContextForSecuritySystem(securitySystem), getAppsForSecuritySystem(securitySystem)))
            .collect(Collectors.toList());
  }
  
  private Optional<ISecurityContext> getSecurityContextForSecuritySystem(String securitySystem)
  {
    return managerBean.getIApplicaitons().stream()
            .filter(app -> StringUtils.equals(getSecuritySystemNameFromAppConfig(app.getName()), securitySystem))
            .findFirst()
            .map(app -> app.getSecurityContext());
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
    String orElse = IConfiguration.get().get("Applications." + appName + ".SecuritySystem").orElse("");
    return orElse;
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
  
  public void triggerSynchronization(List<String> appNames)
  {
    Ivy.log().info("multi trigger");
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
    return findAny.get().getSecuritySystemProvider().equals("ivy Security System");
  }
  
  public boolean isSyncRunningForSelectedApp()
  {
    return isSyncRunning(managerBean.getSelectedApplication().getName());
  }
  
  public boolean isSyncRunning(List<String> appNames)
  {
    Ivy.log().info("multi sync running");
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
