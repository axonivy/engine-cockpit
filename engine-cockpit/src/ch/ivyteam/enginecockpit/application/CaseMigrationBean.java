package ch.ivyteam.enginecockpit.application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ch.ivyteam.ivy.application.app.Application;
import ch.ivyteam.ivy.application.app.ApplicationRepository;
import ch.ivyteam.ivy.security.ISecurityContextRepository;
import ch.ivyteam.ivy.security.exec.Sudo;
import ch.ivyteam.ivy.workflow.ICase;
import ch.ivyteam.ivy.workflow.IWorkflowContext;
import ch.ivyteam.ivy.workflow.caze.CaseBusinessState;
import ch.ivyteam.ivy.workflow.query.CaseQuery;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class CaseMigrationBean implements Serializable {

  // cases moved per poll request, so the progress updates while the migration runs
  private static final int BATCH_SIZE = 20;

  private Application source;
  private Application target;
  private List<Integer> targetVersions = List.of();
  private int targetVersion;
  private List<Long> caseIds = List.of();
  private int moved;
  private int failed;
  private final List<String> logs = new ArrayList<>();

  public void prepare(String contextName, String appName, int version) {
    var context = ISecurityContextRepository.instance().get(contextName);
    var apps = ApplicationRepository.of(context);
    source = apps.findByNameAndVersion(appName, version).orElseThrow();
    targetVersions = apps.findByName(appName)
        .map(Application::version)
        .filter(v -> v != version)
        .sorted(Comparator.reverseOrder())
        .toList();
    if (!targetVersions.isEmpty()) {
      targetVersion = targetVersions.getFirst();
    }
    target = null;
    caseIds = List.of();
    moved = 0;
    failed = 0;
    logs.clear();
  }

  public void start() {
    target = ApplicationRepository.of(source.securityContext())
        .findByNameAndVersion(source.name(), targetVersion).orElseThrow();
    var executor = IWorkflowContext.of(source.securityContext()).getCaseQueryExecutor();
    caseIds = Sudo.get(() -> CaseQuery.create(executor).where()
        .isBusinessCase()
        .and().applicationId().isEqual(source.id())
        .and().businessState().isEqual(CaseBusinessState.OPEN)
        .executor().results().stream()
        .map(ICase::getId)
        .toList());
    logs.add("Moving " + caseIds.size() + " open business cases from version " + source.version()
        + " to version " + target.version());
    finishIfDone();
  }

  public void step() {
    var workflow = IWorkflowContext.of(source.securityContext());
    var from = getProcessed();
    for (var id : caseIds.subList(from, Math.min(from + BATCH_SIZE, caseIds.size()))) {
      try {
        Sudo.run(() -> workflow.findCase(id).move(target));
        moved++;
        logs.add("Case " + id + " moved");
      } catch (RuntimeException ex) {
        failed++;
        logs.add(ex.getMessage());
      }
    }
    finishIfDone();
  }

  private void finishIfDone() {
    if (!isRunning()) {
      logs.add("Finished: " + moved + " moved, " + failed + " failed");
    }
  }

  public boolean isStarted() {
    return target != null;
  }

  public boolean isRunning() {
    return isStarted() && getProcessed() < caseIds.size();
  }

  public int getProcessed() {
    return moved + failed;
  }

  public int getTotal() {
    return caseIds.size();
  }

  public int getFailed() {
    return failed;
  }

  public int getProgress() {
    if (caseIds.isEmpty()) {
      return 100;
    }
    return getProcessed() * 100 / caseIds.size();
  }

  public String getLogs() {
    return String.join("\n", logs);
  }

  public List<Integer> getTargetVersions() {
    return targetVersions;
  }

  public int getTargetVersion() {
    return targetVersion;
  }

  public void setTargetVersion(int targetVersion) {
    this.targetVersion = targetVersion;
  }
}
