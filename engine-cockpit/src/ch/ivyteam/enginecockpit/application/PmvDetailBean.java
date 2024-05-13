package ch.ivyteam.enginecockpit.application;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import ch.ivyteam.enginecockpit.application.model.LibSpecification;
import ch.ivyteam.enginecockpit.application.model.ProcessModelVersion;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.application.IApplicationConfigurationManager;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.ProcessModelVersionRelation;

@ManagedBean
@ViewScoped
public class PmvDetailBean {

  private static final Logger LOGGER = Logger.getLogger(PmvDetailBean.class);

  private String appName;
  private String pmName;
  private String pmvVersion;

  private ManagerBean managerBean;
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
    reloadDetailPmv();
  }

  public String getPmvVersion() {
    return pmvVersion;
  }

  public void setAppName(String appName) {
    this.appName = appName;
    reloadDetailPmv();
  }

  public String getAppName() {
    return appName;
  }

  public void setPmName(String pmName) {
    this.pmName = pmName;
    reloadDetailPmv();
  }

  public String getPmName() {
    return pmName;
  }

  private void reloadDetailPmv() {
    if (this.appName == null ||
        this.pmName == null ||
        this.pmvVersion == null) {
      return;
    }

    managerBean.reloadApplications();
    var appMgr = IApplicationConfigurationManager.instance();
    IProcessModelVersion iPmv = appMgr.findProcessModelVersion(appName, pmName, Integer.parseInt(pmvVersion));
    if (iPmv == null) {
      LOGGER.warn("Can not refresh PMV details for "+appName+"/"+pmName+"/"+pmvVersion);
      return;
    }

    pmv = new ProcessModelVersion(iPmv);
    dependendPmvs = iPmv.getAllRelatedProcessModelVersions(ProcessModelVersionRelation.DEPENDENT).stream()
            .map(dep -> new ProcessModelVersion(dep)).collect(Collectors.toList());
    requriedPmvs = iPmv.getAllRelatedProcessModelVersions(ProcessModelVersionRelation.REQUIRED).stream()
            .map(req -> new ProcessModelVersion(req)).collect(Collectors.toList());

    var library = iPmv.getLibrary();
    if (library == null) {
      return;
    }
    deployedProject = library.getId();
    requiredSpecifications = library.getRequiredLibrarySpecifications().stream()
            .map(spec -> new LibSpecification(spec)).collect(Collectors.toList());
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
