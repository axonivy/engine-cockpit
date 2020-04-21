package ch.ivyteam.enginecockpit.monitor.mbeans.jvm;

import static ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider.attribute;
import static ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider.composite;
import static ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider.delta;
import static ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider.format;
import static ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider.scale;

import java.lang.management.ManagementFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.mbeans.MMonitor;
import ch.ivyteam.enginecockpit.monitor.mbeans.MSeries;
import ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider;

@ManagedBean
@ViewScoped
public class MemoryMonitorBean
{
  private static final long MEGA = 1024l*1024l;
  private final MMonitor heapMemoryMonitor = MMonitor.build().name("Heap Memory").icon("memory").yAxisLabel("Memory [MB]").toMonitor();
  private final MMonitor nonHeapMemoryMonitor = MMonitor.build().name("Non Heap Memory").icon("memory").yAxisLabel("Memory [MB]").toMonitor();
  private final MMonitor gcMonitor = MMonitor.build().name("Garbage Collection").icon("delete").yAxisLabel("Time [ms]").toMonitor();
  
  public MemoryMonitorBean()
  {
    setupHeapMemoryMonitor();
    setupNonHeapMemoryMonitor();
    setupGcMonitors();
  }

  private void setupHeapMemoryMonitor()
  {
    heapMemoryMonitor.addInfoValue(format("Used %d MB", heapMemoryUsed()));
    heapMemoryMonitor.addInfoValue(format("Committed %d MB", heapMemoryCommitted()));
    heapMemoryMonitor.addInfoValue(format("Init %d MB", heapMemoryInit()));
    heapMemoryMonitor.addInfoValue(format("Max %d MB", heapMemoryMax()));
    heapMemoryMonitor.addSeries(new MSeries(heapMemoryUsed(), "Used"));
    heapMemoryMonitor.addSeries(new MSeries(heapMemoryCommitted(),"Committed"));
  }

  private void setupNonHeapMemoryMonitor()
  {
    nonHeapMemoryMonitor.addInfoValue(format("Used %d MB", nonHeapMemoryUsed()));
    nonHeapMemoryMonitor.addInfoValue(format("Committed %d MB", nonHeapMemoryCommitted()));
    nonHeapMemoryMonitor.addSeries(new MSeries(nonHeapMemoryUsed(), "Used"));
    nonHeapMemoryMonitor.addSeries(new MSeries(nonHeapMemoryCommitted(), "Committed"));
  }

  private void setupGcMonitors()
  {
    try
    {
      var garbageCollectors = ManagementFactory.getPlatformMBeanServer().queryNames(new ObjectName("java.lang:type=GarbageCollector,name=*"), null);
      for (ObjectName garbageCollector : garbageCollectors)
      {
        String label = garbageCollector.getKeyProperty("name");
        gcMonitor.addInfoValue(format(label + " %d ms/%d ms Count %d", deltaGcCollectionTime(garbageCollector), gcCollectionTime(garbageCollector), gcCollections(garbageCollector)));
        gcMonitor.addSeries(new MSeries(deltaGcCollectionTime(garbageCollector), label));
      }
    }
    catch(MalformedObjectNameException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  public Monitor getHeapMemoryMonitor()
  {
    return heapMemoryMonitor;
  }
  
  public Monitor getNonHeapMemoryMonitor()
  {
    return nonHeapMemoryMonitor;
  }
  
  public Monitor getGarbageCollectorsMonitor()
  {
    return gcMonitor;
  }
  
  private MValueProvider heapMemoryMax()
  {
    return scale(composite(attribute(ManagementFactory.MEMORY_MXBEAN_NAME, "HeapMemoryUsage"), "max"), MEGA);
  }
  
  private MValueProvider heapMemoryInit()
  {
    return scale(composite(attribute(ManagementFactory.MEMORY_MXBEAN_NAME, "HeapMemoryUsage"), "init"), MEGA);
  }

  private MValueProvider heapMemoryUsed()
  {
    return scale(composite(attribute(ManagementFactory.MEMORY_MXBEAN_NAME, "HeapMemoryUsage"), "used"), MEGA);
  }

  private MValueProvider heapMemoryCommitted()
  {
    return scale(composite(attribute(ManagementFactory.MEMORY_MXBEAN_NAME, "HeapMemoryUsage"), "committed"), MEGA);
  }

  private MValueProvider nonHeapMemoryCommitted()
  {
    return scale(composite(attribute(ManagementFactory.MEMORY_MXBEAN_NAME, "NonHeapMemoryUsage"), "committed"), MEGA);
  }

  private MValueProvider nonHeapMemoryUsed()
  {
    return scale(composite(attribute(ManagementFactory.MEMORY_MXBEAN_NAME, "NonHeapMemoryUsage"), "used"), MEGA);
  }

  private MValueProvider deltaGcCollectionTime(ObjectName garbabeCollector)
  {
    return delta(gcCollectionTime(garbabeCollector));
  }

  private MValueProvider gcCollectionTime(ObjectName garbabeCollector)
  {
    return attribute(garbabeCollector, "CollectionTime");
  }
  
  private MValueProvider gcCollections(ObjectName garbabeCollector)
  {
    return attribute(garbabeCollector, "CollectionCount");
  }
}
