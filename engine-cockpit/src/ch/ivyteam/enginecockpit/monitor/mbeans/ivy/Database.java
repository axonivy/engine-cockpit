package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.attribute;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.cache;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.format;

import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.monitor.Series;
import ch.ivyteam.enginecockpit.monitor.unit.Unit;

class Database 
{
  private final Monitor connectionsMonitor;
  private final Monitor queriesMonitor;
  private final Monitor executionTimeMonitor;


  private final String label;
  private final String name;
  private final String environment;
  private final String application;

  Database(ObjectName extDatabase, String queries, Monitor connectionsMonitor, Monitor queriesMonitor, Monitor executionTimeMonitor)
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

    var usedConnections = cache(1, attribute(canonicalName, "usedConnections", Unit.ONE));
    var openConnections = attribute(canonicalName, "openConnections", Unit.ONE);
    var maxConnections = attribute(canonicalName, "maxConnections", Unit.ONE);
    var transactions = new ExecutionCounter(canonicalName, "transactions");

    connectionsMonitor.addInfoValue(format("Used %5d", usedConnections));
    connectionsMonitor.addInfoValue(format("Open %5d", openConnections));
    connectionsMonitor.addInfoValue(format("Max %5d", maxConnections));
    connectionsMonitor.addSeries(Series.build(openConnections, "Open").toSeries());
    connectionsMonitor.addSeries(Series.build(usedConnections, "Used").toSeries());
    
    queriesMonitor.addInfoValue(format("%5d", transactions.deltaExecutions()));
    queriesMonitor.addInfoValue(format("Total %5d", transactions.executions()));
    queriesMonitor.addInfoValue(format("Errors %5d", transactions.deltaErrors()));
    queriesMonitor.addInfoValue(format("Errors Total %5d", transactions.errors()));
    
    queriesMonitor.addSeries(Series.build(transactions.deltaExecutions(), queries).toSeries());
    queriesMonitor.addSeries(Series.build(transactions.deltaErrors(), "Errors").toSeries());
    
    executionTimeMonitor.addInfoValue(format("Min %t", transactions.deltaMinExecutionTime()));
    executionTimeMonitor.addInfoValue(format("Avg %t", transactions.deltaAvgExecutionTime()));
    executionTimeMonitor.addInfoValue(format("Max %t", transactions.deltaMaxExecutionTime()));
    executionTimeMonitor.addInfoValue(format("Total %t", transactions.executionTime()));
    executionTimeMonitor.addSeries(Series.build(transactions.deltaMinExecutionTime(), "Min").toSeries());
    executionTimeMonitor.addSeries(Series.build(transactions.deltaAvgExecutionTime(), "Avg").toSeries());
    executionTimeMonitor.addSeries(Series.build(transactions.deltaMaxExecutionTime(), "Max").toSeries());
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
