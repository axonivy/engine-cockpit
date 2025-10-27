package ch.ivyteam.enginecockpit.application.model;

import java.util.List;

import ch.ivyteam.enginecockpit.application.ApplicationBean;
import ch.ivyteam.ivy.application.IProcessModel;
import ch.ivyteam.ivy.application.ReleaseState;
import ch.ivyteam.ivy.application.restricted.IApplicationInternal;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.workflow.IWorkflowContext;
import ch.ivyteam.ivy.workflow.restricted.WorkflowContextInternal;

public class ProcessModel implements AppTreeItem {
  private final IProcessModel pm;
  private Boolean isOverrideProject;
  private long runningCasesCount = -1;
  private List<String> deletable;

  public ProcessModel(IProcessModel pm, ApplicationBean bean) {
    this.pm = pm;
  }

  @Override
  public String getName() {
    return "Version " + String.valueOf(pm.getVersion());
  }

  @Override
  public boolean isPm() {
    return true;
  }

  @Override
  public String getDetailView() {
    return "#";
  }

  @SuppressWarnings("restriction")
  public long getRunningCasesCount() {
    if (runningCasesCount < 0) {
      var wf = (WorkflowContextInternal) IWorkflowContext.current();
      runningCasesCount = wf.getRunningCasesCount(pm);
    }
    return runningCasesCount;
  }

  public String getIcon() {
    return isOverrideProject() ? "move-to-bottom" : "module-three-2";
  }

  public String getIconTitle() {
    return isOverrideProject() ? "This PM is configured as strict override project" : getActivityType();
  }

  public long getApplicationId() {
    // return ((IProcessModel) activity).getApplication().getId();
    return pm.getApplication().getId();
  }

  public String getActivityType() {
    return AbstractActivity.PM;
  }

  public void delete() {
    // execute(() -> pm.getApplication().deleteProcessModel(getName()), "delete", false);
  }

  public void forceDelete() {
    // execute(() -> ((IApplicationInternal) pm.getApplication()).forceDeleteProcessModel(getName()), "force delete", false);
  }

  public List<String> isDeletable() {
    if (deletable == null) {
      deletable = pm.isDeletableInternal();
    }
    return deletable;
  }

  @SuppressWarnings("restriction")
  public boolean hasReleasedProcessModelVersion() {
    var processModel = (ch.ivyteam.ivy.application.internal.ProcessModel) pm;
    // return processModel.hasReleasedProcessModelVersion();
    return false;
  }

  @Override
  public ReleaseState getReleaseState() {
    return pm.getReleaseState();
  }

  @Override
  public String getReleaseStateIcon() {
    return switch (getReleaseState()) {
      case RELEASED -> "check-circle-1";
      case DEPRECATED -> "delete";
      case ARCHIVED -> "archive";
      case CREATED, PREPARED -> "advertising-megaphone-2";
      default -> "question-circle";
    };
  }

  public String getWarningMessageForNoReleasedPmv() {
    return Ivy.cm().co("/applications/WarningMessageForNoReleasedPmv");
  }

  @SuppressWarnings("restriction")
  public boolean isOverrideProject() {
    if (isOverrideProject == null) {
      ((IApplicationInternal) pm.getApplication()).getConfiguration().getOrDefault("OverrideProject");
      // var projectId = pm.getProcessModelVersions().stream()
      // .map(IProcessModelVersion::getLibrary)
      // .filter(Objects::nonNull)
      // .map(ILibrary::getId)
      // .distinct()
      // .findFirst()
      // .orElse(null);
      // var projectId = null;
      isOverrideProject = false;// projectId != null && projectId.equals(overrideProject);
    }
    return isOverrideProject;
  }
}
