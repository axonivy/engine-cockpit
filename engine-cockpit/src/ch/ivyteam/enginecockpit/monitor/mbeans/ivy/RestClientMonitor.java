package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import java.lang.management.ManagementFactory;
import java.util.Set;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;

public class RestClientMonitor {

  private RestClient restClient;
  private final String applicationName;
  private final int appVersion;
  private final String restClientUUID;

  public RestClientMonitor() {
    this("", -1, "");
  }

  public RestClientMonitor(String appName, int appVersion, String restClientUUID) {
    this.applicationName = appName;
    this.appVersion = appVersion;
    this.restClientUUID = restClientUUID;
    try {
      var clients = searchJmx(appName, appVersion, restClientUUID);
      restClient = clients.stream()
          .map(RestClient::new)
          .filter(this::isRestClient)
          .findFirst().orElse(RestClient.NO_DATA);
    } catch (MalformedObjectNameException ex) {
      restClient = RestClient.NO_DATA;
    }
  }

  public Monitor getConnectionsMonitor() {
    return restClient.connectionsMonitor();
  }

  public Monitor getCallsMonitor() {
    return restClient.callsMonitor();
  }

  public Monitor getExecutionTimeMonitor() {
    return restClient.executionTimeMonitor();
  }

  public String getRestClient() {
    return restClient.label();
  }

  private boolean isRestClient(RestClient client) {
    return client.application().equals(applicationName) &&
        client.appVersion().equals(String.valueOf(appVersion)) &&
        client.id().equals(restClientUUID);
  }

  private static Set<ObjectName> searchJmx(String appName, int appVersion, String restClientName)
      throws MalformedObjectNameException {
    return ManagementFactory.getPlatformMBeanServer().queryNames(
        new ObjectName("ivy Engine:type=External REST Web Service,application=" + appName + ",version="+appVersion+",name=\"*(" + restClientName + ")\""),
        null);
  }

}
