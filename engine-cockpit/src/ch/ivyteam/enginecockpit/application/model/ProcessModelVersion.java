package ch.ivyteam.enginecockpit.application.model;

import java.util.List;

import javax.ws.rs.core.UriBuilder;

import ch.ivyteam.enginecockpit.application.ApplicationBean;
import ch.ivyteam.enginecockpit.util.DateUtil;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.restricted.IProcessModelVersionInternal;
import ch.ivyteam.ivy.workflow.IWorkflowContext;

public class ProcessModelVersion implements AppTreeItem {

  private final IProcessModelVersionInternal pmv;
  private final String lastChangeDate;
  private int runningCasesCount = -1;

  public ProcessModelVersion(IProcessModelVersion pmv) {
    this(pmv, null);
  }

  @Override
  public String getName() {
    return pmv.getName();
  }

  public ProcessModelVersion(IProcessModelVersion pmv, ApplicationBean bean) {
    lastChangeDate = DateUtil.formatDate(pmv.getLastChangeDate());
    this.pmv = (IProcessModelVersionInternal) pmv;
    updateStats();
  }

  @Override
  public String getDetailView() {
    return UriBuilder.fromPath("pmv-detail.xhtml")
        .queryParam("appName", pmv.getApplication().getName())
        .queryParam("pmVersion", pmv.getApplication().getVersion())
        .queryParam("pmvName", pmv.getName())
        .build()
        .toString();
  }

  public void updateStats() {
    // super.updateStats();
    // if (pmv != null) {
    //// getState().updateReleaseState(pmv.getReleaseState());
    // }
  }

  public long getRunningCasesCount() {
    countRunningCases();
    return runningCasesCount;
  }

  public String getIcon() {
    return "module-three-1";
  }

  @Override
  public boolean isPmv() {
    return true;
  }

  @Override
  public String getVersion() {
    return pmv.getLibraryVersion();
  }

  public boolean isNotConvertable() {
    return !pmv.canBeConverted();
  }

  @Override
  public void release() {
    // execute(() -> pmv.release(), "release", true);
  }

  public void delete() {
    // execute(() -> pmv.delete(), "delete", false);
  }

  public void convert() {
    // execute(() -> pmv.convertProject(new ProjectConversionLog()), "convert", true);
  }

  public boolean canConvert() {
    return true;
  }

  @Override
  public boolean isReleasable() {
    // return getState().is(ReleaseState.DEPRECATED, ReleaseState.ARCHIVED, ReleaseState.PREPARED);
    return true;
  }

  @Override
  public List<String> isDeletable() {
    // return pmv.isDeletable();
    return List.of();
  }

  public long getApplicationId() {
    return pmv.getApplication().getId();
  }

  public String getActivityType() {
    return AbstractActivity.PMV;
  }

  public String getQualifiedVersion() {
    return pmv.getLibraryVersion();
  }

  @Override
  public String getLastChangeDate() {
    return lastChangeDate;
  }

  public String getLibraryId() {
    return pmv.getLibraryId();
  }

  public int getProjectVersion() {
    return pmv.projectVersion();
  }

  private void countRunningCases() {
    if (pmv != null && runningCasesCount < 0) {
      runningCasesCount = IWorkflowContext.current().getRunningCasesCount(pmv);
    }
  }
}
