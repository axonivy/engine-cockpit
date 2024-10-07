package ch.ivyteam.enginecockpit.info;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import ch.ivyteam.ivy.Advisor;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.security.ISecurityContextRepository;
import ch.ivyteam.ivy.security.context.EngineCockpitUrlPath;
import ch.ivyteam.ivy.server.restricted.EngineMode;
import ch.ivyteam.ivy.server.restricted.MaintenanceReason;

@ManagedBean
@RequestScoped
public class EngineInfo {

  private final List<Application> applications;

  public EngineInfo() {
    applications = IApplicationRepository.instance().all().stream()
            .sorted(Comparator.comparing(IApplication::getName, String.CASE_INSENSITIVE_ORDER))
            .map(Application::new)
            .filter(this::isNotNeoApp)
            .collect(Collectors.toList());
  }

  private boolean isNotNeoApp(Application app) {
    return !app.getName().startsWith("ivy-dev-");
  }

  public List<Application> getApplications() {
    if (isDemo()) {
      return applications.stream()
              .filter(app -> !app.getName().equals("demo-portal"))
              .collect(Collectors.toList());
    }
    if (isMaintenance()) {
      return new ArrayList<>();
    }
    return applications;
  }

  public boolean isShowAppHeader() {
    if (isMaintenance()) {
      return false;
    }
    if (isDemo() && getApplications().isEmpty()) {
      return false;
    }
    return true;
  }

  public boolean isShowNoApplications() {
    if (isDemo() || isMaintenance()) {
      return false;
    }
    return applications.isEmpty();
  }

  public boolean isDemo() {
    return EngineMode.is(EngineMode.DEMO);
  }

  public boolean isMaintenance() {
    return EngineMode.is(EngineMode.MAINTENANCE);
  }

  public boolean isNeo() {
    return Advisor.instance().isNeoActive();
  }

  public String getMaintenanceReason() {
    return MaintenanceReason.getMessage();
  }

  public String getEngineCockpitUrl() {
    return EngineCockpitUrlPath.toPath();
  }

  public String getDemoPortalUrl() {
    var path = ISecurityContextRepository.instance().getDefault().contextPath();
    return path + "/demo-portal";
  }
}
