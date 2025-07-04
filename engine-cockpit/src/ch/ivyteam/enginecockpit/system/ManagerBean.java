package ch.ivyteam.enginecockpit.system;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.TabChangeEvent;

import ch.ivyteam.enginecockpit.application.model.Application;
import ch.ivyteam.enginecockpit.application.model.WebServiceProcess;
import ch.ivyteam.enginecockpit.monitor.log.LogView;
import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.enginecockpit.security.system.SecurityBean;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.security.ISecurityManager;
import ch.ivyteam.ivy.security.ISession;

@SuppressWarnings({"restriction"})
@ManagedBean
@SessionScoped
public class ManagerBean {

  private List<SecuritySystem> securitySystems = List.of();
  private int selectedSecuritSystemIndex;

  private List<Application> applications = List.of();
  private int selectedApplicationIndex;

  private Locale formattingLocale;

  private final IApplicationRepository apps = IApplicationRepository.instance();
  private final ISecurityManager securityManager = ISecurityManager.instance();

  public ManagerBean() {
    reloadSecuritySystems();
    reloadApplications();
    var session = ISession.current();
    formattingLocale = Locale.ENGLISH;
    if (session != null) {
      formattingLocale = session.getFormattingLocale();
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
        .map(Application::new)
        .collect(Collectors.toList());
    if (selectedApplicationIndex != 0 && appCount != applications.size()) {
      selectedApplicationIndex = 0;
    }
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
    reloadSecuritySystems();
    if (securitySystems.size() <= selectedSecuritSystemIndex) {
      selectedSecuritSystemIndex = 0;
    }
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

  public List<WebServiceProcess> getWebServiceProcessesOfCurrentApp() {
    var app = getSelectedApplication();
    if (app == null) {
      return new ArrayList<>();
    }
    return app.getWebServiceProcesses();
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
    return formatNumber(getApplications().stream().mapToLong(Application::getRunningCasesCount).sum());
  }

  public boolean isIvySecuritySystemForSelectedSecuritySystem() {
    return SecuritySystem.isIvySecuritySystem(getSelectedSecuritySystem().getSecurityContext());
  }

  public boolean isIvySecuritySystemForSelectedApp() {
    return SecuritySystem.isIvySecuritySystem(getSelectedApplication().getSecurityContext());
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

  public String getConfigLogUrl() {
    return LogView.uri().fileName("config").toUri();
  }
}
