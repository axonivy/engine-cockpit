package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.format;

import javax.management.ObjectName;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.monitor.Series;
import ch.ivyteam.ivy.environment.Ivy;

class WebService {
  public static final WebService NO_DATA = new WebService();

  private final Monitor callsMonitor = Monitor.build().name(Ivy.cm().co("/liveStats/Calls"))
      .title(Ivy.cm().co("/liveStats/WebServiceCalls")).icon("language").toMonitor();
  private final Monitor executionTimeMonitor = Monitor.build().name(Ivy.cm().co("/common/ExecutionTime"))
      .title(Ivy.cm().co("/liveStats/WebServiceExecutionTime")).icon("timer")
      .yAxisLabel(Ivy.cm().co("/common/ExecutionTime")).toMonitor();

  private final String label;
  private final String id;
  private final String name;
  private final String application;

  private WebService() {
    this(null);
  }

  WebService(ObjectName webService) {
    if (webService == null) {
      id = "";
      name = "";
      application = "";
      label = Ivy.cm().co("/common/NoData");
      callsMonitor.addInfoValue(format(Ivy.cm().co("/common/NoDataAvailable")));
      executionTimeMonitor.addInfoValue(format(Ivy.cm().co("/common/NoDataAvailable")));
      return;
    }
    var nm = webService.getKeyProperty("name");
    nm = Strings.CS.removeStart(nm, "\"");
    nm = Strings.CS.removeEnd(nm, "\"");
    this.name = StringUtils.substringBeforeLast(nm, "(").trim();
    var identifier = StringUtils.substringAfterLast(nm, "(");
    this.id = Strings.CS.removeEnd(identifier, ")");

    application = webService.getKeyProperty("application");
    label = application + " > " + name;

    var calls = new ExecutionCounter(webService.getCanonicalName(), "calls");
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

  public Monitor callsMonitor() {
    return callsMonitor;
  }

  public Monitor executionTimeMonitor() {
    return executionTimeMonitor;
  }
}
