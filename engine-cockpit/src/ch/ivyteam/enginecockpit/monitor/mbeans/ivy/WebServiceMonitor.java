package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import java.lang.management.ManagementFactory;
import java.util.Set;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;

public class WebServiceMonitor {
  private WebService webService;
  private final String applicationName;
  private final String webServiceId;

  public WebServiceMonitor() {
    this("", "");
  }

  public WebServiceMonitor(String appName, String webServiceId) {
    this.applicationName = appName;
    this.webServiceId = webServiceId;
    try {
      var services = searchJmx(appName, webServiceId);
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
        service.id().equals(webServiceId);
  }

  private static Set<ObjectName> searchJmx(String appName, String webServiceId)
      throws MalformedObjectNameException {
    return ManagementFactory.getPlatformMBeanServer().queryNames(
        new ObjectName("ivy Engine:type=External Web Service,application=" + appName + ",name=\"*(" + webServiceId + ")\""),
        null);
  }
}
