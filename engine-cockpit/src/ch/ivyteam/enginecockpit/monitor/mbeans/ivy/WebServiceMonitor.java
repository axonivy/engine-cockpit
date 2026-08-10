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
  private final String webServiceKey;

  public WebServiceMonitor() {
    this("", "", -1, "");
  }

  public WebServiceMonitor(String contextName, String appName, int appVersion, String webServiceKey) {
    this.applicationName = appName;
    this.appVersion = appVersion;
    this.webServiceKey = webServiceKey;
    try {
      var services = searchJmx(contextName, appName, appVersion, webServiceKey);
      webService = services.stream()
          .map(WebService::new)
          .filter(this::isWebService)
          .findFirst().orElse(WebService.NO_DATA);
    } catch (MalformedObjectNameException _) {
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
        service.key().equals(webServiceKey);
  }

  private static Set<ObjectName> searchJmx(String contextName, String appName, int appVersion, String webServiceKey)
      throws MalformedObjectNameException {
    return ManagementFactory.getPlatformMBeanServer().queryNames(
        new ObjectName("ivy Engine:type=Web Service,context="+contextName+",app=" + appName + ",version=" + appVersion + ",name=" + webServiceKey),
        null);
  }
}
