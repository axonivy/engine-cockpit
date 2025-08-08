package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.attribute;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.cache;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.format;

import javax.management.ObjectName;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.monitor.Series;
import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.ivy.environment.Ivy;

class RestClient {
  public static final RestClient NO_DATA = new RestClient();

  private final Monitor connectionsMonitor = Monitor.build().name(Ivy.cm().co("/common/Connections"))
      .title(Ivy.cm().co("/liveStats/RestClientConnections")).icon("insert_link").toMonitor();
  private final Monitor callsMonitor = Monitor.build().name(Ivy.cm().co("/liveStats/Calls"))
      .title(Ivy.cm().co("/liveStats/RestClientCalls")).icon("settings_ethernet").toMonitor();
  private final Monitor executionTimeMonitor = Monitor.build().name(Ivy.cm().co("/common/ExecutionTime"))
      .title(Ivy.cm().co("/liveStats/RestClientExecutionTime")).icon("timer")
      .yAxisLabel(Ivy.cm().co("/common/ExecutionTime")).toMonitor();

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
      label = Ivy.cm().co("/common/NoData");
      callsMonitor.addInfoValue(format(Ivy.cm().co("/common/NoDataAvailable")));
      executionTimeMonitor.addInfoValue(format(Ivy.cm().co("/common/NoDataAvailable")));
      connectionsMonitor.addInfoValue(format(Ivy.cm().co("/common/NoDataAvailable")));
      return;
    }
    var nm = restClient.getKeyProperty("name");
    nm = Strings.CS.removeStart(nm, "\"");
    nm = Strings.CS.removeEnd(nm, "\"");
    this.name = StringUtils.substringBeforeLast(nm, "(").trim();
    var identifier = StringUtils.substringAfterLast(nm, "(");
    this.id = Strings.CS.removeEnd(identifier, ")");

    application = restClient.getKeyProperty("application");
    label = toLabel(application, name);

    var usedConnections = cache(1, attribute(restClient, "usedConnections", Unit.ONE));
    var openConnections = attribute(restClient, "openConnections", Unit.ONE);
    var maxConnections = attribute(restClient, "maxConnections", Unit.ONE);

    connectionsMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/ConnectionsMonitorUsedValue"), usedConnections));
    connectionsMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/ConnectionsMonitorOpenValue"), openConnections));
    connectionsMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/ConnectionsMonitorMaxValue"), maxConnections));
    connectionsMonitor.addSeries(Series.build(openConnections, Ivy.cm().co("/liveStats/ConnectionsMonitorOpen")).toSeries());
    connectionsMonitor.addSeries(Series.build(usedConnections, Ivy.cm().co("/liveStats/ConnectionsMonitorUsed")).toSeries());

    var calls = new ExecutionCounter(restClient.getCanonicalName(), "calls");
    callsMonitor.addInfoValue(format("%5d", calls.deltaExecutions()));
    callsMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/CallsMonitorTotalValue"), calls.executions()));
    callsMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/CallsMonitorErrorsValue"), calls.deltaErrors()));
    callsMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/CallsMonitorErrorsTotalValue"), calls.errors()));
    callsMonitor.addSeries(Series.build(calls.deltaExecutions(), Ivy.cm().co("/liveStats/Calls")).toSeries());
    callsMonitor.addSeries(Series.build(calls.deltaErrors(), Ivy.cm().co("/liveStats/MonitorErrors")).toSeries());

    executionTimeMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/ExecutionTimeMonitorMinValue"), calls.deltaMinExecutionTime()));
    executionTimeMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/ExecutionTimeMonitorAvgValue"), calls.deltaAvgExecutionTime()));
    executionTimeMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/ExecutionTimeMonitorMaxValue"), calls.deltaMaxExecutionTime()));
    executionTimeMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/ExecutionTimeMonitorTotalValue"), calls.executionTime()));
    executionTimeMonitor.addSeries(Series.build(calls.deltaMinExecutionTime(), Ivy.cm().co("/liveStats/ExecutionTimeMonitorMin")).toSeries());
    executionTimeMonitor.addSeries(Series.build(calls.deltaAvgExecutionTime(), Ivy.cm().co("/liveStats/ExecutionTimeMonitorAvg")).toSeries());
    executionTimeMonitor.addSeries(Series.build(calls.deltaMaxExecutionTime(), Ivy.cm().co("/liveStats/ExecutionTimeMonitorMax")).toSeries());
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
