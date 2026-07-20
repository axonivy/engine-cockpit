package ch.ivyteam.enginecockpit.application;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Strings;

import ch.ivyteam.enginecockpit.application.model.AppStateDto;
import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.enginecockpit.util.DateUtil;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.app.ApplicationRepository;
import ch.ivyteam.ivy.application.app.state.ActivityState;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityContextRepository;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.ws.rs.core.UriBuilder;

@Named
@ViewScoped
public class ApplicationVersionBean implements Serializable {

  private String contextName;
  private String appName;
  private int appVersion;

  private String nameFilter = "";
  private AppStateDto appState;
  
  private IApplication app;
  private List<ProjectRow> projects;
  private ISecurityContext context;
 
  public void setContext(String contextName) {
    this.contextName = contextName;
  }

  public String getContext() {
    return contextName;
  }

  public void setApp(String appName) {
    this.appName = appName;
  }

  public String getApp() {
    return appName;
  }

  public void setVersion(int appVersion) {
    this.appVersion = appVersion;
  }

  public int getVersion() {
    return appVersion;
  }

  public void onload() {
    context = ISecurityContextRepository.instance().get(contextName);
    if (context == null) {
      ResponseHelper.notFound("Security context not found: " + contextName);
      return;
    }

    app =  ApplicationRepository.of(context).findByNameAndVersion(appName, appVersion).orElse(null);
    if (app == null) {
      ResponseHelper.notFound("Application not found: " + appName);
      return;
    }

    projects = app.projects().all()
        .map(this::toProjectRow)
        .sorted(Comparator.comparing(ProjectRow::name, String.CASE_INSENSITIVE_ORDER))
        .collect(Collectors.toList());

    appState = new AppStateDto(app.state());
  }

  public ProjectRow toProjectRow(IProcessModelVersion pmv) {
    return new ProjectRow(
            pmv.name(),
            pmv.getLibraryVersion(),
        DateUtil.formatDate(pmv.getLastChangeDate()),
        ProjectBean.getLink(contextName, appName, appVersion, pmv.name()));
  }

  public static String getLink(String context, String app, int version) {
    return UriBuilder.fromPath("application-version.xhtml")
        .queryParam("context", context)
        .queryParam("app", app)
        .queryParam("version", version)
        .build()
        .toString();
  }

  public String getLink() {
    return getLink(contextName, appName, appVersion);
  }

  public String getApplicationLink() {
    return ApplicationBean.getLink(contextName, appName);
  }

  public List<ProjectRow> getProjectRows() {
    return projects.stream()
        .filter(row -> matchesNameFilter(row.name()))
        .collect(Collectors.toList());
  }

  public String getNameFilter() {
    return nameFilter;
  }

  public void setNameFilter(String nameFilter) {
    this.nameFilter = nameFilter;
  }

  public IApplication getApplication() {
    return app;
  }

  public String getInstallDir() {
    return app.paths().install().toString();
  }

  public String getDataDir() {
    return app.paths().data().toString();
  }

  public String getConfigDir() {
    return app.paths().config().toString();
  }

  private boolean matchesNameFilter(String name) {
    return nameFilter == null || nameFilter.isBlank() || Strings.CI.contains(name, nameFilter);
  }

  public SecuritySystem getSecuritySystem() {
    return new SecuritySystem(context);
  }

  public boolean isDeactivatable() {
    return app.state().canChangeTo(ActivityState.INACTIVE);
  }

  public boolean isActivatable() {
    return app.state().canChangeTo(ActivityState.ACTIVE);
  }

  public AppStateDto getState() {
    return appState;
  }

  public void activate() {
    execute(() -> app.state().activate(), "activate");
  }

  public void deactivate() {
    execute(() -> app.state().deactivate(), "deactivate");
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

  public static record ProjectRow(String name, String version, String lastChanged, String link) {

    public String getName() {
      return name; 
    }

    public String getVersion() {
      return version; 
    }

    public String getLastChanged() {
      return lastChanged; 
    }

    public String getLink() {
      return link;
    }
  }
}
