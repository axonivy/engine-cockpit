package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import java.lang.management.ManagementFactory;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.Monitor;

@ManagedBean
@ViewScoped
public class ExternalDatabaseMonitorBean
{
  private Database currentDatabase;
  private final Map<String, Database> externalDatabases = new LinkedHashMap<>();
  private String applicationName;
  private String environment;
  private String databaseName;
  
  public ExternalDatabaseMonitorBean()
  {
    try
    {
      var extDatabases = ManagementFactory.getPlatformMBeanServer().queryNames(new ObjectName("ivy Engine:type=External Database,application=*,environment=*,name=*"), null);
      for (var extDatabase : extDatabases)
      {
        var db = new ExternalDatabase(extDatabase);
        externalDatabases.put(db.label(), db);
      }
      externalDatabases.put(ExternalDatabase.NO_DATA.label(), ExternalDatabase.NO_DATA);
      currentDatabase = externalDatabases.values().iterator().next();
    }
    catch(MalformedObjectNameException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  public Monitor getConnectionsMonitor()
  {
    return currentDatabase.connectionsMonitor();
  } 
  
  public Monitor getQueriesMonitor()
  {
    return currentDatabase.queriesMonitor();
  } 

  public Monitor getExecutionTimeMonitor()
  {
    return currentDatabase.executionTimeMonitor();
  } 
  
  public String getExternalDatabase()
  {
    return currentDatabase.label();
  }
  
  public void setExternalDatabase(String externalDatabase)
  {
    currentDatabase = externalDatabases.get(externalDatabase); 
  }
  
  public void setExternalDatabase()
  {
    currentDatabase = externalDatabases.getOrDefault(
         Database.toLabel(applicationName, environment, databaseName), 
         ExternalDatabase.NO_DATA);
  }
  
  public Set<String> getExternalDatabases()
  {
    return externalDatabases.keySet();
  }
  
  public String getConfigurationLink()
  {
    if (currentDatabase == ExternalDatabase.NO_DATA)
    {
      return "externaldatabases.xhtml";
    }
    else
    {
      return "externaldatabasedetail.xhtml?applicationName="+currentDatabase.application()+"&environment="+currentDatabase.environment()+"&databaseName="+currentDatabase.name();
    }
  }
  
  public String getApplicationName()
  {
    return applicationName;
  }
  
  public String getEnvironment()
  {
    return environment;
  }
  
  public String getDatabaseName()
  {
    return databaseName;
  }
  
  public void setApplicationName(String applicationName)
  {
    this.applicationName = applicationName;
    setExternalDatabase();
  }
  
  public void setEnvironment(String environment)
  {
    this.environment = environment;
    setExternalDatabase();
  }
  
  public void setDatabaseName(String databaseName)
  {
    this.databaseName = databaseName;
    setExternalDatabase();
  }
}
