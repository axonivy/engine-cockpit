package ch.ivyteam.enginecockpit.application.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import ch.ivyteam.enginecockpit.application.ApplicationBean;
import ch.ivyteam.ivy.application.IApplicationInternal;
import ch.ivyteam.ivy.application.ILibrary;
import ch.ivyteam.ivy.application.IProcessModel;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.ProcessModelVersionRelation;
import ch.ivyteam.ivy.workflow.IWorkflowContext;

public class ProcessModel extends AbstractActivity {
  private IProcessModel pm;
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
  public boolean isDeletable() {
    return pm.isDeletable();
  }

  @Override
  public String getDeleteHint() {
    var message = new StringBuilder();
    var dependentPmvs = getDependentPmvs();
    if (!dependentPmvs.isEmpty()) {
      message.append(dependentPmvs.size()).append(" dependent PMV(s)");
    }
    if (runningCasesCount > 0) {
      if (message.length() > 0) {
        message.append(" and ");
      }
      message.append(runningCasesCount).append(" running cases. The cases will also be deleted");
    }

    if (message.length() > 0) {
      message.insert(0, getActivityType() + " has ");
      message.append(". ");
    }
    message.append(super.getDeleteHint());

    return message.toString();
  }

  private Set<IProcessModelVersion> getDependentPmvs() {
    var dependentPMVs = new HashSet<IProcessModelVersion>();
    var pmvs = pm.getProcessModelVersions();
    pmvs.forEach(pmv -> dependentPMVs
            .addAll(pmv.getAllRelatedProcessModelVersions(ProcessModelVersionRelation.DEPENDENT)));
    return dependentPMVs;
  }

  @Override
  public boolean isProtected() {
    return getName().equals("engine-cockpit");
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
