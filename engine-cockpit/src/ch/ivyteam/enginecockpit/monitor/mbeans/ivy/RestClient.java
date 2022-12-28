package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.attribute;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.cache;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.format;

import javax.management.ObjectName;

import org.apache.commons.lang.StringUtils;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.monitor.Series;
import ch.ivyteam.enginecockpit.monitor.unit.Unit;

class RestClient {
  public static final RestClient NO_DATA = new RestClient();

  private final Monitor connectionsMonitor = Monitor.build().name("Connections")
          .title("REST Client Connections").icon("insert_link").toMonitor();
  private final Monitor callsMonitor = Monitor.build().name("Calls").title("REST Client Calls")
          .icon("settings_ethernet").toMonitor();
  private final Monitor executionTimeMonitor = Monitor.build().name("Execution Time")
          .title("REST Client Execution Time").icon("timer").yAxisLabel("Execution Time").toMonitor();

  private final String label;
  private final String id;
  private final String name;
  private final String application;

  private RestClient() {
    this(null);
  }

  RestClient(ObjectName restClient) {
    if (restClient == null) {
      id = "";
      name = "";
      application = "";
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
    label = toLabel(application, name);

    var usedConnections = cache(1, attribute(restClient, "usedConnections", Unit.ONE));
    var openConnections = attribute(restClient, "openConnections", Unit.ONE);
    var maxConnections = attribute(restClient, "maxConnections", Unit.ONE);

    connectionsMonitor.addInfoValue(format("Used %5d", usedConnections));
    connectionsMonitor.addInfoValue(format("Open %5d", openConnections));
    connectionsMonitor.addInfoValue(format("Max %5d", maxConnections));
    connectionsMonitor.addSeries(Series.build(openConnections, "Open").toSeries());
    connectionsMonitor.addSeries(Series.build(usedConnections, "Used").toSeries());

    var calls = new ExecutionCounter(restClient.getCanonicalName(), "calls");
    callsMonitor.addInfoValue(format("%5d", calls.deltaExecutions()));
    callsMonitor.addInfoValue(format("Total %5d", calls.executions()));
    callsMonitor.addInfoValue(format("Errors %5d", calls.deltaErrors()));
    callsMonitor.addInfoValue(format("Errors Total %5d", calls.errors()));
    callsMonitor.addSeries(Series.build(calls.deltaExecutions(), "Calls").toSeries());
    callsMonitor.addSeries(Series.build(calls.deltaErrors(), "Errors").toSeries());

    executionTimeMonitor.addInfoValue(format("Min %t", calls.deltaMinExecutionTime()));
    executionTimeMonitor.addInfoValue(format("Avg %t", calls.deltaAvgExecutionTime()));
    executionTimeMonitor.addInfoValue(format("Max %t", calls.deltaMaxExecutionTime()));
    executionTimeMonitor.addInfoValue(format("Total %t", calls.executionTime()));
    executionTimeMonitor.addSeries(Series.build(calls.deltaMinExecutionTime(), "Min").toSeries());
    executionTimeMonitor.addSeries(Series.build(calls.deltaAvgExecutionTime(), "Avg").toSeries());
    executionTimeMonitor.addSeries(Series.build(calls.deltaMaxExecutionTime(), "Max").toSeries());
  }

  public String id() {
    return id;
  }

  public String name() {
    return name;
  }

  public String application() {
    return application;
  }

  public String label() {
    return label;
  }

  public Monitor connectionsMonitor() {
    return connectionsMonitor;
  }

  public Monitor callsMonitor() {
    return callsMonitor;
  }

  public Monitor executionTimeMonitor() {
    return executionTimeMonitor;
  }

  public static String toLabel(String applicationName, String restClientName) {
    return applicationName + " > " + restClientName;
  }
}
