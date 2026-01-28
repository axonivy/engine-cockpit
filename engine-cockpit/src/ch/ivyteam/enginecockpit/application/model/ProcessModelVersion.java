package ch.ivyteam.enginecockpit.application.model;

import java.util.List;

import javax.ws.rs.core.UriBuilder;

import ch.ivyteam.enginecockpit.application.ApplicationBean;
import ch.ivyteam.enginecockpit.util.DateUtil;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.restricted.IProcessModelVersionInternal;
import ch.ivyteam.ivy.workflow.IWorkflowContext;

public class ProcessModelVersion extends AppTreeItem {

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

  @Override
  public String getDisplayName() {
    return getName();
  }

  public ProcessModelVersion(IProcessModelVersion pmv, ApplicationBean bean) {
    super(bean, null);
    lastChangeDate = DateUtil.formatDate(pmv.getLastChangeDate());
    this.pmv = (IProcessModelVersionInternal) pmv;
    updateStats();
  }

  @Override
  public String getDetailView() {
    return UriBuilder.fromPath("pmv-detail.xhtml")
        .queryParam("appName", pmv.getApplication().getName())
        .queryParam("appVersion", pmv.getApplication().getVersion())
        .queryParam("pmvName", pmv.getName())
        .build()
        .toString();
  }

  @Override
  public void updateStats() {
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

  @Override
  public boolean isNotConvertable() {
    return !pmv.canBeConverted();
  }

  @Override
  public void release() {    
  }

  @Override
  public void delete() {
    execute(() -> pmv.getApplication().delete(pmv), "delete", false);
  }

  @Override
  public void convert() {
    execute(() -> pmv.convertProject(new ProjectConversionLog()), "convert", true);
  }

  public boolean canConvert() {
    return true;
  }

  @Override
  public boolean isReleasable() {    
    return false;
  }

  @Override
  public List<String> isDeletable() {
    return List.of();
  }

  @Override
  public long getApplicationId() {
    return pmv.getApplication().getId();
  }

  @Override
  public long getProcessModelVersionId() {
    return pmv.getId();
  }

  @Override
  public String getActivityType() {
    return AppTreeItem.PMV;
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

  public String getLibraryVersion() {
    return pmv.getLibraryVersion();
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
