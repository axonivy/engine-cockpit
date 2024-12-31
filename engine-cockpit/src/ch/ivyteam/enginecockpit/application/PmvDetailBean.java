package ch.ivyteam.enginecockpit.application;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import ch.ivyteam.enginecockpit.application.model.LibSpecification;
import ch.ivyteam.enginecockpit.application.model.ProcessModelVersion;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.application.IApplicationConfigurationManager;
import ch.ivyteam.ivy.application.ProcessModelVersionRelation;

@ManagedBean
@ViewScoped
public class PmvDetailBean {

  private static final Logger LOGGER = Logger.getLogger(PmvDetailBean.class);
  private String appName;
  private String pmName;
  private String pmvVersion;
  private final ManagerBean managerBean;
  private ProcessModelVersion pmv;
  private String deployedProject;
  private List<ProcessModelVersion> dependendPmvs;
  private List<ProcessModelVersion> requriedPmvs;
  private List<LibSpecification> requiredSpecifications;

  public PmvDetailBean() {
    managerBean = ManagerBean.instance();
  }

  public void setPmvVersion(String pmvVersion) {
    this.pmvVersion = pmvVersion;
  }

  public String getPmvVersion() {
    return pmvVersion;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getAppName() {
    return appName;
  }

  public void setPmName(String pmName) {
    this.pmName = pmName;
  }

  public String getPmName() {
    return pmName;
  }

  public void onload() {
    if (appName == null) {
      ResponseHelper.notFound("appName not set");
      return;
    }
    if (pmName == null) {
      ResponseHelper.notFound("pmName not set");
      return;
    }
    if (pmvVersion == null) {
      ResponseHelper.notFound("pmvVersion not set");
      return;
    }

    managerBean.reloadApplications();
    var appMgr = IApplicationConfigurationManager.instance();
    var iPmv = appMgr.findProcessModelVersion(appName, pmName, Integer.parseInt(pmvVersion));
    if (iPmv == null) {
      LOGGER.warn("Can not refresh PMV details for " + appName + "/" + pmName + "/" + pmvVersion);
      ResponseHelper.notFound("Process Model Version '" + pmName + "' for version '" + pmvVersion
          + "' in app '" + appName + "' not found");
      return;
    }

    pmv = new ProcessModelVersion(iPmv);
    dependendPmvs = iPmv.getAllRelatedProcessModelVersions(ProcessModelVersionRelation.DEPENDENT).stream()
        .map(ProcessModelVersion::new)
        .collect(Collectors.toList());
    requriedPmvs = iPmv.getAllRelatedProcessModelVersions(ProcessModelVersionRelation.REQUIRED).stream()
        .map(ProcessModelVersion::new)
        .collect(Collectors.toList());
    var library = iPmv.getLibrary();
    if (library == null) {
      return;
    }
    deployedProject = library.getId();
    requiredSpecifications = library.getRequiredLibrarySpecifications().stream()
        .map(LibSpecification::new)
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

  public List<LibSpecification> getRequiredSpecifications() {
    return requiredSpecifications;
  }
}
