package ch.ivyteam.enginecockpit.info;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.PrimeFaces;

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
  private boolean isShutingDown;

  public EngineInfo() {
    applications = IApplicationRepository.instance().all().stream()
            .sorted(Comparator.comparing(IApplication::getName, String.CASE_INSENSITIVE_ORDER))
            .map(Application::new)
            .filter(this::isNotInDevMode)
            .collect(Collectors.toList());
  }

  public boolean canShutdown() {
    if (!isDemo()) {
      return false;
    }
    var addr = getRequest().getRemoteAddr();
    if ("127.0.0.1".equals(addr) || "0:0:0:0:0:0:0:1".equals(addr)) {
      return true;
    }
    return false;
  }

  public void openShutdownDialog() {
    PrimeFaces.current().executeScript("PF('shutdownDialog').show()");
  }

  private HttpServletRequest getRequest() {
    return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
  }

  public String getShutdownMessage() {
    if (isShutingDown) {
      return "The Axon Ivy Engine is shutting down. You can close the window.";
    }
    return "Do you really want to shutdown the Axon Ivy Engine?";
  }

  public void shutdown() {
    if (canShutdown()) {
      isShutingDown = true;
      new Thread(() -> System.exit(0)).start();
    }
  }

  public boolean isShutdownButtonsDisabled() {
    return isShutingDown;
  }

  private boolean isNotInDevMode(Application app) {
    return !app.isDevMode();
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

  public boolean isShowApps() {
    return !getApplications().isEmpty();
  }

  public boolean isShowNoApps() {
    if (isDemo() || isMaintenance()) {
      return false;
    }
    return getApplications().isEmpty();
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
