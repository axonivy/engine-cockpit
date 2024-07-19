package ch.ivyteam.enginecockpit.security.export;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.download.AllResourcesDownload;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityManager;
import ch.ivyteam.ivy.security.ISession;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class SecurityExportBean implements AllResourcesDownload{

  private ISecurityContext securityContext;
  private SecurityExportJob job;
  private boolean showProgressBar = false;
  private String name;

  public SecurityExportBean() {
  }

  public String getSecuritySystemName() {
    return name;
  }

  public void setSecuritySystemName(String secSystemName) {
    if (StringUtils.isBlank(name)) {
      name = secSystemName;
      securityContext = ISecurityManager.instance().securityContexts().get(name);
    }
  }

  public int getProgress() {
    if (job != null) {
      return job.getProgress();
    }
    return 0;
  }

  public void startReport() {
    job = new SecurityExportJob(securityContext, ISession.current());
    job.setProgress(0);
    Ivy.log().info("Job scheduled");
    job.schedule().once();
    setShowProgressBar(true);
  }

  public void cancel() {
    if (job != null) {
      job.cancel();
    }
  }

  @Override
  public StreamedContent getAllResourcesDownload() {
    if (job != null) {
      return job.getResult();
    }

    return null;
  }

  public String getValue() {
    return "Generate Files";
  }

  public String getDownloadValue() {
    return "Download";
  }

  public boolean setShowProgressBar(boolean show) {
    return showProgressBar = show;
  }

  public boolean getShowProgressBar() {
    return showProgressBar;
  }

  public void onComplete() {
    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Progress Completed"));
  }
}
