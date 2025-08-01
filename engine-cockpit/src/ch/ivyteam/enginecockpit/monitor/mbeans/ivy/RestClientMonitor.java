package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import java.lang.management.ManagementFactory;
import java.util.Set;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;

public class RestClientMonitor {
  private RestClient restClient;
  private final String applicationName;
  private final String restClientUUID;

  public RestClientMonitor() {
    this("", "");
  }

  public RestClientMonitor(String appName, String restClientUUID) {
    this.applicationName = appName;
    this.restClientUUID = restClientUUID;
    try {
      var clients = searchJmx(appName, restClientUUID);
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
        client.id().equals(restClientUUID);
  }

  private static Set<ObjectName> searchJmx(String appName, String restClientName)
      throws MalformedObjectNameException {
    return ManagementFactory.getPlatformMBeanServer().queryNames(
        new ObjectName("ivy Engine:type=External REST Web Service,application=" + appName + ",name=\"*(" + restClientName + ")\""),
        null);
  }

}
