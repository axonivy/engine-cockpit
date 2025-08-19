package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.attribute;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.cache;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.format;

import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.monitor.Series;
import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.util.CmsUtil;
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

    connectionsMonitor.addInfoValue(format(CmsUtil.coWithDefault("/liveStats/ConnectionsMonitorUsedValue", "Used %5d"), usedConnections));
    connectionsMonitor.addInfoValue(format(CmsUtil.coWithDefault("/liveStats/ConnectionsMonitorOpenValue", "Open %5d"), openConnections));
    connectionsMonitor.addInfoValue(format(CmsUtil.coWithDefault("/liveStats/ConnectionsMonitorMaxValue", "Max %5d"), maxConnections));
    connectionsMonitor.addSeries(Series.build(openConnections, CmsUtil.coWithDefault("/liveStats/ConnectionsMonitorOpen", "Open")).toSeries());
    connectionsMonitor.addSeries(Series.build(usedConnections, CmsUtil.coWithDefault("/liveStats/ConnectionsMonitorUsed", "Used")).toSeries());

    queriesMonitor.addInfoValue(format("%5d", transactions.deltaExecutions()));
    queriesMonitor.addInfoValue(format(CmsUtil.coWithDefault("/liveStats/QueriesMonitorTotalValue", "Total %5d"), transactions.executions()));
    queriesMonitor.addInfoValue(format(CmsUtil.coWithDefault("/liveStats/QueriesMonitorErrorsValue", "Errors %5d"), transactions.deltaErrors()));
    queriesMonitor.addInfoValue(format(CmsUtil.coWithDefault("/liveStats/QueriesMonitorErrorsTotalValue", "Errors Total %5d"), transactions.errors()));

    queriesMonitor.addSeries(Series.build(transactions.deltaExecutions(), queries).toSeries());
    queriesMonitor.addSeries(Series.build(transactions.deltaErrors(), CmsUtil.coWithDefault("/liveStats/QueriesMonitorErrors", "Errors")).toSeries());

    executionTimeMonitor.addInfoValue(format(CmsUtil.coWithDefault("/liveStats/ExecutionTimeMonitorMinValue", "Min %t"), transactions.deltaMinExecutionTime()));
    executionTimeMonitor.addInfoValue(format(CmsUtil.coWithDefault("/liveStats/ExecutionTimeMonitorAvgValue", "Avg %t"), transactions.deltaAvgExecutionTime()));
    executionTimeMonitor.addInfoValue(format(CmsUtil.coWithDefault("/liveStats/ExecutionTimeMonitorMaxValue", "Max %t"), transactions.deltaMaxExecutionTime()));
    executionTimeMonitor.addInfoValue(format(CmsUtil.coWithDefault("/liveStats/ExecutionTimeMonitorTotalValue", "Total %t"), transactions.executionTime()));
    executionTimeMonitor.addSeries(Series.build(transactions.deltaMinExecutionTime(), CmsUtil.coWithDefault("/liveStats/ExecutionTimeMonitorMin", "Min")).toSeries());
    executionTimeMonitor.addSeries(Series.build(transactions.deltaAvgExecutionTime(), CmsUtil.coWithDefault("/liveStats/ExecutionTimeMonitorAvg", "Avg")).toSeries());
    executionTimeMonitor.addSeries(Series.build(transactions.deltaMaxExecutionTime(), CmsUtil.coWithDefault("/liveStats/ExecutionTimeMonitorMax", "Max")).toSeries());
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
