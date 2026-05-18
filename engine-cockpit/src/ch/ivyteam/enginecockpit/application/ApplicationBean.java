package ch.ivyteam.enginecockpit.application;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.faces.application.FacesMessage;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import jakarta.faces.context.FacesContext;

import org.apache.commons.lang3.Strings;

import ch.ivyteam.enginecockpit.application.model.Application;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.application.ReleaseState;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.application.app.NewApplication;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISecurityManager;

@Named
@ViewScoped
public class ApplicationBean {

  private String nameFilter = "";
  private boolean activateOnCreate = true;

  private final Application applicationTemplate = new Application();
  private final ManagerBean managerBean;

  public ApplicationBean() {
    managerBean = ManagerBean.instance();
  }

  public List<ApplicationRow> getApplicationRows() {
    var securityContextName = selectedSecurityContextName();
    if (securityContextName == null) {
      return List.of();
    }
    return managerBean.getApplications().stream()
        .filter(app -> app.getSecSystem().equals(securityContextName))
        .filter(app -> matchesNameFilter(app.getName()))
        .collect(Collectors.groupingBy(Application::getName))
        .entrySet().stream()
        .map(entry -> ApplicationRow.from(entry.getKey(), entry.getValue()))
        .sorted(Comparator.comparing(ApplicationRow::getName, String.CASE_INSENSITIVE_ORDER))
        .collect(Collectors.toList());
  }

  public void createApplication() {
    try {
      var securityContext = ISecurityManager.instance()
          .securityContexts()
          .get(applicationTemplate.getSecSystem());
      IApplicationRepository
          .of(securityContext)
          .create(NewApplication.create(applicationTemplate.getName()).toNewApplication());
      reloadApplications();
    } catch (RuntimeException ex) {
      FacesContext.getCurrentInstance().addMessage(
          "applicationMessage",
          new FacesMessage(FacesMessage.SEVERITY_ERROR, Ivy.cm().co("/common/Error"), ex.getMessage()));
    }
  }

  public void reloadApplications() {
    managerBean.reloadApplications();
    nameFilter = "";
  }

  public Application getApplicationTemplate() {
    return applicationTemplate;
  }

  public boolean isActivateOnCreate() {
    return activateOnCreate;
  }

  public void setActivateOnCreate(boolean activateOnCreate) {
    this.activateOnCreate = activateOnCreate;
  }

  public String getNameFilter() {
    return nameFilter;
  }

  public void setNameFilter(String nameFilter) {
    this.nameFilter = nameFilter;
  }

  private String selectedSecurityContextName() {
    var selected = managerBean.getSelectedSecuritySystem();
    return selected == null ? null : selected.getSecurityContext().getName();
  }

  private boolean matchesNameFilter(String name) {
    return nameFilter == null || nameFilter.isBlank() || Strings.CI.contains(name, nameFilter);
  }

  public static class ApplicationRow {

    private final String name;
    private final String releasedVersions;
    private final String archivedVersions;

    private ApplicationRow(String name, String releasedVersions, String archivedVersions) {
      this.name = name;
      this.releasedVersions = releasedVersions;
      this.archivedVersions = archivedVersions;
    }

    static ApplicationRow from(String name, List<Application> versions) {
      return new ApplicationRow(
          name,
          versionsWithState(versions, ReleaseState.RELEASED),
          archivedOrDeprecatedVersions(versions));
    }

    private static String versionsWithState(List<Application> apps, ReleaseState state) {
      return apps.stream()
          .filter(app -> app.getReleaseState() == state)
          .map(Application::version)
          .distinct().sorted()
          .map(String::valueOf)
          .collect(Collectors.joining(", "));
    }

    private static String archivedOrDeprecatedVersions(List<Application> apps) {
      return apps.stream()
          .filter(app -> app.getReleaseState() == ReleaseState.ARCHIVED
              || app.getReleaseState() == ReleaseState.DEPRECATED)
          .map(Application::version)
          .distinct().sorted()
          .map(String::valueOf)
          .collect(Collectors.joining(", "));
    }

    public String getName() {
      return name; 
    }

    public String getReleasedVersions() {
      return releasedVersions;
    }
    
    public String getArchivedVersions() {
      return archivedVersions;
    }
  }
}
