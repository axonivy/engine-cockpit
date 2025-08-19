package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.format;

import javax.management.ObjectName;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.monitor.Series;
import ch.ivyteam.enginecockpit.util.CmsUtil;
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
    callsMonitor.addInfoValue(format(CmsUtil.coWithDefault("/common/Total", "Total") + " %5d", calls.executions()));
    callsMonitor.addInfoValue(format(CmsUtil.coWithDefault("/common/Errors", "Errors") + " %5d", calls.deltaErrors()));
    callsMonitor.addInfoValue(format(CmsUtil.coWithDefault("/common/ErrorsTotal", "Errors Total") + " %5d", calls.errors()));
    callsMonitor.addSeries(Series.build(calls.deltaExecutions(), Ivy.cm().co("/liveStats/Calls")).toSeries());
    callsMonitor.addSeries(Series.build(calls.deltaErrors(), Ivy.cm().co("/common/Errors")).toSeries());

    executionTimeMonitor.addInfoValue(format(CmsUtil.coWithDefault("/liveStats/Min", "Min") + " %t", calls.deltaMinExecutionTime()));
    executionTimeMonitor.addInfoValue(format(CmsUtil.coWithDefault("/liveStats/Avg", "Avg") + " %t", calls.deltaAvgExecutionTime()));
    executionTimeMonitor.addInfoValue(format(CmsUtil.coWithDefault("/liveStats/Max", "Max") + " %t", calls.deltaMaxExecutionTime()));
    executionTimeMonitor.addInfoValue(format(CmsUtil.coWithDefault("/common/Total", "Total") + " %t", calls.executionTime()));
    executionTimeMonitor.addSeries(Series.build(calls.deltaMinExecutionTime(), CmsUtil.coWithDefault("/liveStats/Min", "Min")).toSeries());
    executionTimeMonitor.addSeries(Series.build(calls.deltaAvgExecutionTime(), CmsUtil.coWithDefault("/liveStats/Avg", "Avg")).toSeries());
    executionTimeMonitor.addSeries(Series.build(calls.deltaMaxExecutionTime(), CmsUtil.coWithDefault("/liveStats/Max", "Max")).toSeries());
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
