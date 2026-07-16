package ch.ivyteam.enginecockpit.application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Strings;

import ch.ivyteam.enginecockpit.application.model.NewApplication;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.app.ApplicationRepository;
import ch.ivyteam.ivy.application.app.state.ReleaseState;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityManager;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class ApplicationsBean implements Serializable {

  private String nameFilter = "";

  private final NewApplication newApplication = new NewApplication();

  private List<ApplicationRow> applications = new ArrayList<>();

  public void onload() {
    applications = load();
  }

  public void reload() {
    onload();
    nameFilter = "";
  }

  public List<ApplicationRow> getApplications() {
    return applications.stream()
        .filter(app -> matchesNameFilter(app.getName()))
        .collect(Collectors.toList());
  }

  private List<ApplicationRow> load() {
    var securityContext = selectedSecurityContext();
    if (securityContext == null) {
      return List.of();
    }

    return ApplicationRepository.of(securityContext).all().stream()
        .collect(Collectors.groupingBy(IApplication::name))
        .entrySet().stream()
        .map(entry -> new ApplicationRowConverter(entry.getKey(), entry.getValue()).convert())
        .sorted(Comparator.comparing(ApplicationRow::getName, String.CASE_INSENSITIVE_ORDER))
        .collect(Collectors.toList());
  }

  public void createApplication() {
    try {
      var securityContext = ISecurityManager.instance().securityContexts()
          .get(newApplication.getSecurityContextName());
      ApplicationRepository.of(securityContext)
          .create(ch.ivyteam.ivy.application.app.NewApplication.create(newApplication.getAppName()).toNewApplication());
      reload();
    } catch (RuntimeException ex) {
      FacesContext.getCurrentInstance().addMessage(
          "applicationMessage",
          new FacesMessage(FacesMessage.SEVERITY_ERROR, Ivy.cm().co("/common/Error"), ex.getMessage()));
    }
  }

  public void initDefaultSecuritySystem() {
    newApplication.setSecurityContextName(selectedSecurityContext().getName());
  }

  public NewApplication getNewApplication() {
    return newApplication;
  }

  public String getNameFilter() {
    return nameFilter;
  }

  public void setNameFilter(String nameFilter) {
    this.nameFilter = nameFilter;
  }

  private ISecurityContext selectedSecurityContext() {
    var selected = ManagerBean.instance().getSelectedSecuritySystem();
    return selected == null ? null : selected.getSecurityContext();
  }

  private boolean matchesNameFilter(String name) {
    return nameFilter == null || nameFilter.isBlank() || Strings.CI.contains(name, nameFilter);
  }

  public static class ApplicationRow {

    private final String name;
    private final ApplicationVersion releasedVersion;
    private final List<ApplicationVersion> deprecatedVersions;

    private ApplicationRow(String name, ApplicationVersion releasedVersion, List<ApplicationVersion> deprecatedVersions) {
      this.name = name;
      this.releasedVersion = releasedVersion;
      this.deprecatedVersions = deprecatedVersions;
    }

    public String getName() {
      return name;
    }

    public ApplicationVersion getReleasedVersion() {
      return releasedVersion;
    }

    public List<ApplicationVersion> getDeprecatedVersions() {
      return deprecatedVersions;
    }
  }

  public static class ApplicationVersion {

    private final String context;
    private final String app;
    private final String version;

    public ApplicationVersion(String context, String app, String version) {
      this.context = context;
      this.app = app;
      this.version = version;
    }

    public String getVersion() {
      return version;
    }

    public String getLink() {
      return ApplicationVersionBean.getLink(context, app, Integer.parseInt(version));
    }

    @Override
    public String toString() {
      return version;
    }
  }

  private static class ApplicationRowConverter {

    private final String name;
    private final List<IApplication> apps;

    public ApplicationRowConverter(String name, List<IApplication> apps) {
      this.name = name;
      this.apps = apps;
    }

    private ApplicationRow convert() {
      return new ApplicationRow(
          name,
          findReleasedVersion(),
          findDeprecatedVersions());
    }

    private ApplicationVersion findReleasedVersion() {
      return apps.stream()
          .filter(app -> app.state().releaseState() == ReleaseState.RELEASED)
          .findAny()
          .map(this::toApplicationVersion)
          .orElse(null);
    }

    private List<ApplicationVersion> findDeprecatedVersions() {
      return apps.stream()
          .filter(app -> app.state().releaseState() == ReleaseState.DEPRECATED)
          .sorted(Comparator.comparingInt(IApplication::version))
          .map(this::toApplicationVersion)
          .collect(Collectors.toList());
    }

    private ApplicationVersion toApplicationVersion(IApplication app) {
      return new ApplicationVersion(
          app.securityContext().name(),
          app.name(),
          String.valueOf(app.version()));
    }
  }
}
