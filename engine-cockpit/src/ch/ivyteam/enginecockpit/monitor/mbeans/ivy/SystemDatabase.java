package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;

public final class SystemDatabase extends AbstractDatabase {
  private static final ObjectName DATABASE_PERSISTENCY_SERVICE;

  static {
    try {
      DATABASE_PERSISTENCY_SERVICE = new ObjectName("ivy Engine:type=Database Persistency Service");
    } catch (MalformedObjectNameException ex) {
      throw new IllegalArgumentException("Wrong object name", ex);
    }
  }

  SystemDatabase() {
    super(
            DATABASE_PERSISTENCY_SERVICE,
            "Transactions",
            Monitor.build().name("Connections").icon("insert_link").toMonitor(),
            Monitor.build().name("Transactions").icon("dns").toMonitor(),
            Monitor.build().name("Processing Time").icon("timer").yAxisLabel("Time").toMonitor());
  }
}
