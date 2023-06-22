package ch.ivyteam.enginecockpit.security.system.compare;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityContextRepository;
import ch.ivyteam.ivy.security.ISecurityManager;
import ch.ivyteam.ivy.security.internal.context.compare.Issue;
import ch.ivyteam.ivy.security.internal.context.compare.SecurityContextComparer;

@ManagedBean
@ViewScoped
public class SecuritySystemCompareBean {

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
    result = comparer.run().stream().collect(Collectors.toList());
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
