package ch.ivyteam.enginecockpit.security.system;

import static ch.ivyteam.ivy.security.ISecurityContext.DEFAULT;
import static ch.ivyteam.ivy.security.ISecurityContext.SYSTEM;

import java.util.Collection;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.security.ISecurityConstants;
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
    return ISecurityManager.instance().securityContexts().all().stream()
            .filter(s -> !SYSTEM.equals(s.getName()))
            .map(s -> new SecuritySystem(s))
            .filter(s -> !isDefaultWithNoApps(s))
            .toList();
  }

  public static boolean isDefaultWithNoApps(SecuritySystem system) {
    return DEFAULT.equals(system.getSecuritySystemName()) && system.getAppNames().isEmpty();
  }

  public List<SecuritySystem> getSecuritySystems() {
    return systems;
  }

  public Collection<String> getAvailableSecuritySystems() {
    return systems.stream()
            .map(s -> s.getSecuritySystemName())
            .toList();
  }

  public void triggerSyncForSelectedSecuritySystem() {
    managerBean.getSelectedSecuritySystem().getSecurityContext().triggerSynchronization();
  }

  public void triggerSyncForSelectedApp() {
    managerBean.getSelectedApplication().getSecuritySystem().getSecurityContext().triggerSynchronization();
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
    return managerBean.getSelectedApplication().getSecuritySystem().getSecurityContext().isSynchronizationRunning();
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
