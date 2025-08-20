package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.ivy.environment.Ivy;

final class Database extends AbstractDatabase {
  public static final Database NO_DATA = new Database();

  Database(ObjectName extDatabase) {
    super(
        extDatabase,
        Ivy.cm().co("/liveStats/Queries"),
        Monitor.build().title(Ivy.cm().co("/liveStats/DatabaseConnections")).name(Ivy.cm().co("/common/Connections")).icon("insert_link").toMonitor(),
        Monitor.build().title(Ivy.cm().co("/liveStats/DatabaseQueries")).name(Ivy.cm().co("/liveStats/Queries")).icon("dns").toMonitor(),
        Monitor.build().title(Ivy.cm().co("/liveStats/DatabaseQueryExecutionTime")).name(Ivy.cm().co("/common/ExecutionTime")).icon("timer")
            .yAxisLabel(Ivy.cm().co("/liveStats/ExecutionTimeYAxisLabel")).toMonitor());
  }

  private Database() {
    this(null);
  }

}
