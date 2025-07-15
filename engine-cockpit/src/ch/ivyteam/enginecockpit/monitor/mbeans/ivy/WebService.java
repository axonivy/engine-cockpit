package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.format;

import javax.management.ObjectName;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.monitor.Series;

class WebService {
  public static final WebService NO_DATA = new WebService();

  private final Monitor callsMonitor = Monitor.build().name("Calls").title("Web Service Calls")
      .icon("language").toMonitor();
  private final Monitor executionTimeMonitor = Monitor.build().name("Execution Time")
      .title("Web Service Execution Time").icon("timer").yAxisLabel("Execution Time").toMonitor();

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
      label = "No Data";
      callsMonitor.addInfoValue(format("No data available"));
      executionTimeMonitor.addInfoValue(format("No data available"));
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

  public Monitor callsMonitor() {
    return callsMonitor;
  }

  public Monitor executionTimeMonitor() {
    return executionTimeMonitor;
  }
}
