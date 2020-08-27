package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;

final class ExternalDatabase extends Database
{
  public static final ExternalDatabase NO_DATA = new ExternalDatabase();

  ExternalDatabase(ObjectName extDatabase)
  {
    super(
        extDatabase, 
        "Queries",      
        Monitor.build().title("External Database Connections").name("Connections").icon("insert_link").toMonitor(),
        Monitor.build().title("External Database Queries").name("Queries").icon("dns").toMonitor(),
        Monitor.build().title("External Database Query Execution Time").name("Execution Time").icon("timer").yAxisLabel("Execution Time [us]").toMonitor());
  }

  private ExternalDatabase()
  {
    this(null);
  }

}
