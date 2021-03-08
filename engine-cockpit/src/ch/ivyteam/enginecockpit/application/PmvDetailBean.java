package ch.ivyteam.enginecockpit.application;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.application.model.LibSpecification;
import ch.ivyteam.enginecockpit.application.model.ProcessModelVersion;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.ProcessModelVersionRelation;

@ManagedBean
@ViewScoped
public class PmvDetailBean
{
  private String appName;
  private String pmName;
  private String pmvVersion;

  private ManagerBean managerBean;
  private ProcessModelVersion pmv;
  private String deployedProject;
  private List<ProcessModelVersion> dependendPmvs;
  private List<ProcessModelVersion> requriedPmvs;
  private List<LibSpecification> requiredSpecifications;

  public PmvDetailBean()
  {
    var context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
  }

  public void setPmvVersion(String pmvVersion)
  {
    this.pmvVersion = pmvVersion;
    reloadDetailPmv();
  }

  public String getPmvVersion()
  {
    return pmvVersion;
  }

  public void setAppName(String appName)
  {
    this.appName = appName;
    reloadDetailPmv();
  }

  public String getAppName()
  {
    return appName;
  }

  public void setPmName(String pmName)
  {
    this.pmName = pmName;
    reloadDetailPmv();
  }

  public String getPmName()
  {
    return pmName;
  }

  private void reloadDetailPmv()
  {
    if (this.appName != null && this.pmName != null && this.pmvVersion != null)
    {
      managerBean.reloadApplications();
      IProcessModelVersion iPmv = managerBean.getManager().findProcessModelVersion(appName, pmName,
              Integer.parseInt(pmvVersion));
      pmv = new ProcessModelVersion(iPmv);
      deployedProject = iPmv.getLibrary().getId();
      dependendPmvs = iPmv.getAllRelatedProcessModelVersions(ProcessModelVersionRelation.DEPENDENT).stream()
              .map(dep -> new ProcessModelVersion(dep)).collect(Collectors.toList());
      requriedPmvs = iPmv.getAllRelatedProcessModelVersions(ProcessModelVersionRelation.REQUIRED).stream()
              .map(req -> new ProcessModelVersion(req)).collect(Collectors.toList());
      requiredSpecifications = iPmv.getLibrary().getRequiredLibrarySpecifications().stream()
              .map(spec -> new LibSpecification(spec)).collect(Collectors.toList());
    }
  }

  public ProcessModelVersion getPmv()
  {
    return pmv;
  }

  public String getDeployedProject()
  {
    return deployedProject;
  }

  public List<ProcessModelVersion> getDependentPmvs()
  {
    return dependendPmvs;
  }

  public List<ProcessModelVersion> getRequiredPmvs()
  {
    return requriedPmvs;
  }

  public List<LibSpecification> getRequiredSpecifications()
  {
    return requiredSpecifications;
  }

}
