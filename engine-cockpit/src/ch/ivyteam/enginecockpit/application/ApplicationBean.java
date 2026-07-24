package ch.ivyteam.enginecockpit.application;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Strings;

import ch.ivyteam.enginecockpit.application.model.AppStateDto;
import ch.ivyteam.enginecockpit.application.model.DeleteApplication;
import ch.ivyteam.enginecockpit.commons.ContentFilter;
import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.configuration.model.ConfigProperty;
import ch.ivyteam.enginecockpit.configuration.model.ConfigViewImpl;
import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.ivy.application.app.Application;
import ch.ivyteam.ivy.application.app.ApplicationRepository;
import ch.ivyteam.ivy.application.app.NewApplication;
import ch.ivyteam.ivy.application.app.link.AppLink;
import ch.ivyteam.ivy.application.app.state.ActivityState;
import ch.ivyteam.ivy.application.app.state.CasesCounter;
import ch.ivyteam.ivy.application.app.state.ReleaseState;
import ch.ivyteam.ivy.configuration.restricted.ConfigValueFormat;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityContextRepository;
import ch.ivyteam.ivy.workflow.StandardProcessType;
import ch.ivyteam.ivy.workflow.standard.DefaultPagesConfigurator;
import ch.ivyteam.ivy.workflow.standard.StandardProcessStartFinder;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.ws.rs.core.UriBuilder;

@Named
@ViewScoped
public class ApplicationBean implements Serializable {

  private String appName;
  private String contextName;

  private ISecurityContext context;
  private Application app;
  private ConfigViewImpl configView;

  private List<ApplicationVersionRow> applicationVersions;

  private String nameFilter = "";

  public void onload() {
    context = ISecurityContextRepository.instance().get(contextName);
    if (context == null) {
      ResponseHelper.notFound("Security context not found: " + contextName);
      return;
    }

    var apps = ApplicationRepository.of(context);
    app = apps.findReleasedByName(appName);
    if (app == null) {
      app = apps.findByName(appName).stream()
          .max(Comparator.comparingInt(Application::version))
          .orElse(null);
    }
    if (app == null) {
      ResponseHelper.notFound("Application not found: " + appName);
      return;
    }

    configView = new ConfigViewBuilder(app).build();

    applicationVersions = apps.findByName(appName).stream()
        .map(ApplicationVersionRow::new)
        .sorted(Comparator.comparing(ApplicationVersionRow::getVersion).reversed())
        .collect(Collectors.toList());
  }

  public List<ApplicationVersionRow> getApplicationVersions() {
    return applicationVersions.stream()
        .filter(version -> matchesNameFilter(version.getVersion()))
        .collect(Collectors.toList());
  }

  public Application getApplication() {
    return app;
  }

  public SecuritySystem getSecuritySystem() {
    return new SecuritySystem(context);
  }

  public ConfigViewImpl getConfigView() {
    return configView;
  }

  private DeleteApplication deleteApplication;

  public void prepareDelete(ApplicationVersionRow version) {
    var isLastVersion = applicationVersions.size() == 1;
    this.deleteApplication = new DeleteApplication(version, isLastVersion);
  }

  public void deleteSelectedApplicationVersion() {
    ApplicationRepository.of(context).delete(appName, deleteApplication.getVersion());
    if (deleteApplication.isLastVersion()) {
      try {
        FacesContext.getCurrentInstance().getExternalContext().redirect("applications.xhtml");
        FacesContext.getCurrentInstance().responseComplete();
      } catch (IOException ex) {
        throw new RuntimeException("Could not send redirect", ex);
      }      
    } else {
      onload();
    }
  }

  public DeleteApplication getDeleteApplication() {
    return deleteApplication;
  }

  public void createVersion() {
    try {
      ApplicationRepository.of(context).create(NewApplication.create(app.name()).toNewApplication());
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
        .summary(Ivy.cm().content("/configuration/ReloadApplicationConfigurationMessage").replace("application", app.name()).get())
        .show();
  }

  public String getApp() {
    return appName;
  }

  public void setApp(String appName) {
    this.appName = appName;
  }

  public String getContext() {
    return contextName;
  }

  public void setContext(String contextName) {
    this.contextName = contextName;
  }

  public static String getLink(String context, String app) {
    return UriBuilder.fromPath("application.xhtml")
        .queryParam("context", context)
        .queryParam("app", app)
        .build()
        .toString();
  }

  public String getHomeUrl() {
    return AppLink.home(app).getRelative();
  }

  public String getDevWorkflowUrl() {
    return AppLink.devWorkflow(app).getRelative();
  }

  public boolean isDisabled() {
    return app.state().releaseState() != ReleaseState.RELEASED || app.state().activityState() != ActivityState.ACTIVE;
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

    private final Application app;

    private ConfigViewBuilder(Application app) {
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

    private static List<String> librariesOf(Application app) {
      var libs = new LinkedList<String>();
      libs.add("");
      app.projects().all()
          .map(project -> project.mavenCoordinates().id())
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

    private final String name;
    private final String version;
    private final AppStateDto state;
    private final long openCases;
    private final long doneCases;
    private final String link;

    private ApplicationVersionRow(Application app) {
      this.name = app.name();
      this.version = String.valueOf(app.version());
      this.state = new AppStateDto(app.state());
      this.openCases = CasesCounter.openOf(app);
      this.doneCases = CasesCounter.doneOf(app);
      this.link = ApplicationVersionBean.getLink(app.securityContext().name(), app.name(), app.version());
    }

    public String getName() {
      return name;
    }

    public String getVersion() {
      return version;
    }

    public AppStateDto getState() {
      return state;
    }

    public long getOpenCases() {
      return openCases;
    }

    public long getDoneCases() {
      return doneCases;
    }

    public String getLink() {
      return link;
    }
  }
}
