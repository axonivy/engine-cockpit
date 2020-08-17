package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.mbeans.MMonitor;

public final class SystemDatabase extends Database
{
  private static final ObjectName DATABASE_PERSISTENCY_SERVICE;
  
  static
  {
    try
    {
      DATABASE_PERSISTENCY_SERVICE = new ObjectName("ivy Engine:type=Database Persistency Service");
    }
    catch (MalformedObjectNameException ex)
    {
      throw new IllegalArgumentException("Wrong object name", ex);
    }    
  }

  SystemDatabase()
  {
    super(
        DATABASE_PERSISTENCY_SERVICE, 
        "Transactions", 
        MMonitor.build().name("Connections").icon("insert_link").toMonitor(),
        MMonitor.build().name("Transactions").icon("dns").toMonitor(),
        MMonitor.build().name("Processing Time").icon("timer").yAxisLabel("Time [us]").toMonitor());
  }
}
