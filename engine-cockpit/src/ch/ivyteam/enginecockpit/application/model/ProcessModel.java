package ch.ivyteam.enginecockpit.application.model;

import java.util.HashSet;
import java.util.Set;

import ch.ivyteam.enginecockpit.application.ApplicationBean;
import ch.ivyteam.ivy.application.IProcessModel;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.ProcessModelVersionRelation;
import ch.ivyteam.ivy.environment.Ivy;

public class ProcessModel extends AbstractActivity {
  private long runningCasesCount;
  private IProcessModel pm;

  public ProcessModel(IProcessModel pm, ApplicationBean bean) {
    super(pm.getName(), pm.getId(), pm, bean);
    this.pm = pm;
    runningCasesCount = pm.getProcessModelVersions().stream()
            .mapToLong(pmv -> Ivy.wf().getRunningCasesCount(pmv)).sum();
  }

  @Override
  public long getRunningCasesCount() {
    return runningCasesCount;
  }

  @Override
  public String getIcon() {
    return "module-three-2";
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

}
