package ch.ivyteam.enginecockpit.application.model;

import java.util.List;

import ch.ivyteam.enginecockpit.application.ApplicationBean;
import ch.ivyteam.enginecockpit.util.DateUtil;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.ReleaseState;
import ch.ivyteam.ivy.application.restricted.IProcessModelVersionInternal;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.workflow.IWorkflowContext;

public class ProcessModelVersion extends AbstractActivity {

  private final IProcessModelVersionInternal pmv;
  private final String lastChangeDate;
  private final Library lib;
  private int runningCasesCount = -1;

  public ProcessModelVersion(IProcessModelVersion pmv) {
    this(pmv, null);
  }

  public ProcessModelVersion(IProcessModelVersion pmv, ApplicationBean bean) {
    super(pmv.getVersionName(), pmv.getId(), pmv, bean);
    lib = new Library();
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
  public boolean isNotConvertable() {
    return !pmv.canBeConverted();
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
  public void convert() {
    execute(() -> pmv.convertProject(new ProjectConversionLog()), "convert", true);
  }

  @Override
  public boolean canConvert() {
    return true;
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
