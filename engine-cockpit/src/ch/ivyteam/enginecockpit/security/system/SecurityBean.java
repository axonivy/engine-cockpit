package ch.ivyteam.enginecockpit.security.system;

import static ch.ivyteam.ivy.security.ISecurityContext.DEFAULT;
import static ch.ivyteam.ivy.security.ISecurityContext.SYSTEM;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.security.ISecurityConstants;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityManager;
import ch.ivyteam.ivy.security.identity.core.IdentityProviderRegistry;
import ch.ivyteam.ivy.security.identity.spi.IdentityProvider;
import ch.ivyteam.ivy.security.internal.SecurityContext;

@ManagedBean
@ViewScoped
public class SecurityBean {

  private List<SecuritySystem> systems;
  private List<SecuritySystem> filteredSystems;
  private String filter;

  private String newSecuritySystemName;
  private String newSecuritySystemProvider;

  private ManagerBean managerBean;

  public SecurityBean() {
    managerBean = ManagerBean.instance();
    loadSecuritySystems();
  }

  private void loadSecuritySystems() {
    systems = readSecuritySystems();
  }

  public static List<SecuritySystem> readSecuritySystems() {
    var securitySystems = readAllSecurityContexts().map(SecuritySystem::new).collect(Collectors.toList());
    if (securitySystems.size() > 1) {
      return securitySystems.stream().filter(s -> !isDefaultWithNoApps(s)).collect(Collectors.toList());
    }
    return securitySystems;
  }

  private static Stream<ISecurityContext> readAllSecurityContexts() {
    return ISecurityManager.instance().securityContexts().all().stream()
            .filter(s -> !SYSTEM.equals(s.getName()));
  }

  public static boolean isDefaultWithNoApps(SecuritySystem system) {
    return DEFAULT.equals(system.getSecuritySystemName()) && system.getAppNames().isEmpty();
  }

  public List<SecuritySystem> getSecuritySystems() {
    return systems;
  }

  public Collection<String> getAvailableSecuritySystems() {
    return readAllSecurityContexts()
            .map(s -> s.getName())
            .collect(Collectors.toList());
  }

  public void triggerSyncForSelectedSecuritySystem() {
    managerBean.getSelectedSecuritySystem().getSecurityContext().triggerSynchronization();
  }

  public void triggerSyncForSelectedApp() {
    managerBean.getSelectedApplication().getSecurityContext().triggerSynchronization();
  }

  public boolean isIvySecurityForSelectedApp() {
    return managerBean.isIvySecuritySystemForSelectedApp();
  }

  public boolean isIvySecurityForSelectedSecuritySystem() {
    return managerBean.isIvySecuritySystemForSelectedSecuritySystem();
  }

  public boolean isSyncRunningForSelectedSecuritySystem() {
    return managerBean.getSelectedSecuritySystem().getSecurityContext().isSynchronizationRunning();
  }

  public boolean isSyncRunningForSelectedApp() {
    return managerBean.getSelectedApplication().getSecurityContext().isSynchronizationRunning();
  }

  public boolean isAnySyncRunning() {
    return systems.stream()
            .anyMatch(system -> system.getSecurityContext().isSynchronizationRunning());
  }

  public String getNewSecuritySystemName() {
    return newSecuritySystemName;
  }

  public void setNewSecuritySystemName(String name) {
    this.newSecuritySystemName = name;
  }

  public String getNewSecuritySystemProvider() {
    return newSecuritySystemProvider;
  }

  public void setNewSecuritySystemProvider(String provider) {
    this.newSecuritySystemProvider = provider;
  }

  public List<IdentityProvider> getProviders() {
    return IdentityProviderRegistry.all();
  }

  public void createNewSecuritySystem() {
    try {
      var securityContext = (SecurityContext) ISecurityManager.instance().securityContexts().create(newSecuritySystemName);
      securityContext.config().setProperty(ISecurityConstants.PROVIDER_CONFIG_KEY, newSecuritySystemProvider);
      loadSecuritySystems();
    } catch (IllegalArgumentException ex) {
      FacesContext.getCurrentInstance().addMessage("msgs",
              new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage()));
    }
  }

  public List<SecuritySystem> getFilteredSystems() {
    return filteredSystems;
  }

  public void setFilteredSystems(List<SecuritySystem> filteredSystems) {
    this.filteredSystems = filteredSystems;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }
}
