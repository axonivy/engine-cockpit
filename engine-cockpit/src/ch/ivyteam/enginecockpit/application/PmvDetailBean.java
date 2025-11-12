package ch.ivyteam.enginecockpit.application;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.application.model.LibSpecification;
import ch.ivyteam.enginecockpit.application.model.ProcessModelVersion;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.application.ProcessModelVersionRelation;
import ch.ivyteam.ivy.application.restricted.IApplicationConfigurationManager;

@ManagedBean
@ViewScoped
@SuppressWarnings("restriction")
public class PmvDetailBean {

  private String appName;
  private String pmVersion;
  private String pmvName;
  private final ManagerBean managerBean;
  private ProcessModelVersion pmv;
  private String deployedProject;
  private List<ProcessModelVersion> dependendPmvs;
  private List<ProcessModelVersion> requriedPmvs;
  private List<LibSpecification> requiredSpecifications;

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

  public void setPmVersion(String pmVersion) {
    this.pmVersion = pmVersion;
  }

  public String getPmVersion() {
    return pmVersion;
  }

  public void onload() {
    if (appName == null) {
      ResponseHelper.notFound("appName not set");
      return;
    }
    if (pmVersion == null) {
      ResponseHelper.notFound("pmName not set");
      return;
    }
    if (pmvName == null) {
      ResponseHelper.notFound("pmvVersion not set");
      return;
    }

    managerBean.reloadApplications();
    var appMgr = IApplicationConfigurationManager.instance();
    var iPmv = appMgr.findProcessModelVersion(appName, pmvName, Integer.parseInt(pmVersion));
    if (iPmv == null) {
      ResponseHelper.notFound("Process Model Version '" + pmVersion + "' for version '" + pmvName + "' in app '" + appName + "' not found");
      return;
    }

    pmv = new ProcessModelVersion(iPmv);
    dependendPmvs = iPmv.getAllRelatedProcessModelVersions(ProcessModelVersionRelation.DEPENDENT).stream()
        .map(ProcessModelVersion::new)
        .collect(Collectors.toList());
    requriedPmvs = iPmv.getAllRelatedProcessModelVersions(ProcessModelVersionRelation.REQUIRED).stream()
        .map(ProcessModelVersion::new)
        .collect(Collectors.toList());
    // var library = iPmv.getLibrary();
    // if (library == null) {
    // return;
    // }
    // deployedProject = library.getId();
    // requiredSpecifications = library.getRequiredLibrarySpecifications().stream()
    // .map(LibSpecification::new)
    // .collect(Collectors.toList());
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

  public List<LibSpecification> getRequiredSpecifications() {
    return requiredSpecifications;
  }
}
