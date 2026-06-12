package ch.ivyteam.enginecockpit.application;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.Strings;

import ch.ivyteam.enginecockpit.application.model.Application;
import ch.ivyteam.enginecockpit.commons.ContentFilter;
import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.configuration.model.ConfigProperty;
import ch.ivyteam.enginecockpit.configuration.model.ConfigViewImpl;
import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.enginecockpit.util.DateUtil;
import ch.ivyteam.ivy.application.ActivityState;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.ReleaseState;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.application.app.NewApplication;
import ch.ivyteam.ivy.configuration.restricted.ConfigValueFormat;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.workflow.StandardProcessType;
import ch.ivyteam.ivy.workflow.standard.DefaultPagesConfigurator;
import ch.ivyteam.ivy.workflow.standard.StandardProcessStartFinder;

@ManagedBean
@ViewScoped
public class ApplicationDetailBean {

  private String appName;
  private String appVersion;

  private Application selectedVersion;
  private ConfigViewImpl configView;

  private final ManagerBean managerBean;

  public ApplicationDetailBean() {
    managerBean = ManagerBean.instance();
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppVersion(String appVersion) {
    this.appVersion = appVersion;
  }

  public String getAppVersion() {
    return appVersion;
  }

  public void onload() {
    managerBean.reloadApplications();
    selectedVersion = resolveVersion();

    if (selectedVersion == null) {
      ResponseHelper.notFound(Ivy.cm().content("/common/NotFoundApplication")
          .replace("application", appName)
          .get());
      return;
    }

    appVersion = selectedVersion.getVersion();
    configView = buildConfigView();
  }

  public List<VersionRow> getVersionRows() {
    return managerBean.getApplications().stream()
        .filter(app -> appName != null && appName.equals(app.getName()))
        .sorted(Comparator.comparingInt(Application::version).reversed())
        .map(VersionRow::new)
        .collect(Collectors.toList());
  }

  public List<ProjectRow> getProjectRows() {
    if (selectedVersion == null) {
      return List.of();
    }
    return selectedVersion.app().getProcessModelVersions()
        .map(pmv -> new ProjectRow(
            pmv.getName(),
            pmv.getLibraryVersion(),
            DateUtil.formatDate(pmv.getLastChangeDate())))
        .sorted(Comparator.comparing(ProjectRow::getName, String.CASE_INSENSITIVE_ORDER))
        .collect(Collectors.toList());
  }

  public Application getApplication() {
    return selectedVersion;
  }

  public SecuritySystem getSecuritySystem() {
    if (selectedVersion != null) {
      return new SecuritySystem(selectedVersion.getSecurityContext());
    }
    return managerBean.getSelectedSecuritySystem();
  }

  public void deleteProject(String projectName) {
    if (selectedVersion == null || projectName == null || projectName.isBlank()) {
      return;
    }

    var project = selectedVersion.app().findProcessModelVersion(projectName);
    if (project == null) {
      FacesContext.getCurrentInstance().addMessage(
          "applicationMessage",
          new FacesMessage(FacesMessage.SEVERITY_ERROR, Ivy.cm().co("/common/Error"),
              "Project '" + projectName + "' not found."));
      return;
    }

    try {
      selectedVersion.app().delete(project);
      managerBean.reloadApplications();
      selectedVersion = resolveVersion();
      configView = selectedVersion != null ? buildConfigView() : null;
      Message.info()
          .clientId("applicationMessage")
          .summary(Ivy.cm().co("/common/Delete"))
          .detail(projectName)
          .show();
    } catch (RuntimeException ex) {
      FacesContext.getCurrentInstance().addMessage(
          "applicationMessage",
          new FacesMessage(FacesMessage.SEVERITY_ERROR, Ivy.cm().co("/common/Error"), ex.getMessage()));
    }
  }

  public ConfigViewImpl getConfigView() {
    return configView;
  }

  public String getLatestVersion() {
    return managerBean.getApplications().stream()
        .filter(app -> appName != null && appName.equals(app.getName()))
        .map(Application::version)
        .max(Comparator.naturalOrder())
        .map(String::valueOf)
        .orElse("");
  }

  public void createVersion() {
    if (selectedVersion == null) {
      return;
    }
    try {
      IApplicationRepository
          .of(selectedVersion.getSecurityContext())
          .create(NewApplication.create(appName).toNewApplication());
      managerBean.reloadApplications();
    } catch (RuntimeException ex) {
      FacesContext.getCurrentInstance().addMessage(
          "applicationMessage",
          new FacesMessage(FacesMessage.SEVERITY_ERROR, Ivy.cm().co("/common/Error"), ex.getMessage()));
    }
  }

  public void reloadConfig() {
    var app = iApplication();
    app.config().reload();
    Message.info()
        .clientId("applicationMessage")
        .summary(Ivy.cm().content("/configuration/ReloadApplicationConfigurationMessage")
            .replace("application", app.getName())
            .get())
        .show();
  }

  private Application resolveVersion() {
    var candidates = managerBean.getApplications().stream()
        .filter(app -> appName != null && appName.equals(app.getName()))
        .collect(Collectors.toList());

    if (candidates.isEmpty()) {
      return null;
    }
    if (appVersion != null && appVersion.matches("\\d+")) {
      var requested = Integer.parseInt(appVersion);
      return candidates.stream()
          .filter(app -> app.version() == requested)
          .findAny()
          .orElseGet(() -> latestOf(candidates));
    }
    return latestOf(candidates);
  }

  private static Application latestOf(List<Application> candidates) {
    return candidates.stream()
        .max(Comparator.comparingInt(Application::version))
        .orElse(candidates.get(0));
  }

  private ConfigViewImpl buildConfigView() {
    return new ConfigViewImpl(
        iApplication().config().get(),
        this::enrichWithEnumerationValues,
        List.of(
            ConfigViewImpl.defaultFilter(),
            hideByPrefix("Variables",         "/configuration/ShowVariablesMessage"),
            hideByPrefix("Databases",         "/configuration/ShowDatabasesMessage"),
            hideByPrefix("RestClients",       "/configuration/ShowRestClientsMessage"),
            hideByPrefix("WebServiceClients", "/configuration/ShowWebServiceClientsMessage")));
  }

  private ConfigProperty enrichWithEnumerationValues(ConfigProperty property) {
    if (Strings.CS.startsWith(property.getKey(), "StandardProcess")) {
      property.setConfigValueFormat(ConfigValueFormat.ENUMERATION);
      property.setEnumerationValues(() -> standardProcessLibraries(property));
    }
    if (Objects.equals(property.getKey(), "OverrideProject")) {
      property.setConfigValueFormat(ConfigValueFormat.ENUMERATION);
      property.setEnumerationValues(() -> librariesOf(managerBean.getSelectedIApplication()));
    }
    return property;
  }

  private List<String> standardProcessLibraries(ConfigProperty config) {
    var configurator = DefaultPagesConfigurator.of(iApplication());
    var libraries = new LinkedHashSet<String>();
    libraries.add("");
    libraries.add(StandardProcessStartFinder.AUTO);
    libraries.add(config.getValue());
    Arrays.stream(StandardProcessType.values())
        .forEach(type -> libraries.addAll(configurator.findLibraries(type)));
    return List.copyOf(libraries);
  }

  private static List<String> librariesOf(IApplication app) {
    var libs = new LinkedList<String>();
    libs.add("");
    app.getProcessModelVersions()
        .map(pmv -> pmv.getLibraryId())
        .distinct()
        .forEach(libs::add);
    return libs;
  }

  private static ContentFilter<ConfigProperty> hideByPrefix(String prefix, String messageKey) {
    return new ContentFilter<>(
        prefix,
        Ivy.cm().co(messageKey),
        property -> !Strings.CI.startsWith(property.getKey(), prefix + "."),
        true);
  }

  private IApplication iApplication() {
    if (selectedVersion == null) {
      return null;
    }
    return managerBean.getIApplication(selectedVersion.getId());
  }

  public static class VersionRow {

    private final Application application;

    public VersionRow(Application application) {
      this.application = application;
    }

    public Application getApplication() {
      return application;
    }

    public String getVersion() {
      return application.getVersion();
    }

    public String getReleaseState() {
      return application.getReleaseState().toString();
    }

    public String getReleaseStateIcon() {
      return application.getReleaseStateIcon();
    }

    public String getActivityState() {
      return application.app().getActivityState().toString();
    }

    public String getActivityStateIcon() {
      return switch (application.app().getActivityState()) {
        case ACTIVE   -> "ti ti-player-play";
        case INACTIVE -> "ti ti-player-stop";
        case LOCKED   -> "ti ti-lock";
        default       -> "ti ti-help-circle";
      };
    }

    public long getRunningCasesCount() { return application.getRunningCasesCount(); }
    public boolean isActive() { return application.app().getActivityState() == ActivityState.ACTIVE; }
    public boolean isReleasable() { return application.getReleaseState() != ReleaseState.RELEASED; }
  }

  public static class ProjectRow {

    private final String name;
    private final String version;
    private final String lastChanged;

    public ProjectRow(String name, String version, String lastChanged) {
      this.name = name;
      this.version = version;
      this.lastChanged = lastChanged;
    }

    public String getName() {
      return name;
    }
    public String getVersion() {
      return version;
    }
    public String getLastChanged() {
      return lastChanged;
    }
  }
}
