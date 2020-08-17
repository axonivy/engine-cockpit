package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider.attribute;
import static ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider.cache;
import static ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider.format;

import javax.management.ObjectName;

import org.apache.commons.lang.StringUtils;

import ch.ivyteam.enginecockpit.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.mbeans.MMonitor;
import ch.ivyteam.enginecockpit.monitor.mbeans.MSeries;

class RestClient
{
  public static final RestClient NO_DATA = new RestClient();
  
  private final MMonitor connectionsMonitor = MMonitor.build().name("Connections").title("REST Client Connections").icon("insert_link").toMonitor();
  private final MMonitor callsMonitor = MMonitor.build().name("Calls").title("REST Client Calls").icon("settings_ethernet").toMonitor();
  private final MMonitor executionTimeMonitor = MMonitor.build().name("Execution Time").title("REST Client Execution Time").icon("timer").yAxisLabel("Execution Time [us]").toMonitor();

  private final String label;
  private final String id;
  private final String name;
  private final String environment;
  private final String application;
  
  private RestClient()
  {
    this(null);
  }

  RestClient(ObjectName restClient)
  {
    if (restClient == null)
    {
      id = "";
      name = "";
      environment = "";
      application ="";
      label = "No Data";
      callsMonitor.addInfoValue(format("No data available"));
      executionTimeMonitor.addInfoValue(format("No data available"));
      connectionsMonitor.addInfoValue(format("No data available"));
      return;
    }
    var nm = restClient.getKeyProperty("name");
    nm = StringUtils.removeStart(nm, "\"");
    nm = StringUtils.removeEnd(nm, "\"");
    this.name = StringUtils.substringBeforeLast(nm, "(").trim();
    var identifier = StringUtils.substringAfterLast(nm, "(");
    this.id = StringUtils.removeEnd(identifier, ")");
    
    application = restClient.getKeyProperty("application");
    environment = restClient.getKeyProperty("environment");
    label = toLabel(application, environment, name);

    var usedConnections = cache(1, attribute(restClient, "usedConnections"));
    var openConnections = attribute(restClient, "openConnections");
    var maxConnections = attribute(restClient, "maxConnections");
    
    connectionsMonitor.addInfoValue(format("Used %d", usedConnections));
    connectionsMonitor.addInfoValue(format("Open %d", openConnections));
    connectionsMonitor.addInfoValue(format("Max %d", maxConnections));
    connectionsMonitor.addSeries(new MSeries(openConnections, "Open"));
    connectionsMonitor.addSeries(new MSeries(usedConnections, "Used"));

    var calls = new ExecutionCounter(restClient.getCanonicalName(), "calls");
    callsMonitor.addInfoValue(format("%d", calls.deltaExecutions()));
    callsMonitor.addInfoValue(format("Total %d", calls.executions()));
    callsMonitor.addInfoValue(format("Errors %d", calls.deltaErrors()));
    callsMonitor.addInfoValue(format("Errors Total %d", calls.errors()));
    callsMonitor.addSeries(new MSeries(calls.deltaExecutions(), "Calls"));
    callsMonitor.addSeries(new MSeries(calls.deltaErrors(), "Errors"));
    
    executionTimeMonitor.addInfoValue(format("Min %d us", calls.deltaMinExecutionTime()));
    executionTimeMonitor.addInfoValue(format("Avg %d us", calls.deltaAvgExecutionTime()));
    executionTimeMonitor.addInfoValue(format("Max %d us", calls.deltaMaxExecutionTime()));
    executionTimeMonitor.addInfoValue(format("Total %d us", calls.executionTime()));
    executionTimeMonitor.addSeries(new MSeries(calls.deltaMinExecutionTime(), "Min"));
    executionTimeMonitor.addSeries(new MSeries(calls.deltaAvgExecutionTime(), "Avg"));
    executionTimeMonitor.addSeries(new MSeries(calls.deltaMaxExecutionTime(), "Max"));
  }

  public String id()
  {
    return id;
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

  public Monitor callsMonitor()
  {
    return callsMonitor;
  }
  
  public Monitor executionTimeMonitor()
  {
    return executionTimeMonitor;
  }

  public static String toLabel(String applicationName, String environment, String restClientName)
  {    
    return applicationName +" > " + environment +" > " + restClientName;
  }
}
