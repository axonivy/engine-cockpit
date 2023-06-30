package ch.ivyteam.enginecockpit.security.system.compare;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityContextRepository;
import ch.ivyteam.ivy.security.ISecurityManager;
import ch.ivyteam.ivy.security.context.compare.Issue;
import ch.ivyteam.ivy.security.context.compare.SecurityContextComparer;
import ch.ivyteam.ivy.security.context.compare.Solver;
import ch.ivyteam.ivy.security.context.compare.Solver.Type;
import ch.ivyteam.util.collections.ConcurrentHashSet;

@ManagedBean
@ViewScoped
public class SecuritySystemCompareBean {

  private final Set<Issue> solved = new ConcurrentHashSet<>();
  private String sourceSecuritySystem;
  private String targetSecuritySystem;
  private List<Issue> result;

  public List<Issue> getReport() {
    return result;
  }

  public void run() {
    var sourceContext = ISecurityContextRepository.instance().get(sourceSecuritySystem);
    var targetContext = ISecurityContextRepository.instance().get(targetSecuritySystem);
    var comparer = new SecurityContextComparer(sourceContext, targetContext);
    result = new ArrayList<>(comparer.run());
  }

  public boolean isReportAvailable() {
    return result != null;
  }

  public void solve(Issue issue) {
    if (isSolved(issue)) {
      return;
    }
    solved.add(issue);
    issue.solve();
  }

  public String solveHint(Issue issue) {
    return switch (issue.solver().type()) {
      case CREATE -> "This will create the entry in the target security system " + targetSecuritySystem;
      case UPDATE -> "This will update the entry in the target security system " + targetSecuritySystem;
      case DELETE -> "This will delete the entry in the target security system " + targetSecuritySystem;
    };
  }

  public void solveCreateIssues() {
    solveIssues(Type.CREATE);
  }

  public void solveUpdateIssues() {
    solveIssues(Type.UPDATE);
  }

  public void solveDeleteIssues() {
    solveIssues(Type.DELETE);
  }

  private void solveIssues(Solver.Type type) {
    result.stream()
      .filter(issue -> !isSolved(issue))
      .filter(issue -> issue.solver().type() == type)
      .forEach(issue -> solve(issue));
  }

  public String styleClassForButton(Issue issue) {
    return switch(issue.solver().type()) {
      case DELETE -> "ui-button-danger";
      case CREATE -> "ui-button-success";
      default -> "";
    };
  }

  public boolean isSolved(Issue issue) {
    return solved.contains(issue);
  }

  public List<String> getSecuritySystems() {
    return ISecurityManager.instance().securityContexts().all().stream()
            .map(ISecurityContext::getName)
            .collect(Collectors.toList());
  }

  public String getTargetSecuritySystem() {
    return targetSecuritySystem;
  }

  public void setTargetSecuritySystem(String targetSecuritySystem) {
    this.targetSecuritySystem = targetSecuritySystem;
  }

  public String getSourceSecuritySystem() {
    return sourceSecuritySystem;
  }

  public void setSourceSecuritySystem(String sourceSecuritySystem) {
    this.sourceSecuritySystem = sourceSecuritySystem;
  }
}
