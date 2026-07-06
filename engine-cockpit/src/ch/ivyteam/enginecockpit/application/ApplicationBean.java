package ch.ivyteam.enginecockpit.application;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import org.apache.commons.lang3.Strings;

import ch.ivyteam.enginecockpit.commons.ContentFilter;
import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.configuration.model.ConfigProperty;
import ch.ivyteam.enginecockpit.configuration.model.ConfigViewImpl;
import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.ivy.application.ActivityState;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.ReleaseState;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.application.app.NewApplication;
import ch.ivyteam.ivy.configuration.restricted.ConfigValueFormat;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityContextRepository;
import ch.ivyteam.ivy.workflow.IWorkflowContext;
import ch.ivyteam.ivy.workflow.StandardProcessType;
import ch.ivyteam.ivy.workflow.standard.DefaultPagesConfigurator;
import ch.ivyteam.ivy.workflow.standard.StandardProcessStartFinder;

@Named
@ViewScoped
public class ApplicationBean implements Serializable {

  private String appName;
  private String securityContextName;

  private ISecurityContext securityContext;
  private IApplication app;
  private ConfigViewImpl configView;

  private List<ApplicationVersionRow> applicationVersions;

  private String nameFilter = "";

  public void onload() {
    securityContext = ISecurityContextRepository.instance().all().stream()
        .filter(context -> securityContextName.equals(context.getName()))
        .findAny()
        .orElse(null);
    if (securityContext == null) {
      ResponseHelper.notFound("Security context not found: " + securityContextName);
      return;
    }

    var apps = IApplicationRepository.of(securityContext);
    app = apps.findReleasedByName(appName);

    if (app == null) {
      app = apps.all().stream()
          .filter(a -> a.getName().equals(appName))
          .max(Comparator.comparingInt(IApplication::getVersion))
          .orElse(null);
    }

    if (app == null) {
      ResponseHelper.notFound("Application not found: " + appName);
      return;
    }

    configView = new ConfigViewBuilder(app).build();

    applicationVersions = apps.all().stream()
        .filter(a -> a.getName().equals(appName))
        .map(ApplicationVersionRow::new)
        .sorted(Comparator.comparing(ApplicationVersionRow::getVersion).reversed())
        .collect(Collectors.toList());
  }

  public List<ApplicationVersionRow> getApplicationVersions() {
    return applicationVersions.stream()
        .filter(version -> matchesNameFilter(version.getVersion()))
        .collect(Collectors.toList());
  }

  public IApplication getApplication() {
    return app;
  }

  public SecuritySystem getSecuritySystem() {
    return new SecuritySystem(securityContext);
  }

  public ConfigViewImpl getConfigView() {
    return configView;
  }

  public void createVersion() {
    try {
      IApplicationRepository
          .of(securityContext)
          .create(NewApplication.create(app.getName()).toNewApplication());
      onload();
    } catch (RuntimeException ex) {
      Message.error()
          .clientId("applicationMessage")
          .summary(Ivy.cm().co("/common/Error"))
          .detail(ex.getMessage())
          .exception(ex)
          .show();
    }
  }

  public void reloadConfig() {
    app.config().reload();
    Message.info()
        .clientId("applicationMessage")
        .summary(Ivy.cm().content("/configuration/ReloadApplicationConfigurationMessage")
            .replace("application", app.getName())
            .get())
        .show();
  }

  public String getApp() {
    return appName;
  }

  public void setApp(String appName) {
    this.appName = appName;
  }

  public String getContext() {
    return securityContextName;
  }

  public void setContext(String securityContextName) {
    this.securityContextName = securityContextName;
  }

  public String getApplicationVersionLink(ApplicationVersionRow version) {
    return ApplicationDetailLink.getApplicationVersionLink(appName, securityContextName, version.getVersionNumber());
  }

  public String getNameFilter() {
    return nameFilter;
  }

  public void setNameFilter(String nameFilter) {
    this.nameFilter = nameFilter;
  }

  private boolean matchesNameFilter(String version) {
    return nameFilter == null || nameFilter.isBlank() || Strings.CI.contains(version, nameFilter);
  }

  private static class ConfigViewBuilder {
    private final IApplication app;

    private ConfigViewBuilder(IApplication app) {
      this.app = app;
    }

    private ConfigViewImpl build() {
      return new ConfigViewImpl(
          app.config().get(),
          this::enrichWithEnumerationValues,
          List.of(
              ConfigViewImpl.defaultFilter(),
              hideByPrefix("Variables", "/configuration/ShowVariablesMessage"),
              hideByPrefix("Databases", "/configuration/ShowDatabasesMessage"),
              hideByPrefix("RestClients", "/configuration/ShowRestClientsMessage"),
              hideByPrefix("WebServiceClients", "/configuration/ShowWebServiceClientsMessage")));
    }

    private ConfigProperty enrichWithEnumerationValues(ConfigProperty property) {
      if (Strings.CS.startsWith(property.getKey(), "StandardProcess")) {
        property.setConfigValueFormat(ConfigValueFormat.ENUMERATION);
        property.setEnumerationValues(() -> standardProcessLibraries(property));
      }
      if (Objects.equals(property.getKey(), "OverrideProject")) {
        property.setConfigValueFormat(ConfigValueFormat.ENUMERATION);
        property.setEnumerationValues(() -> librariesOf(app));
      }
      return property;
    }

    private List<String> standardProcessLibraries(ConfigProperty config) {
      var configurator = DefaultPagesConfigurator.of(app);
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
          .map(IProcessModelVersion::getLibraryId)
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
  }

  public static class ApplicationVersionRow {

    private final IApplication app;
    private long runningCasesCount = -1L;

    private ApplicationVersionRow(IApplication app) {
      this.app = app;
    }

    public int getVersionNumber() {
      return app.getVersion();
    }

    public String getVersion() {
      return String.valueOf(app.getVersion());
    }

    public ReleaseState getReleaseState() {
      return app.getReleaseState();
    }

    public String getReleaseStateLabel() {
      return app.getReleaseState().toString();
    }

    public String getReleaseStateIcon() {
      return switch (app.getReleaseState()) {
        case RELEASED -> "ti ti-circle-check";
        case DEPRECATED -> "ti ti-circle-half-vertical";
        case ARCHIVED -> "ti ti-archive";
        case CREATED, PREPARED -> "ti ti-speakerphone";
        default -> "ti ti-help-circle";
      };
    }

    public String getActivityStateLabel() {
      return app.getActivityState().toString();
    }

    public String getActivityStateIcon() {
      return switch (app.getActivityState()) {
        case ACTIVE -> "ti ti-player-play";
        case INACTIVE -> "ti ti-player-stop";
        default -> "ti ti-help-circle";
      };
    }

    public boolean isNotStartable() {
      return app.getActivityState() == ActivityState.ACTIVE;
    }

    public boolean isNotStopable() {
      return app.getActivityState() == ActivityState.INACTIVE;
    }

    public boolean isReleasable() {
      return app.getReleaseState() != ReleaseState.RELEASED;
    }

    public List<String> isDeletable() {
      return app.isDeletable();
    }

    public String getNotDeletableMessage() {
      return String.join("\n", isDeletable());
    }

    public long getRunningCasesCount() {
      if (runningCasesCount < 0) {
        runningCasesCount = IWorkflowContext.of(app.getSecurityContext()).getRunningCasesCount(app);
      }
      return runningCasesCount;
    }

    public void activate() {
      execute(app::activate, "activate");
    }

    public void deactivate() {
      execute(app::deactivate, "deactivate");
    }

    public void release() {
      execute(app::release, "release");
    }

    public void delete() {
      execute(
          () -> IApplicationRepository.instance().delete(app.getName(), app.getVersion()),
          "delete");
    }

    public void forceDelete() {
      delete();
    }

    private static void execute(Runnable operation, String actionKey) {
      try {
        operation.run();
      } catch (RuntimeException ex) {
        Message.error()
            .clientId("applicationMessage")
            .summary(Ivy.cm().co("/common/Error"))
            .detail(ex.getMessage())
            .exception(ex)
            .show();
      }
    }
  }
}
