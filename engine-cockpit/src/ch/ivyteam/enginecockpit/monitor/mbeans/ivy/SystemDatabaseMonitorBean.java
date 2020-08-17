package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.monitor.Monitor;

@ManagedBean
@ViewScoped
public class SystemDatabaseMonitorBean
{
  private static final SystemDatabase DATABASE = new SystemDatabase();
  
  public Monitor getConnectionsMonitor()
  {
    return DATABASE.connectionsMonitor();
  }
  
  public Monitor getTransactionsMonitor()
  {
    return DATABASE.queriesMonitor();
  }
  
  public Monitor getProcessingTimeMonitor()
  {
    return DATABASE.executionTimeMonitor();
  }
}
