package ch.ivyteam.enginecockpit.application.model;

import java.util.List;

import jakarta.ws.rs.core.UriBuilder;

import ch.ivyteam.enginecockpit.application.ApplicationsBean;
import ch.ivyteam.enginecockpit.util.DateUtil;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.project.model.ProjectVersion;
import ch.ivyteam.ivy.workflow.IWorkflowContext;

public class ProcessModelVersion {

  private final IProcessModelVersion pmv;
  private final String lastChangeDate;
  private int runningCasesCount = -1;

  public ProcessModelVersion(IProcessModelVersion pmv) {
    this(pmv, null);
  }

  public String getSecurityContextName() {
    return pmv.getApplication().getSecurityContext().getName();
  }

  public String getName() {
    return pmv.getName();
  }

  public String getDisplayName() {
    return getName();
  }

  public ProcessModelVersion(IProcessModelVersion pmv, ApplicationsBean bean) {
    lastChangeDate = DateUtil.formatDate(pmv.getLastChangeDate());
    this.pmv = pmv;
    updateStats();
  }

  public String getDetailView() {
    return UriBuilder.fromPath("project.xhtml")
        .queryParam("context", pmv.getApplication().getSecurityContext().getName())
        .queryParam("app", pmv.getApplication().getName())
        .queryParam("version", pmv.getApplication().getVersion())
        .queryParam("project", pmv.getName())
        .build()
        .toString();
  }

  public void updateStats() {}

  public long getRunningCasesCount() {
    countRunningCases();
    return runningCasesCount;
  }

  public String getIcon() {
    return "ti ti-packages";
  }

  public boolean isPmv() {
    return true;
  }

  public String getVersion() {
    return pmv.getLibraryVersion();
  }

  public void release() {}

  public boolean isReleasable() {
    return false;
  }

  public List<String> isDeletable() {
    return List.of();
  }

  public String getQualifiedVersion() {
    return pmv.getLibraryVersion();
  }

  public String getLastChangeDate() {
    return lastChangeDate;
  }

  public String getLibraryId() {
    return pmv.getLibraryId();
  }

  public String getLibraryVersion() {
    return pmv.getLibraryVersion();
  }

  public int getProjectVersion() {
    return ProjectVersion.of(pmv.project()).version();
  }

  private void countRunningCases() {
    if (pmv != null && runningCasesCount < 0) {
      runningCasesCount = IWorkflowContext.current().getRunningCasesCount(pmv);
    }
  }
}
