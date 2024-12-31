package ch.ivyteam.enginecockpit.application.model;

import java.util.List;
import java.util.Objects;

import ch.ivyteam.enginecockpit.application.ApplicationBean;
import ch.ivyteam.ivy.application.IApplicationInternal;
import ch.ivyteam.ivy.application.ILibrary;
import ch.ivyteam.ivy.application.IProcessModel;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.workflow.IWorkflowContext;

public class ProcessModel extends AbstractActivity {
  private final IProcessModel pm;
  private Boolean isOverrideProject;
  private long runningCasesCount = -1;

  public ProcessModel(IProcessModel pm, ApplicationBean bean) {
    super(pm.getName(), pm.getId(), pm, bean);
    this.pm = pm;
  }

  @Override
  public boolean isPm() {
    return true;
  }

  @Override
  public long getRunningCasesCount() {
    countRunningCases();
    return runningCasesCount;
  }

  @Override
  public String getIcon() {
    return isOverrideProject() ? "move-to-bottom" : "module-three-2";
  }

  @Override
  public String getIconTitle() {
    return isOverrideProject() ? "This PM is configured as strict override project" : getActivityType();
  }

  @Override
  public long getApplicationId() {
    return ((IProcessModel) activity).getApplication().getId();
  }

  @Override
  public String getActivityType() {
    return AbstractActivity.PM;
  }

  @Override
  public void delete() {
    execute(() -> pm.getApplication().deleteProcessModel(getName()), "delete", false);
  }

  @Override
  public void forceDelete() {
    execute(() -> ((IApplicationInternal) pm.getApplication()).forceDeleteProcessModel(getName()), "force delete", false);
  }

  @Override
  public List<String> isDeletable() {
    return pm.isDeletableInternal();
  }

  @Override
  @SuppressWarnings("restriction")
  public boolean hasReleasedProcessModelVersion() {
    var processModel = (ch.ivyteam.ivy.application.internal.ProcessModel) pm;
    return processModel.hasReleasedProcessModelVersion();
  }

  @Override
  public String getWarningMessageForNoReleasedPmv() {
    return "No released process model version";
  }

  @SuppressWarnings("restriction")
  public boolean isOverrideProject() {
    if (isOverrideProject == null) {
      var overrideProject = ((IApplicationInternal) pm.getApplication()).getConfiguration().getOrDefault("OverrideProject");
      var projectId = pm.getProcessModelVersions().stream()
          .map(IProcessModelVersion::getLibrary)
          .filter(Objects::nonNull)
          .map(ILibrary::getId)
          .distinct()
          .findFirst()
          .orElse(null);
      isOverrideProject = projectId != null && projectId.equals(overrideProject);
    }
    return isOverrideProject;
  }

  private void countRunningCases() {
    if (pm != null && runningCasesCount < 0) {
      var wf = IWorkflowContext.current();
      runningCasesCount = pm.getProcessModelVersions().parallelStream()
          .mapToLong(pmv -> wf.getRunningCasesCount(pmv)).sum();
    }
  }

}
