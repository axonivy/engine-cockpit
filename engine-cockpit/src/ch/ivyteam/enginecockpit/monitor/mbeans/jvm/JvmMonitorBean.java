package ch.ivyteam.enginecockpit.monitor.mbeans.jvm;

import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.attribute;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.format;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.percentage;
import static ch.ivyteam.ivy.environment.Ivy.cm;
import static java.lang.String.join;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;

import java.lang.management.ManagementFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.monitor.Series;
import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.monitor.value.ValueProvider;

@ManagedBean
@ViewScoped
public class JvmMonitorBean {
  private final Monitor cpuMonitor = Monitor.build().name(cm().co("/monitor/CPULoad"))
      .icon("computer-chip")
      .yAxisLabel(cm().co("/monitor/Load"))
      .toMonitor();
  private final Monitor classesMonitor = Monitor.build().name(cm().co("/monitor/Classes"))
      .icon("coffee-cup")
      .toMonitor();
  private final Monitor threadsMonitor = Monitor.build().name(cm().co("/monitor/Threads"))
      .icon("analytics-graph")
      .toMonitor();

  public JvmMonitorBean() {
    setupCpuMonitor();
    setupClassesMonitor();
    setupThreadsMonitor();
  }

  private void setupCpuMonitor() {
    cpuMonitor.addInfoValue(format(join(SPACE, cm().co("/monitor/System"), "%.1f"), systemCpuLoad()));
    cpuMonitor.addInfoValue(format(join(SPACE, cm().co("/common/AxonIvy"), "%.1f"), processCpuLoad()));
    cpuMonitor.addSeries(Series.build(systemCpuLoad(), cm().co("/monitor/System")).toSeries());
    cpuMonitor.addSeries(Series.build(processCpuLoad(), cm().co("/monitor/Process")).toSeries());
  }

  private void setupClassesMonitor() {
    classesMonitor.addInfoValue(format(join(SPACE, cm().co("/monitor/Loaded"), "%5d"), classesLoaded()));
    classesMonitor.addInfoValue(format(join(SPACE, cm().co("/monitor/Unloaded"), EMPTY, "%5d"), classesUnloaded()));
    classesMonitor.addInfoValue(format(join(SPACE, cm().co("/monitor/TotalLoaded"), "%5d"), classesTotalLoaded()));
    classesMonitor.addSeries(Series.build(classesLoaded(), cm().co("/monitor/Loaded")).toSeries());
    classesMonitor.addSeries(Series.build(classesUnloaded(), cm().co("/monitor/Unloaded")).toSeries());
  }

  private void setupThreadsMonitor() {
    threadsMonitor.addInfoValue(format(join(SPACE, cm().co("/monitor/Active"), "%5d") , threadsCount()));
    threadsMonitor.addInfoValue(format(join(SPACE, cm().co("/monitor/Daemons"), EMPTY, "%5d"), threadsDeamonCount()));
    threadsMonitor.addInfoValue(format(join(SPACE, cm().co("/monitor/Peak"), "%5d"), threadsPeakCount()));
    threadsMonitor.addInfoValue(format(join(SPACE, cm().co("/monitor/TotalStarted"), "%5d"), threadsTotalStarted()));
    threadsMonitor.addSeries(Series.build(threadsCount(), cm().co("/monitor/Active")).toSeries());
    threadsMonitor.addSeries(Series.build(threadsDeamonCount(), cm().co("/monitor/Daemons")).toSeries());
  }

  public Monitor getCpuMonitor() {
    return cpuMonitor;
  }

  public Monitor getClassesMonitor() {
    return classesMonitor;
  }

  public Monitor getThreadsMonitor() {
    return threadsMonitor;
  }

  private ValueProvider processCpuLoad() {
    return percentage(
        attribute(ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, "ProcessCpuLoad", Unit.PERCENTAGE));
  }

  private ValueProvider systemCpuLoad() {
    return percentage(
        attribute(ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, "SystemCpuLoad", Unit.PERCENTAGE));
  }

  private ValueProvider classesLoaded() {
    return attribute(ManagementFactory.CLASS_LOADING_MXBEAN_NAME, "LoadedClassCount", Unit.ONE);
  }

  private ValueProvider classesUnloaded() {
    return attribute(ManagementFactory.CLASS_LOADING_MXBEAN_NAME, "UnloadedClassCount", Unit.ONE);
  }

  private ValueProvider classesTotalLoaded() {
    return attribute(ManagementFactory.CLASS_LOADING_MXBEAN_NAME, "TotalLoadedClassCount", Unit.ONE);
  }

  private ValueProvider threadsDeamonCount() {
    return attribute(ManagementFactory.THREAD_MXBEAN_NAME, "DaemonThreadCount", Unit.ONE);
  }

  private ValueProvider threadsPeakCount() {
    return attribute(ManagementFactory.THREAD_MXBEAN_NAME, "PeakThreadCount", Unit.ONE);
  }

  private ValueProvider threadsTotalStarted() {
    return attribute(ManagementFactory.THREAD_MXBEAN_NAME, "TotalStartedThreadCount", Unit.ONE);
  }

  private ValueProvider threadsCount() {
    return attribute(ManagementFactory.THREAD_MXBEAN_NAME, "ThreadCount", Unit.ONE);
  }
}
