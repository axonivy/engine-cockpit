package ch.ivyteam.enginecockpit.system;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.TabChangeEvent;

import ch.ivyteam.enginecockpit.application.model.Application;
import ch.ivyteam.enginecockpit.security.system.SecuritySystemConfig;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.IApplicationConfigurationManager;
import ch.ivyteam.ivy.application.IApplicationInternal;
import ch.ivyteam.ivy.application.restricted.IEnvironment;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.security.ISecurityManager;
import ch.ivyteam.ivy.security.ISession;

@SuppressWarnings("restriction")
@ManagedBean
@SessionScoped
public class ManagerBean {
  private List<Application> applications = Collections.emptyList();
  private int selectedApplicationIndex;

  private Map<Long, List<String>> environments = new HashMap<>();
  private String selectedEnvironment;

  private boolean hideDashboadWarnings;
  private Locale formattingLocale;

  private IApplicationConfigurationManager manager = IApplicationConfigurationManager.instance();
  private ISecurityManager securityManager = ISecurityManager.instance();

  public ManagerBean() {
    reloadApplications();
    hideDashboadWarnings = BooleanUtils.toBoolean(System.getProperty("hide.dashboard.warnings"));
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

  public void updateSelectedApplication(TabChangeEvent event) {
    setSelectedApplicationIndex(0);
    for (var app : applications) {
      if (app.getName().equals(event.getTab().getTitle())) {
        setSelectedApplicationIndex(applications.indexOf(app));
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

  public IApplicationConfigurationManager getManager() {
    return manager;
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
    return manager.getApplication(getSelectedApplication().getId());
  }

  public IApplication getIApplication(long id) {
    return manager.getApplication(id);
  }

  public List<IApplication> getIApplications() {
    return manager.getApplicationsSortedByName(false);
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

  public Locale getDefaultEmailLanguageForSelectedApp() {
    return getSelectedIApplication().getDefaultEMailLanguage();
  }

  public List<SelectItem> getSupportedLanguagesWithDefault() {
    var appLanguage = getSelectedIApplication().getDefaultEMailLanguage();
    var languages = new ArrayList<SelectItem>();
    languages.add(new SelectItem("app", "Application default (" + appLanguage.getDisplayLanguage() + ")"));
    languages.addAll(getSupportedLanguages());
    return languages;
  }

  public List<SelectItem> getSupportedLanguages() {
    var locales = new ArrayList<Locale>();
    locales.add(Locale.ENGLISH);
    locales.add(Locale.GERMAN);
    locales.add(Locale.FRENCH);
    IApplicationInternal app = (IApplicationInternal) getSelectedIApplication();
    if (app != null) {
      locales.addAll(app.getLanguages());
    }
    return locales.stream()
            .distinct()
            .collect(Collectors.toMap(Locale::getLanguage, l -> l, (l1, l2) -> l1)).values().stream()
            .map(l -> new SelectItem(l.getLanguage(), l.getDisplayLanguage()))
            .collect(Collectors.toList());
  }

  public boolean isIvySecuritySystem() {
    return SecuritySystemConfig.IVY_SECURITY_SYSTEM
            .equals(getSelectedIApplication().getSecurityContext().getExternalSecuritySystemName());
  }

  public List<String> getEnvironments() {
    Application app = getSelectedApplication();
    if (app == null) {
      return List.of();
    }
    return environments.get(app.getId());
  }

  public void setSelectedEnvironment(String environment) {
    selectedEnvironment = environment;
  }

  public String getSelectedEnvironment() {
    if (environments.get(getSelectedApplication().getId()).contains(selectedEnvironment)) {
      return selectedEnvironment;
    }
    return IEnvironment.DEFAULT_ENVIRONMENT_NAME;
  }

  public IEnvironment getSelectedIEnvironment() {
    return getSelectedIApplication().findEnvironment(getSelectedEnvironment());
  }

  public boolean hideDashboardWarnings() {
    return hideDashboadWarnings;
  }

  public boolean isRestartEngine() {
    return IConfiguration.instance().isEngineRestartNeeded();
  }

  public String formatNumber(long count) {
    return NumberFormat.getInstance(formattingLocale).format(count);
  }

}
