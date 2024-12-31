package ch.ivyteam.enginecockpit.security.system.compare;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

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
  private List<Issue> filteredResult;

  public List<Issue> getReport() {
    return result;
  }

  public List<Issue> getFilteredResult() {
    return filteredResult;
  }

  public void setFilteredResult(List<Issue> filteredResult) {
    this.filteredResult = filteredResult;
  }

  public boolean globalFilterFunction(Object value, Object filter, @SuppressWarnings("unused") Locale locale) {
    String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
    if (StringUtils.isBlank(filterText)) {
      return true;
    }
    var issue = (Issue) value;
    return toLower(issue.id()).contains(filterText)
        || toLower(issue.name()).contains(filterText)
        || toLower(issue.what().toString()).contains(filterText)
        || toLower(issue.entity().toString()).contains(filterText)
        || toLower(issue.target()).contains(filterText)
        || toLower(issue.source()).contains(filterText);
  }

  private String toLower(String value) {
    if (value == null) {
      return "";
    }
    return value.toLowerCase();
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
    solveIssue(issue, true);
  }

  private void solveIssue(Issue issue, boolean showMessage) {
    if (isSolved(issue)) {
      return;
    }
    var solveResult = issue.solve();
    if (solveResult.isOk()) {
      solved.add(issue);
    } else {
      if (showMessage) {
        var message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Exceution failed", solveResult.error());
        FacesContext.getCurrentInstance().addMessage("msgs", message);
      }
    }
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
        .forEach(issue -> solveIssue(issue, false));
  }

  public String styleClassForButton(Issue issue) {
    return switch (issue.solver().type()) {
      case DELETE -> "ui-button-danger";
      case CREATE -> "ui-button-success";
      default -> "";
    };
  }

  public boolean isSolved(Issue issue) {
    return solved.contains(issue);
  }

  public List<String> getSecuritySystems() {
    return ISecurityManager.instance().securityContexts().allWithSystem().stream()
        .map(ISecurityContext::getName)
        .filter(name -> !name.equals(sourceSecuritySystem))
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
