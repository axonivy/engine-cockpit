package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import java.lang.management.ManagementFactory;
import java.util.Set;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;

public class WebServiceMonitor {
  private WebService webService;
  private final String applicationName;
  private final int appVersion;
  private final String webServiceId;

  public WebServiceMonitor() {
    this("", -1, "");
  }

  public WebServiceMonitor(String appName, int appVersion, String webServiceId) {
    this.applicationName = appName;
    this.appVersion = appVersion;
    this.webServiceId = webServiceId;
    try {
      var services = searchJmx(appName, appVersion, webServiceId);
      webService = services.stream()
          .map(WebService::new)
          .filter(this::isWebService)
          .findFirst().orElse(WebService.NO_DATA);
    } catch (MalformedObjectNameException ex) {
      webService = WebService.NO_DATA;
    }
  }

  public Monitor getCallsMonitor() {
    return webService.callsMonitor();
  }

  public Monitor getExecutionTimeMonitor() {
    return webService.executionTimeMonitor();
  }

  public String getWebService() {
    return webService.label();
  }

  private boolean isWebService(WebService service) {
    return service.application().equals(applicationName) &&
        service.appVersion().equals(String.valueOf(appVersion)) &&
        service.id().equals(webServiceId);
  }

  private static Set<ObjectName> searchJmx(String appName, int appVersion, String webServiceId)
      throws MalformedObjectNameException {
    return ManagementFactory.getPlatformMBeanServer().queryNames(
        new ObjectName("ivy Engine:type=External Web Service,application=" + appName + ",version=" + appVersion + ",name=\"*(" + webServiceId + ")\""),
        null);
  }
}
