package ch.ivyteam.enginecockpit.system;

import java.text.NumberFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.TabChangeEvent;

import ch.ivyteam.enginecockpit.application.model.Application;
import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.enginecockpit.security.system.SecurityBean;
import ch.ivyteam.enginecockpit.security.system.SecuritySystemConfig;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.application.restricted.IEnvironment;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.security.ISecurityConstants;
import ch.ivyteam.ivy.security.ISecurityManager;
import ch.ivyteam.ivy.security.ISession;

@SuppressWarnings({"restriction", "removal"})
@ManagedBean
@SessionScoped
public class ManagerBean {

  private List<SecuritySystem> securitySystems = List.of();
  private int selectedSecuritSystemIndex;

  private List<Application> applications = List.of();
  private int selectedApplicationIndex;

  private Map<Long, List<String>> environments = new HashMap<>();
  private String selectedEnvironment;

  private Locale formattingLocale;

  private IApplicationRepository apps = IApplicationRepository.instance();
  private ISecurityManager securityManager = ISecurityManager.instance();

  public ManagerBean() {
    reloadSecuritySystems();
    reloadApplications();
    var session = ISession.current();
    formattingLocale = Locale.ENGLISH;
    if (session != null) {
      formattingLocale = session.getFormattingLocale();
    }
  }

  public void reloadEnvironments() {
    if (!applications.isEmpty()) {
      if (StringUtils.isBlank(selectedEnvironment)) {
        selectedEnvironment = StringUtils.defaultString(getSelectedIApplication().getActiveEnvironment(),
                IEnvironment.DEFAULT_ENVIRONMENT_NAME);
      }
      for (IApplication iApplication : getIApplications()) {
        environments.put(iApplication.getId(), iApplication.getEnvironmentsSortedByName().stream()
                .map(e -> e.getName()).collect(Collectors.toList()));
      }
    }
  }

  public void reloadSecuritySystems() {
    securitySystems = SecurityBean.readSecuritySystems();
  }

  public List<SecuritySystem> getSecuritySystems() {
    reloadSecuritySystems();
    return securitySystems;
  }

  public void reloadApplications() {
    int appCount = applications.size();
    applications = getIApplications().stream()
            .map(app -> new Application(app))
            .collect(Collectors.toList());
    if (selectedApplicationIndex != 0 && appCount != applications.size()) {
      selectedApplicationIndex = 0;
    }
    reloadEnvironments();
  }

  public List<Application> getApplications() {
    reloadApplications();
    return applications;
  }

  public int getSelectedApplicationIndex() {
    return selectedApplicationIndex;
  }

  public void setSelectedApplicationIndex(int index) {
    selectedApplicationIndex = index;
  }

  public void setSelectedSecuritSystemIndex(int selectedSecuritSystemIndex) {
    this.selectedSecuritSystemIndex = selectedSecuritSystemIndex;
  }

  public int getSelectedSecuritSystemIndex() {
    return selectedSecuritSystemIndex;
  }

  public SecuritySystem getSelectedSecuritySystem() {
    return securitySystems.get(selectedSecuritSystemIndex);
  }

  public void updateSelectedApplication(TabChangeEvent<?> event) {
    setSelectedApplicationIndex(0);
    for (var app : applications) {
      if (app.getName().equals(event.getTab().getTitle())) {
        setSelectedApplicationIndex(applications.indexOf(app));
      }
    }
  }

  public void updateSelectedSecuritySystem(TabChangeEvent<?> event) {
    for (var securitySystem : securitySystems) {
      if (securitySystem.getSecuritySystemName().equals(event.getTab().getTitle())) {
        setSelectedSecuritSystemIndex(securitySystems.indexOf(securitySystem));
      }
    }
  }

  public void setSelectedApplicationName(String appName) {
    for (int i = 0; i < applications.size(); i++) {
      if (applications.get(i).getName().equals(appName)) {
        setSelectedApplicationIndex(i);
        return;
      }
    }
  }

  public String getSelectedApplicationName() {
    var selectedApplication = getSelectedApplication();
    if (selectedApplication == null) {
      return "";
    }
    return selectedApplication.getName();
  }

  public IApplicationRepository apps() {
    return apps;
  }

  public Application getSelectedApplication() {
    if (applications.isEmpty()) {
      return null;
    }
    return applications.get(selectedApplicationIndex);
  }

  public IApplication getSelectedIApplication() {
    if (applications.isEmpty()) {
      return null;
    }
    return getIApplication(getSelectedApplication().getId());
  }

  public IApplication getIApplication(long id) {
    return apps.findById(id).orElse(null);
  }

  public List<IApplication> getIApplications() {
    return apps.all().stream()
            .filter(app -> !app.isSystem())
            .sorted(Comparator.comparing(IApplication::getName, String.CASE_INSENSITIVE_ORDER))
            .collect(Collectors.toList());
  }

  public String getSessionCount() {
    return formatNumber(securityManager.getSessionCount());
  }

  public String getApplicationCount() {
    return formatNumber(getIApplications().size());
  }

  public String getUsersCount() {
    return formatNumber(securityManager.getUsersCount());
  }

  public String getRunningCasesCount() {
    return formatNumber(getApplications().stream().mapToLong(a -> a.getRunningCasesCount()).sum());
  }

  public boolean isIvySecuritySystemForSelectedSecuritySystem() {
    return isIvySecuritySystem(getSelectedSecuritySystem());
  }

  public static boolean isIvySecuritySystem(SecuritySystem securitySystem) {
    return SecuritySystemConfig.IVY_SECURITY_SYSTEM
            .equals(securitySystem.getSecurityContext().getExternalSecuritySystemName());
  }

  public boolean isJndiSecuritySystem() {
    return isJndiSecuritySystem(getSelectedSecuritySystem());
  }

  public static boolean isJndiSecuritySystem(SecuritySystem securitySystem) {
    var name = securitySystem.getSecurityContext().getExternalSecuritySystemName();
    return ISecurityConstants.MICROSOFT_ACTIVE_DIRECTORY_SECURITY_SYSTEM_PROVIDER_NAME.equals(name) ||
            ISecurityConstants.NOVELL_E_DIRECTORY_SECURITY_SYSTEM_PROVIDER_NAME.equals(name);
  }

  public boolean isIvySecuritySystemForSelectedApp() {
    return isIvySecuritySystem(getSelectedApplication().getSecuritySystem());
  }

  public List<String> getEnvironments() {
    var app = getSelectedApplication();
    if (app == null) {
      return List.of();
    }
    return environments.get(app.getId());
  }

  public void setSelectedEnvironment(String environment) {
    selectedEnvironment = environment;
  }

  public String getSelectedEnvironment() {
    var app = getSelectedApplication();
    if (app != null && environments.get(app.getId()).contains(selectedEnvironment)) {
      return selectedEnvironment;
    }
    return IEnvironment.DEFAULT_ENVIRONMENT_NAME;
  }

  public IEnvironment getSelectedIEnvironment() {
    return getSelectedIApplication().findEnvironment(getSelectedEnvironment());
  }

  public boolean isRestartEngine() {
    return IConfiguration.instance().isEngineRestartNeeded();
  }

  public String formatNumber(long count) {
    return NumberFormat.getInstance(formattingLocale).format(count);
  }

  public static ManagerBean instance() {
    var context = FacesContext.getCurrentInstance();
    return context
            .getApplication()
            .evaluateExpressionGet(context, "#{managerBean}", ManagerBean.class);
  }
}
