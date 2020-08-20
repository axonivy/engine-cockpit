package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import java.lang.management.ManagementFactory;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.Monitor;

@ManagedBean
@ViewScoped
public class ExternalDatabaseMonitor
{
  private Database database;
  private String applicationName;
  private String databaseName;
  
  public ExternalDatabaseMonitor()
  {
    this("", "", "");
  }

  public ExternalDatabaseMonitor(String appName, String env, String databaseName)
  {
    this.applicationName = appName;
    this.databaseName = databaseName;
    try
    {
      var databases = searchJmxRestClient(appName, env, databaseName);
      if (databases.isEmpty())
      {
        databases = searchJmxRestClient(appName, "Default", databaseName);
      }
      database = databases.stream()
              .map(client -> new ExternalDatabase(client))
              .filter(this::isDatabase)
              .findFirst().orElse(ExternalDatabase.NO_DATA);
    }
    catch(MalformedObjectNameException ex)
    {
      database = ExternalDatabase.NO_DATA;
    }
  }
  
  public Monitor getConnectionsMonitor()
  {
    return database.connectionsMonitor();
  } 
  
  public Monitor getQueriesMonitor()
  {
    return database.queriesMonitor();
  } 

  public Monitor getExecutionTimeMonitor()
  {
    return database.executionTimeMonitor();
  } 
  
  public String getExternalDatabase()
  {
    return database.label();
  }
  
  private boolean isDatabase(Database db)
  {
    return db.application().equals(applicationName) &&
            db.name().equals(databaseName);
  }
  
  private static Set<ObjectName> searchJmxRestClient(String appName, String env, String databaseName) throws MalformedObjectNameException
  {
    return ManagementFactory.getPlatformMBeanServer().queryNames(
            new ObjectName("ivy Engine:type=External Database,application=" + appName + ",environment=" + env + ",name=" + databaseName), null);
  }
  
}
