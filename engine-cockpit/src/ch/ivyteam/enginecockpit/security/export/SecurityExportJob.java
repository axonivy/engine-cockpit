package ch.ivyteam.enginecockpit.security.export;

import java.io.IOException;

import org.primefaces.model.StreamedContent;

import ch.ivyteam.ivy.job.IJob;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISession;

@SuppressWarnings("restriction")
public class SecurityExportJob implements IJob {

  private volatile int progress = 0;
  private SecurityExport securityExport;

  public SecurityExportJob(ISecurityContext securityContext, ISession session) {
    this.securityExport = new SecurityExport(securityContext, session);
  }

  @Override
  public void execute(){
    try {
      securityExport.export();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public String getName() {
    return "Security Export Job";
  }

  public StreamedContent getResult() {
    return securityExport.getResult();
  }

  public int getProgress() {
    progress = securityExport.getProgress();
    return progress;
  }

  public void setProgress(int i) {
    progress = i;
  }
}
