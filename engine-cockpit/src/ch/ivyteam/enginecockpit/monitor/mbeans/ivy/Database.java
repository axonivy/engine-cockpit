package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider.attribute;
import static ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider.cache;
import static ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider.format;

import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.mbeans.MMonitor;
import ch.ivyteam.enginecockpit.monitor.mbeans.MSeries;

class Database 
{
  private final MMonitor connectionsMonitor;
  private final MMonitor queriesMonitor;
  private final MMonitor executionTimeMonitor;


  private final String label;
  private final String name;
  private final String environment;
  private final String application;

  Database(ObjectName extDatabase, String queries, MMonitor connectionsMonitor, MMonitor queriesMonitor, MMonitor executionTimeMonitor)
  {
    this.connectionsMonitor = connectionsMonitor;
    this.queriesMonitor = queriesMonitor;
    this.executionTimeMonitor = executionTimeMonitor;
    
    if (extDatabase == null)
    {
      name = "";
      environment = "";
      application ="";
      label = "No Data";
      connectionsMonitor.addInfoValue(format("No data available"));
      queriesMonitor.addInfoValue(format("No data available"));
      executionTimeMonitor.addInfoValue(format("No data available"));
      return;
    }
    
    application = extDatabase.getKeyProperty("application");
    environment = extDatabase.getKeyProperty("environment");
    name = extDatabase.getKeyProperty("name");
    label = toLabel(application, environment, name);
    var canonicalName = extDatabase.getCanonicalName();

    var usedConnections = cache(1, attribute(canonicalName, "usedConnections"));
    var openConnections = attribute(canonicalName, "openConnections");
    var maxConnections = attribute(canonicalName, "maxConnections");
    var transactions = new ExecutionCounter(canonicalName, "transactions");

    connectionsMonitor.addInfoValue(format("Used %d", usedConnections));
    connectionsMonitor.addInfoValue(format("Open %d", openConnections));
    connectionsMonitor.addInfoValue(format("Max %d", maxConnections));
    connectionsMonitor.addSeries(new MSeries(openConnections, "Open"));
    connectionsMonitor.addSeries(new MSeries(usedConnections, "Used"));
    
    queriesMonitor.addInfoValue(format("%d", transactions.deltaExecutions()));
    queriesMonitor.addInfoValue(format("Total %d", transactions.executions()));
    queriesMonitor.addInfoValue(format("Errors %d", transactions.deltaErrors()));
    queriesMonitor.addInfoValue(format("Errors Total %d", transactions.errors()));
    
    queriesMonitor.addSeries(new MSeries(transactions.deltaExecutions(), queries));
    queriesMonitor.addSeries(new MSeries(transactions.deltaErrors(), "Errors"));
    
    executionTimeMonitor.addInfoValue(format("Min %d us", transactions.deltaMinExecutionTime()));
    executionTimeMonitor.addInfoValue(format("Avg %d us", transactions.deltaAvgExecutionTime()));
    executionTimeMonitor.addInfoValue(format("Max %d us", transactions.deltaMaxExecutionTime()));
    executionTimeMonitor.addInfoValue(format("Total %d us", transactions.executionTime()));
    executionTimeMonitor.addSeries(new MSeries(transactions.deltaMinExecutionTime(), "Min"));
    executionTimeMonitor.addSeries(new MSeries(transactions.deltaAvgExecutionTime(), "Avg"));
    executionTimeMonitor.addSeries(new MSeries(transactions.deltaMaxExecutionTime(), "Max"));
  }

  public String name()
  {
    return name;
  }
  
  public String application()
  {
    return application;
  }
  
  public String environment()
  {
    return environment;
  }
  
  public String label()
  {
    return label;
  }

  public Monitor connectionsMonitor()
  {
    return connectionsMonitor;
  }
  
  public Monitor queriesMonitor()
  {
    return queriesMonitor;
  }

  public Monitor executionTimeMonitor()
  {
    return executionTimeMonitor;
  }

  public static String toLabel(String applicationName, String environment, String databaseName)
  {
    return applicationName +" > " + environment +" > " + databaseName;
  }
}