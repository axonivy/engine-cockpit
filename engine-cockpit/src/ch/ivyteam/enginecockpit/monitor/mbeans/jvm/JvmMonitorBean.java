package ch.ivyteam.enginecockpit.monitor.mbeans.jvm;

import static ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider.attribute;
import static ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider.format;
import static ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider.percentage;

import java.lang.management.ManagementFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.mbeans.MMonitor;
import ch.ivyteam.enginecockpit.monitor.mbeans.MSeries;
import ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider;

@ManagedBean
@ViewScoped
public class JvmMonitorBean
{
  private final MMonitor cpuMonitor = MMonitor.build().name("CPU Load").icon("computer").yAxisLabel("Load [%]").toMonitor();
  private final MMonitor classesMonitor = MMonitor.build().name("Classes").icon("free_breakfast").toMonitor();
  private final MMonitor threadsMonitor = MMonitor.build().name("Threads").icon("gesture").toMonitor();
  
  public JvmMonitorBean()
  {
    setupCpuMonitor();
    setupClassesMonitor();
    setupThreadsMonitor();
  }

  private void setupCpuMonitor()
  {
    cpuMonitor.addInfoValue(format("System %.1f%%", systemCpuLoad()));
    cpuMonitor.addInfoValue(format("Axon.ivy %.1f%%", processCpuLoad()));
    cpuMonitor.addSeries(new MSeries(systemCpuLoad(), "System"));
    cpuMonitor.addSeries(new MSeries(processCpuLoad(), "Process"));
  }

  private void setupClassesMonitor()
  {
    classesMonitor.addInfoValue(format("Loaded %d", classesLoaded()));
    classesMonitor.addInfoValue(format("Unloaded  %d", classesUnloaded()));
    classesMonitor.addInfoValue(format("Total Loaded %d", classesTotalLoaded()));
    classesMonitor.addSeries(new MSeries(classesLoaded(), "Loaded"));
    classesMonitor.addSeries(new MSeries(classesUnloaded(), "Unloaded"));
  }

  private void setupThreadsMonitor()
  {
    threadsMonitor.addInfoValue(format("Active %d", threadsCount()));
    threadsMonitor.addInfoValue(format("Daemons  %d", threadsDeamonCount()));
    threadsMonitor.addInfoValue(format("Peak %d", threadsPeakCount()));
    threadsMonitor.addInfoValue(format("Total Started %d", threadsTotalStarted()));
    threadsMonitor.addSeries(new MSeries(threadsCount(), "Active"));
    threadsMonitor.addSeries(new MSeries(threadsDeamonCount(), "Daemons"));
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
  
  private MValueProvider processCpuLoad()
  {
    return percentage(attribute(ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, "ProcessCpuLoad"));
  }

  private MValueProvider systemCpuLoad()
  {
    return percentage(attribute(ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, "SystemCpuLoad"));
  }
  
  private MValueProvider classesLoaded()
  {
    return attribute(ManagementFactory.CLASS_LOADING_MXBEAN_NAME, "LoadedClassCount");
  }

  private MValueProvider classesUnloaded()
  {
    return attribute(ManagementFactory.CLASS_LOADING_MXBEAN_NAME, "UnloadedClassCount");
  }

  private MValueProvider classesTotalLoaded()
  {
    return attribute(ManagementFactory.CLASS_LOADING_MXBEAN_NAME, "TotalLoadedClassCount");
  }

  private MValueProvider threadsDeamonCount()
  {
    return attribute(ManagementFactory.THREAD_MXBEAN_NAME, "DaemonThreadCount");
  }
  
  private MValueProvider threadsPeakCount()
  {
    return attribute(ManagementFactory.THREAD_MXBEAN_NAME, "PeakThreadCount");
  }

  private MValueProvider threadsTotalStarted()
  {
    return attribute(ManagementFactory.THREAD_MXBEAN_NAME, "TotalStartedThreadCount");
  }

  private MValueProvider threadsCount()
  {
    return attribute(ManagementFactory.THREAD_MXBEAN_NAME, "ThreadCount");
  }
}
