package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import java.lang.management.ManagementFactory;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;

@ManagedBean
@ViewScoped
public class DatabaseMonitor {
  private AbstractDatabase database;
  private final String applicationName;
  private final String databaseName;

  public DatabaseMonitor() {
    this("", "");
  }

  public DatabaseMonitor(String appName, String databaseName) {
    this.applicationName = appName;
    this.databaseName = databaseName;
    try {
      var databases = searchJmx(appName, databaseName);
      database = databases.stream()
          .map(Database::new)
          .filter(this::isDatabase)
          .findFirst().orElse(Database.NO_DATA);
    } catch (MalformedObjectNameException ex) {
      database = Database.NO_DATA;
    }
  }

  public Monitor getConnectionsMonitor() {
    return database.connectionsMonitor();
  }

  public Monitor getQueriesMonitor() {
    return database.queriesMonitor();
  }

  public Monitor getExecutionTimeMonitor() {
    return database.executionTimeMonitor();
  }

  public String getDatabase() {
    return database.label();
  }

  private boolean isDatabase(AbstractDatabase db) {
    return db.application().equals(applicationName) &&
        db.name().equals(databaseName);
  }

  private static Set<ObjectName> searchJmx(String appName, String databaseName)
      throws MalformedObjectNameException {
    return ManagementFactory.getPlatformMBeanServer().queryNames(
        new ObjectName("ivy Engine:type=External Database,application=" + appName + ",name=" + databaseName),
        null);
  }
}
