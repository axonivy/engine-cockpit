package ch.ivyteam.enginecockpit.application.model;

import java.util.List;

import ch.ivyteam.enginecockpit.application.ApplicationBean;
import ch.ivyteam.enginecockpit.util.DateUtil;
import ch.ivyteam.ivy.application.ILibrary;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.IProcessModelVersionInternal;
import ch.ivyteam.ivy.application.ReleaseState;
import ch.ivyteam.ivy.workflow.IWorkflowContext;

public class ProcessModelVersion extends AbstractActivity {

  private IProcessModelVersionInternal pmv;
  private String lastChangeDate;
  private String description;
  private Library lib;
  private int runningCasesCount = -1;

  public ProcessModelVersion(IProcessModelVersion pmv) {
    this(pmv, null);
  }

  public ProcessModelVersion(IProcessModelVersion pmv, ApplicationBean bean) {
    super(pmv.getVersionName(), pmv.getId(), pmv, bean);
    lib = new Library(pmv.getLibrary());
    description = pmv.getDescription();
    lastChangeDate = DateUtil.formatDate(pmv.getLastChangeDate());
    this.pmv = (IProcessModelVersionInternal) pmv;
    updateStats();
  }

  @Override
  public String getDetailView() {
    return "pmv-detail.xhtml?appName=" + pmv.getApplication().getName() + "&pmName="
            + pmv.getProcessModel().getName() + "&pmvVersion=" + pmv.getVersionNumber();
  }

  @Override
  public void updateStats() {
    super.updateStats();
    if (pmv != null) {
      getState().updateReleaseState(pmv.getReleaseState());
    }
  }

  @Override
  public long getRunningCasesCount() {
    countRunningCases();
    return runningCasesCount;
  }

  @Override
  public String getIcon() {
    return "module-three-1";
  }

  @Override
  public boolean isPmv() {
    return true;
  }

  @Override
  public void release() {
    execute(() -> pmv.release(), "release", true);
  }

  @Override
  public void delete() {
    execute(() -> pmv.delete(), "delete", false);
  }

  @Override
  public boolean isReleasable() {
    return getState().is(ReleaseState.DEPRECATED, ReleaseState.ARCHIVED, ReleaseState.PREPARED);
  }

  @Override
  public List<String> isDeletable() {
    return pmv.isDeletable();
  }

  @Override
  public long getApplicationId() {
    return pmv.getProcessModel().getApplication().getId();
  }

  public long getProcessModelId() {
    return pmv.getProcessModel().getId();
  }

  @Override
  public String getActivityType() {
    return AbstractActivity.PMV;
  }

  public String getQualifiedVersion() {
    return lib.version;
  }

  public String getLastChangeDate() {
    return lastChangeDate;
  }

  public String getDescription() {
    return description;
  }

  public String getLibraryId() {
    return lib.id;
  }

  public boolean isLibraryResolved() {
    return lib.resolved;
  }

  public String getLibraryResolvedTooltip() {
    return (isLibraryResolved() ? "All" : "Not all")
            + " direct and indirect required libraries are available in the system.";
  }

  private void countRunningCases() {
    if (pmv != null && runningCasesCount < 0) {
      runningCasesCount = IWorkflowContext.current().getRunningCasesCount(pmv);
    }
  }

  private static class Library {
    private String id = "Unknown id";
    private String version = "Unknown version";
    private boolean resolved = false;

    private Library(ILibrary lib) {
      if (lib != null) {
        id = lib.getId();
        version = lib.getQualifiedVersion().getRawVersion();
        resolved = lib.isResolved();
      }
    }
  }

}
