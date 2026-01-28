package ch.ivyteam.enginecockpit.application;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.application.model.Application;
import ch.ivyteam.enginecockpit.application.model.ProcessModelVersion;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.application.ProcessModelVersionRelation;
import ch.ivyteam.ivy.application.app.IApplicationRepository;

@ManagedBean
@ViewScoped
public class PmvDetailBean {

  private String appName;
  private String appVersion;
  private String pmvName;
  private final ManagerBean managerBean;
  private ProcessModelVersion pmv;
  private String deployedProject;
  private List<ProcessModelVersion> dependendPmvs;
  private List<ProcessModelVersion> requriedPmvs;

  public PmvDetailBean() {
    managerBean = ManagerBean.instance();
  }

  public void setPmvName(String pmvName) {
    this.pmvName = pmvName;
  }

  public String getPmvName() {
    return pmvName;
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
    if (appName == null) {
      ResponseHelper.notFound("appName not set");
      return;
    }
    if (appVersion == null) {
      ResponseHelper.notFound("appVersion not set");
      return;
    }    
    if (pmvName == null) {
      ResponseHelper.notFound("pmvName not set");
      return;
    }

    managerBean.reloadApplications();

    var app = IApplicationRepository.instance().findByNameAndVersion(appName, Integer.parseInt(appVersion)).stream()
        .findAny()
        .orElse(null);
    if (app == null) {
      ResponseHelper.notFound("app notn found");
      return;
    }

    var iPmv = app.findProcessModelVersion(pmvName);
    if (iPmv == null) {
      ResponseHelper.notFound("Process Model Version '" + pmvName + "' for version '" + appVersion + "' in app '" + appName + "' not found");
      return;
    }

    pmv = new ProcessModelVersion(iPmv);
    dependendPmvs = iPmv.getAllRelatedProcessModelVersions(ProcessModelVersionRelation.DEPENDENT).stream()
        .map(ProcessModelVersion::new)
        .collect(Collectors.toList());
    requriedPmvs = iPmv.getAllRelatedProcessModelVersions(ProcessModelVersionRelation.REQUIRED).stream()
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
    return Application.getDetailViewLink(appName, Integer.parseInt(appVersion));
  }
}
