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
  private final String appVersion;

  AbstractDatabase(ObjectName extDatabase, String queries, Monitor connectionsMonitor, Monitor queriesMonitor,
      Monitor executionTimeMonitor) {
    this.connectionsMonitor = connectionsMonitor;
    this.queriesMonitor = queriesMonitor;
    this.executionTimeMonitor = executionTimeMonitor;

    if (extDatabase == null) {
      name = "";
      application = "";
      appVersion = "";
      label = Ivy.cm().co("/common/NoData");
      connectionsMonitor.addInfoValue(format(Ivy.cm().co("/common/NoDataAvailable")));
      queriesMonitor.addInfoValue(format(Ivy.cm().co("/common/NoDataAvailable")));
      executionTimeMonitor.addInfoValue(format(Ivy.cm().co("/common/NoDataAvailable")));
      return;
    }

    application = extDatabase.getKeyProperty("application");
    appVersion = extDatabase.getKeyProperty("version");
    name = extDatabase.getKeyProperty("name");
    label = toLabel(application, name);
    var canonicalName = extDatabase.getCanonicalName();

    var usedConnections = cache(1, attribute(canonicalName, "usedConnections", Unit.ONE));
    var openConnections = attribute(canonicalName, "openConnections", Unit.ONE);
    var maxConnections = attribute(canonicalName, "maxConnections", Unit.ONE);
    var transactions = new ExecutionCounter(canonicalName, "transactions");

    connectionsMonitor.addInfoValue(format(CmsUtil.coWithDefault("/liveStats/Used", "Used") + " %5d", usedConnections));
    connectionsMonitor.addInfoValue(format(CmsUtil.coWithDefault("/liveStats/Open", "Open") + " %5d", openConnections));
    connectionsMonitor.addInfoValue(format(CmsUtil.coWithDefault("/liveStats/Max", "Max") + " %5d", maxConnections));
    connectionsMonitor.addSeries(Series.build(openConnections, CmsUtil.coWithDefault("/liveStats/Open", "Open")).toSeries());
    connectionsMonitor.addSeries(Series.build(usedConnections, CmsUtil.coWithDefault("/liveStats/Used", "Used")).toSeries());

    queriesMonitor.addInfoValue(format("%5d", transactions.deltaExecutions()));
    queriesMonitor.addInfoValue(format(CmsUtil.coWithDefault("/common/Total", "Total") + " %5d", transactions.executions()));
    queriesMonitor.addInfoValue(format(CmsUtil.coWithDefault("/common/Errors", "Errors") + " %5d", transactions.deltaErrors()));
    queriesMonitor.addInfoValue(format(CmsUtil.coWithDefault("/common/ErrorsTotal", "Errors Total") + " %5d", transactions.errors()));

    queriesMonitor.addSeries(Series.build(transactions.deltaExecutions(), queries).toSeries());
    queriesMonitor.addSeries(Series.build(transactions.deltaErrors(), CmsUtil.coWithDefault("/common/Errors", "Errors")).toSeries());

    executionTimeMonitor.addInfoValue(format(CmsUtil.coWithDefault("/liveStats/Min", "Min") + " %t", transactions.deltaMinExecutionTime()));
    executionTimeMonitor.addInfoValue(format(CmsUtil.coWithDefault("/liveStats/Avg", "Avg") + " %t", transactions.deltaAvgExecutionTime()));
    executionTimeMonitor.addInfoValue(format(CmsUtil.coWithDefault("/liveStats/Max", "Max") + " %t", transactions.deltaMaxExecutionTime()));
    executionTimeMonitor.addInfoValue(format(CmsUtil.coWithDefault("/common/Total", "Total") + " %t", transactions.executionTime()));
    executionTimeMonitor.addSeries(Series.build(transactions.deltaMinExecutionTime(), CmsUtil.coWithDefault("/liveStats/Min", "Min")).toSeries());
    executionTimeMonitor.addSeries(Series.build(transactions.deltaAvgExecutionTime(), CmsUtil.coWithDefault("/liveStats/Avg", "Avg")).toSeries());
    executionTimeMonitor.addSeries(Series.build(transactions.deltaMaxExecutionTime(), CmsUtil.coWithDefault("/liveStats/Max", "Max")).toSeries());
  }

  public String name() {
    return name;
  }

  public String application() {
    return application;
  }

  public String appVersion() {
    return appVersion;
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
