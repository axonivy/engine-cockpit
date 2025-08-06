package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.attribute;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.cache;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.format;

import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.monitor.Series;
import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.ivy.environment.Ivy;

abstract class AbstractDatabase {
  private final Monitor connectionsMonitor;
  private final Monitor queriesMonitor;
  private final Monitor executionTimeMonitor;

  private final String label;
  private final String name;
  private final String application;

  AbstractDatabase(ObjectName extDatabase, String queries, Monitor connectionsMonitor, Monitor queriesMonitor,
      Monitor executionTimeMonitor) {
    this.connectionsMonitor = connectionsMonitor;
    this.queriesMonitor = queriesMonitor;
    this.executionTimeMonitor = executionTimeMonitor;

    if (extDatabase == null) {
      name = "";
      application = "";
      label = Ivy.cm().co("/common/NoData");
      connectionsMonitor.addInfoValue(format(Ivy.cm().co("/common/NoDataAvailable")));
      queriesMonitor.addInfoValue(format(Ivy.cm().co("/common/NoDataAvailable")));
      executionTimeMonitor.addInfoValue(format(Ivy.cm().co("/common/NoDataAvailable")));
      return;
    }

    application = extDatabase.getKeyProperty("application");
    name = extDatabase.getKeyProperty("name");
    label = toLabel(application, name);
    var canonicalName = extDatabase.getCanonicalName();

    var usedConnections = cache(1, attribute(canonicalName, "usedConnections", Unit.ONE));
    var openConnections = attribute(canonicalName, "openConnections", Unit.ONE);
    var maxConnections = attribute(canonicalName, "maxConnections", Unit.ONE);
    var transactions = new ExecutionCounter(canonicalName, "transactions");

    connectionsMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/ConnectionsMonitorUsedValue"), usedConnections));
    connectionsMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/ConnectionsMonitorOpenValue"), openConnections));
    connectionsMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/ConnectionsMonitorMaxValue"), maxConnections));
    connectionsMonitor.addSeries(Series.build(openConnections, Ivy.cm().co("/liveStats/ConnectionsMonitorOpen")).toSeries());
    connectionsMonitor.addSeries(Series.build(usedConnections, Ivy.cm().co("/liveStats/ConnectionsMonitorUsed")).toSeries());

    queriesMonitor.addInfoValue(format("%5d", transactions.deltaExecutions()));
    queriesMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/QueriesMonitorTotalValue"), transactions.executions()));
    queriesMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/QueriesMonitorErrorsValue"), transactions.deltaErrors()));
    queriesMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/QueriesMonitorErrorsTotalValue"), transactions.errors()));

    queriesMonitor.addSeries(Series.build(transactions.deltaExecutions(), queries).toSeries());
    queriesMonitor.addSeries(Series.build(transactions.deltaErrors(), Ivy.cm().co("/liveStats/QueriesMonitorErrors")).toSeries());

    executionTimeMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/ExecutionTimeMonitorMinValue"), transactions.deltaMinExecutionTime()));
    executionTimeMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/ExecutionTimeMonitorAvgValue"), transactions.deltaAvgExecutionTime()));
    executionTimeMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/ExecutionTimeMonitorMaxValue"), transactions.deltaMaxExecutionTime()));
    executionTimeMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/ExecutionTimeMonitorTotalValue"), transactions.executionTime()));
    executionTimeMonitor.addSeries(Series.build(transactions.deltaMinExecutionTime(), Ivy.cm().co("/liveStats/ExecutionTimeMonitorMin")).toSeries());
    executionTimeMonitor.addSeries(Series.build(transactions.deltaAvgExecutionTime(), Ivy.cm().co("/liveStats/ExecutionTimeMonitorAvg")).toSeries());
    executionTimeMonitor.addSeries(Series.build(transactions.deltaMaxExecutionTime(), Ivy.cm().co("/liveStats/ExecutionTimeMonitorMax")).toSeries());
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

  public Monitor queriesMonitor() {
    return queriesMonitor;
  }

  public Monitor executionTimeMonitor() {
    return executionTimeMonitor;
  }

  public static String toLabel(String applicationName, String databaseName) {
    return applicationName + " > " + databaseName;
  }
}
