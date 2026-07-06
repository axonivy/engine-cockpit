package ch.ivyteam.enginecockpit.application;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import ch.ivyteam.enginecockpit.application.model.ProcessModelVersion;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.security.ISecurityContextRepository;

@Named
@ViewScoped
public class ProjectBean implements Serializable {
  
  private String securityContextName;
  private String appName;
  private String appVersion;
  private String project;
  private ProcessModelVersion pmv;
  private String deployedProject;
  private List<ProcessModelVersion> dependendPmvs;
  private List<ProcessModelVersion> requriedPmvs;


  public void setProject(String project) {
    this.project = project;
  }

  public String getProject() {
    return project;
  }

  public String getContext() {
    return securityContextName;
  }

  public void setContext(String securityContextName) {
    this.securityContextName = securityContextName;
  }

  public void setApp(String appName) {
    this.appName = appName;
  }

  public String getApp() {
    return appName;
  }

  public void setVersion(String appVersion) {
    this.appVersion = appVersion;
  }

  public String getVersion() {
    return appVersion;
  }

  public void onload() {
    var securityContext = ISecurityContextRepository.instance().all().stream()
        .filter(context -> securityContextName.equals(context.getName()))
        .findAny()
        .orElse(null);
    if (securityContext == null) {
      ResponseHelper.notFound("Security context not found: " + securityContextName);
      return;
    }

    var apps = IApplicationRepository.of(securityContext);
    var app = apps.all().stream()
          .filter(a -> a.getName().equals(appName))
          .filter(a -> String.valueOf(a.getVersion()).equals(appVersion))
          .findAny()
          .orElse(null);
    if (app == null) {
      ResponseHelper.notFound("Application '" + appName + "' with version '" + appVersion + "' not found");
      return;
    }

    var iPmv = app.findProcessModelVersion(project);
    if (iPmv == null) {
      ResponseHelper.notFound("Process Model Version '" + project + "' for version '" + appVersion + "' in app '" + appName + "' not found");
      return;
    }

    pmv = new ProcessModelVersion(iPmv);
    dependendPmvs = iPmv.getAllDependentProcessModelVersions()
        .map(ProcessModelVersion::new)
        .collect(Collectors.toList());
    requriedPmvs = iPmv.getAllRequiredProcessModelVersions()
        .map(ProcessModelVersion::new)
        .collect(Collectors.toList());
  }

  public ProcessModelVersion getPmv() {
    return pmv;
  }

  public String getDeployedProject() {
    return deployedProject;
  }

  public List<ProcessModelVersion> getDependentPmvs() {
    return dependendPmvs;
  }

  public List<ProcessModelVersion> getRequiredPmvs() {
    return requriedPmvs;
  }

  public String getApplicationDetailLink() {
    return ApplicationDetailLink.getApplicationDetailLink(appName, securityContextName);
  }
}
