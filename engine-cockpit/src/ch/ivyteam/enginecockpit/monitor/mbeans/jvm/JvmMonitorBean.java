package ch.ivyteam.enginecockpit.monitor.mbeans.jvm;

import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.attribute;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.format;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.percentage;

import java.lang.management.ManagementFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.monitor.Series;
import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.monitor.value.ValueProvider;

@ManagedBean
@ViewScoped
public class JvmMonitorBean
{
  private final Monitor cpuMonitor = Monitor.build().name("CPU Load").icon("computer-chip").yAxisLabel("Load").toMonitor();
  private final Monitor classesMonitor = Monitor.build().name("Classes").icon("coffee-cup").toMonitor();
  private final Monitor threadsMonitor = Monitor.build().name("Threads").icon("analytics-graph").toMonitor();
  
  public JvmMonitorBean()
  {
    setupCpuMonitor();
    setupClassesMonitor();
    setupThreadsMonitor();
  }

  private void setupCpuMonitor()
  {
    cpuMonitor.addInfoValue(format("System %.1f", systemCpuLoad()));
    cpuMonitor.addInfoValue(format("Axon.ivy %.1f", processCpuLoad()));
    cpuMonitor.addSeries(Series.build(systemCpuLoad(), "System").toSeries());
    cpuMonitor.addSeries(Series.build(processCpuLoad(), "Process").toSeries());
  }

  private void setupClassesMonitor()
  {
    classesMonitor.addInfoValue(format("Loaded %5d", classesLoaded()));
    classesMonitor.addInfoValue(format("Unloaded  %5d", classesUnloaded()));
    classesMonitor.addInfoValue(format("Total Loaded %5d", classesTotalLoaded()));
    classesMonitor.addSeries(Series.build(classesLoaded(), "Loaded").toSeries());
    classesMonitor.addSeries(Series.build(classesUnloaded(), "Unloaded").toSeries());
  }

  private void setupThreadsMonitor()
  {
    threadsMonitor.addInfoValue(format("Active %5d", threadsCount()));
    threadsMonitor.addInfoValue(format("Daemons  %5d", threadsDeamonCount()));
    threadsMonitor.addInfoValue(format("Peak %5d", threadsPeakCount()));
    threadsMonitor.addInfoValue(format("Total Started %5d", threadsTotalStarted()));
    threadsMonitor.addSeries(Series.build(threadsCount(), "Active").toSeries());
    threadsMonitor.addSeries(Series.build(threadsDeamonCount(), "Daemons").toSeries());
  }

  public Monitor getCpuMonitor()
  {
    return cpuMonitor;
  }
  
  public Monitor getClassesMonitor()
  {
    return classesMonitor;
  }
  
  public Monitor getThreadsMonitor()
  {
    return threadsMonitor;
  }
  
  private ValueProvider processCpuLoad()
  {
    return percentage(attribute(ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, "ProcessCpuLoad", Unit.PERCENTAGE));
  }

  private ValueProvider systemCpuLoad()
  {
    return percentage(attribute(ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, "SystemCpuLoad", Unit.PERCENTAGE));
  }
  
  private ValueProvider classesLoaded()
  {
    return attribute(ManagementFactory.CLASS_LOADING_MXBEAN_NAME, "LoadedClassCount", Unit.ONE);
  }

  private ValueProvider classesUnloaded()
  {
    return attribute(ManagementFactory.CLASS_LOADING_MXBEAN_NAME, "UnloadedClassCount", Unit.ONE);
  }

  private ValueProvider classesTotalLoaded()
  {
    return attribute(ManagementFactory.CLASS_LOADING_MXBEAN_NAME, "TotalLoadedClassCount", Unit.ONE);
  }

  private ValueProvider threadsDeamonCount()
  {
    return attribute(ManagementFactory.THREAD_MXBEAN_NAME, "DaemonThreadCount", Unit.ONE);
  }
  
  private ValueProvider threadsPeakCount()
  {
    return attribute(ManagementFactory.THREAD_MXBEAN_NAME, "PeakThreadCount", Unit.ONE);
  }

  private ValueProvider threadsTotalStarted()
  {
    return attribute(ManagementFactory.THREAD_MXBEAN_NAME, "TotalStartedThreadCount", Unit.ONE);
  }

  private ValueProvider threadsCount()
  {
    return attribute(ManagementFactory.THREAD_MXBEAN_NAME, "ThreadCount", Unit.ONE);
  }
}
