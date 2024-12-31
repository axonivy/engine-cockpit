package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;

final class Database extends AbstractDatabase {
  public static final Database NO_DATA = new Database();

  Database(ObjectName extDatabase) {
    super(
        extDatabase,
        "Queries",
        Monitor.build().title("Database Connections").name("Connections").icon("insert_link").toMonitor(),
        Monitor.build().title("Database Queries").name("Queries").icon("dns").toMonitor(),
        Monitor.build().title("Database Query Execution Time").name("Execution Time").icon("timer")
            .yAxisLabel("Execution Time [us]").toMonitor());
  }

  private Database() {
    this(null);
  }

}
