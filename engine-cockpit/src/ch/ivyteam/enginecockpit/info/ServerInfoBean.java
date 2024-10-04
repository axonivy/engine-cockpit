package ch.ivyteam.enginecockpit.info;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.inject.Inject;

import ch.ivyteam.di.restricted.DiCore;
import ch.ivyteam.ivy.Advisor;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.security.context.EngineCockpitUrlPath;
import ch.ivyteam.ivy.server.restricted.EngineMode;
import ch.ivyteam.ivy.server.restricted.MaintenanceReason;
import ch.ivyteam.licence.LicenceEventManager;

@ManagedBean(name="ivyServerInfoBean")
// only used in info.xhtml
public class ServerInfoBean {

  @Inject
  private IApplicationRepository apps;

  //@Inject
  //private IUpdateManager updateManager;

  private List<Application> applications;

  public ServerInfoBean() {
    DiCore.getGlobalInjector().injectMembers(this);
    applications = apps.all().stream()
            .sorted(Comparator.comparing(IApplication::getName, String.CASE_INSENSITIVE_ORDER))
            .map(Application::new)
            .filter(this::isNotNeoApp)
            .collect(Collectors.toList());
  }

  private boolean isNotNeoApp(Application app) {
    return !app.getName().startsWith("ivy-dev-");
  }

  public List<Application> getApplications() {
    return applications;
  }

  public int getMessageCount() {
    int count = 0;
    if (hasLicenceMessages()) {
      count ++;
    }
    if (newUpdateAvailable()) {
      count ++;
    }
    return count;
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

  public boolean hasLicenceMessages() {
    return !LicenceEventManager.getInstance().getUnconfirmedLicenceEvents().isEmpty();
  }

  public boolean newUpdateAvailable() {
    return false;
    //return updateManager.isNewerServiceReleaseAvailable() || updateManager.isNewerReleaseAvailable();
  }

  public String getEngineCockpitUrl() {
    return EngineCockpitUrlPath.toPath();
  }
}
