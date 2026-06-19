package ch.ivyteam.enginecockpit.application;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import ch.ivyteam.enginecockpit.application.model.Application;
import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.enginecockpit.util.DateUtil;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISecurityContext;

@Named
@ViewScoped
public class ApplicationDetailBean implements Serializable {

  private String securityContextName;
  private String appName;
  private Application selectedVersion;
  private String appVersion;

  private final ManagerBean managerBean;

  public ApplicationDetailBean() {
    managerBean = ManagerBean.instance();
  }

  public void setSecurityContextName(String securityContextName) {
    this.securityContextName = securityContextName;
  }

  public String getSecurityContextName() {
    return securityContextName;
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

    var securityContext = selectedSecurityContext();
    selectedVersion = resolveVersion(securityContext);

    if (selectedVersion == null) {
      ResponseHelper.notFound(Ivy.cm().content("/common/NotFoundApplication")
          .replace("application", appName)
          .get());
      return;
    }

    appVersion = selectedVersion.getVersion();
  }

  private Application resolveVersion(ISecurityContext securityContext) {
    if (securityContext == null || appName == null || appName.isBlank()) {
      return null;
    }

    var candidates = IApplicationRepository.of(securityContext).all().stream()
        .filter(iapp -> appName.equals(iapp.getName()))
        .map(Application::new)
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

  private ISecurityContext selectedSecurityContext() {
    if (securityContextName != null && !securityContextName.isBlank()) {
      return managerBean.getSecuritySystems().stream()
          .filter(system -> securityContextName.equals(system.getSecuritySystemName()))
          .map(SecuritySystem::getSecurityContext)
          .findFirst()
          .orElse(null);
    }
    var selected = managerBean.getSelectedSecuritySystem();
    return selected == null ? null : selected.getSecurityContext();
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

  private IApplication iApplication() {
    if (selectedVersion == null) {
      return null;
    }
    return managerBean.getIApplication(selectedVersion.getId());
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
