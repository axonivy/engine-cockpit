package ch.ivyteam.enginecockpit.application;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.Strings;

import ch.ivyteam.enginecockpit.application.model.Application;
import ch.ivyteam.enginecockpit.application.model.NewApplication;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.ReleaseState;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityManager;

@ManagedBean
@ViewScoped
public class ApplicationsBean {

  private String nameFilter = "";

  private final NewApplication newApplication = new NewApplication();
  private final ManagerBean managerBean;

  private List<ApplicationRow> applications = new ArrayList<>();

  public ApplicationsBean() {
    managerBean = ManagerBean.instance();
  }

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

    return IApplicationRepository.of(securityContext).all().stream()
      .collect(Collectors.groupingBy(IApplication::getName))
      .entrySet().stream()
      .map(entry -> new ApplicationRowConverter(entry.getKey(), entry.getValue()).convert())
      .sorted(Comparator.comparing(ApplicationRow::getName, String.CASE_INSENSITIVE_ORDER))
      .collect(Collectors.toList());
  }

  public void createApplication() {
    try {
      var securityContext = ISecurityManager.instance()
          .securityContexts()
          .get(newApplication.getSecurityContextName());
      IApplicationRepository
          .of(securityContext)
          .create(ch.ivyteam.ivy.application.app.NewApplication.create(newApplication.getAppName()).toNewApplication());
      reload();
    } catch (RuntimeException ex) {
      FacesContext.getCurrentInstance().addMessage(
          "applicationMessage",
          new FacesMessage(FacesMessage.SEVERITY_ERROR, Ivy.cm().co("/common/Error"), ex.getMessage()));
    }
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
    var selected = managerBean.getSelectedSecuritySystem();
    return selected == null ? null : selected.getSecurityContext();
  }

  private boolean matchesNameFilter(String name) {
    return nameFilter == null || nameFilter.isBlank() || Strings.CI.contains(name, nameFilter);
  }

  public static class ApplicationRow {

    private final String name;
    private final ApplicationVersion releasedVersion;
    private final List<ApplicationVersion> archivedVersions;

    private ApplicationRow(String name, ApplicationVersion releasedVersion, List<ApplicationVersion> archivedVersions) {
      this.name = name;
      this.releasedVersion = releasedVersion;
      this.archivedVersions = archivedVersions;
    }

    public String getName() {
      return name; 
    }

    public ApplicationVersion getReleasedVersion() {
      return releasedVersion;
    }
    
    public List<ApplicationVersion> getArchivedVersions() {
      return archivedVersions;
    }
  }

  public static class ApplicationVersion {

    private final String securityContextName;
    private final String applicationName;
    private final String version;

    public ApplicationVersion(String securityContextName, String applicationName, String version) {
      this.securityContextName = securityContextName;
      this.applicationName = applicationName;
      this.version = version;
    }
   
    public String getVersion() {
      return version;
    }

    public String getLink() {
      return Application.getDetailViewLink(securityContextName, applicationName, Integer.parseInt(version));
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
        findArchivedOrDeprecatedVersions());
    }
  
    private ApplicationVersion findReleasedVersion() {
      return apps.stream()
        .filter(app -> app.getReleaseState() == ReleaseState.RELEASED)
        .findAny()
        .map(this::toApplicationVersion)
        .orElse(null);
      }
      
    private List<ApplicationVersion> findArchivedOrDeprecatedVersions() {
      return apps.stream()
      .filter(app -> app.getReleaseState() == ReleaseState.ARCHIVED
      || app.getReleaseState() == ReleaseState.DEPRECATED)
      .sorted(Comparator.comparingInt(IApplication::getVersion))
      .map(this::toApplicationVersion)
      .collect(Collectors.toList());
    }
  
    private ApplicationVersion toApplicationVersion(IApplication app) {
      return new ApplicationVersion(
          app.getSecurityContext().getName(),
          app.getName(),
          String.valueOf(app.getVersion()));
    }
  }
}
