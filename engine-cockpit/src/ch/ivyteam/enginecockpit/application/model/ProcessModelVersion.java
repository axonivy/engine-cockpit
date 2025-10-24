package ch.ivyteam.enginecockpit.application.model;

import java.util.List;

import ch.ivyteam.enginecockpit.application.ApplicationBean;
import ch.ivyteam.enginecockpit.util.DateUtil;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.restricted.IProcessModelVersionInternal;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.workflow.IWorkflowContext;

public class ProcessModelVersion implements AppTreeItem {

  private final IProcessModelVersionInternal pmv;
  private final String lastChangeDate;
  private final Library lib;
  private int runningCasesCount = -1;

  public ProcessModelVersion(IProcessModelVersion pmv) {
    this(pmv, null);
  }

  @Override
  public String getName() {
    return pmv.getName();
  }

  public ProcessModelVersion(IProcessModelVersion pmv, ApplicationBean bean) {
    lib = new Library();
    lastChangeDate = DateUtil.formatDate(pmv.getLastChangeDate());
    this.pmv = (IProcessModelVersionInternal) pmv;
    updateStats();
  }

  public String getDetailView() {
    return "xx";
    // return "pmv-detail.xhtml?appName=" + pmv.getApplication().getName() + "&pmName="
    // + pmv.getProcessModel().getName() + "&pmvVersion=" + pmv.getVersionNumber();
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

  public boolean isPmv() {
    return true;
  }

  public boolean isNotConvertable() {
    return !pmv.canBeConverted();
  }

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

  public boolean isReleasable() {
    // return getState().is(ReleaseState.DEPRECATED, ReleaseState.ARCHIVED, ReleaseState.PREPARED);
    return true;
  }

  public List<String> isDeletable() {
    // return pmv.isDeletable();
    return List.of();
  }

  public long getApplicationId() {
    return pmv.getProcessModel().getApplication().getId();
  }

  public long getProcessModelId() {
    return pmv.getProcessModel().getId();
  }

  public String getActivityType() {
    return AbstractActivity.PMV;
  }

  public String getQualifiedVersion() {
    return lib.version;
  }

  public String getLastChangeDate() {
    return lastChangeDate;
  }

  public String getLibraryId() {
    return lib.id;
  }

  public boolean isLibraryResolved() {
    return lib.resolved;
  }

  public int getProjectVersion() {
    return pmv.projectVersion();
  }

  public String getLibraryResolvedTooltip() {
    return isLibraryResolved() ? Ivy.cm().co("/pmvDetail/AllDirectIndirectRequiredLibrariesAvailableMessage")
        : Ivy.cm().co("/pmvDetail/NotAllDirectIndirectRequiredLibrariesAvailableMessage");
  }

  private void countRunningCases() {
    if (pmv != null && runningCasesCount < 0) {
      runningCasesCount = IWorkflowContext.current().getRunningCasesCount(pmv);
    }
  }

  private static class Library {
    private final String id = "Unknown id";
    private final String version = "Unknown version";
    private final boolean resolved = false;

    private Library() {
      // if (lib != null) {
      // id = lib.getId();
      // version = lib.getQualifiedVersion().getRawVersion();
      // resolved = lib.isResolved();
      // }
    }
  }
}
