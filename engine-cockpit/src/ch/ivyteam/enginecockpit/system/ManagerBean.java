package ch.ivyteam.enginecockpit.system;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.primefaces.event.TabChangeEvent;

import ch.ivyteam.enginecockpit.application.ApplicationBean;
import ch.ivyteam.enginecockpit.monitor.log.LogView;
import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.enginecockpit.security.system.SecurityBean;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.app.ApplicationRepository;
import ch.ivyteam.ivy.application.app.state.ReleaseState;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.security.ISecurityManager;
import ch.ivyteam.ivy.security.ISession;
import ch.ivyteam.ivy.workflow.IWorkflowContext;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named
@SessionScoped
public class ManagerBean implements Serializable {

  private List<SecuritySystem> securitySystems = List.of();
  private int selectedSecuritSystemIndex;

  private List<IApplication> applications = List.of();
  private int selectedApplicationIndex;

  private Locale formattingLocale;

  private final ApplicationRepository apps = ApplicationRepository.instance();
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
    applications = loadApplications();
    if (selectedApplicationIndex != 0 && appCount != applications.size()) {
      selectedApplicationIndex = 0;
    }
  }

  public List<IApplication> getApplications() {
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
      if (app.name().equals(event.getTab().getTitle())) {
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
      if (applications.get(i).name().equals(appName)) {
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
    return selectedApplication.name();
  }

  public String getSelectedApplicationDetailLink() {
    return ApplicationBean.getLink(getSelectedSecuritySystem().getSecuritySystemName(), getSelectedApplicationName());
  }

  public IApplication getSelectedApplication() {
    if (applications.isEmpty()) {
      return null;
    }
    return applications.get(selectedApplicationIndex);
  }

  private List<IApplication> loadApplications() {
    return apps.all().stream()
        .filter(app -> app.state().releaseState() == ReleaseState.RELEASED)
        .sorted(Comparator.comparing(IApplication::name, String.CASE_INSENSITIVE_ORDER))
        .collect(Collectors.toList());
  }

  public String getSessionCount() {
    return formatNumber(securityManager.getSessionCount());
  }

  public String getApplicationCount() {
    return formatNumber(applications.size());
  }

  public String getUsersCount() {
    return formatNumber(securityManager.getUsersCount());
  }

  public String getRunningCasesCount() {
    long runningCases = getApplications().stream()
        .mapToLong(app -> IWorkflowContext.of(app.securityContext()).getRunningCasesCount(app))
        .sum();
    return formatNumber(runningCases);
  }

  public boolean isIvySecuritySystemForSelectedSecuritySystem() {
    return SecuritySystem.isIvySecuritySystem(getSelectedSecuritySystem().getSecurityContext());
  }

  public boolean isIvySecuritySystemForSelectedApp() {
    return SecuritySystem.isIvySecuritySystem(getSelectedApplication().securityContext());
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
