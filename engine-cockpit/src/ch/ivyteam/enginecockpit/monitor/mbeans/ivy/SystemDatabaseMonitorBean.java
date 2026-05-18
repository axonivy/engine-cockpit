package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;

@Named
@ViewScoped
public class SystemDatabaseMonitorBean {
  private final SystemDatabase DATABASE = new SystemDatabase();

  public Monitor getConnectionsMonitor() {
    return DATABASE.connectionsMonitor();
  }

  public Monitor getTransactionsMonitor() {
    return DATABASE.queriesMonitor();
  }

  public Monitor getProcessingTimeMonitor() {
    return DATABASE.executionTimeMonitor();
  }
}
