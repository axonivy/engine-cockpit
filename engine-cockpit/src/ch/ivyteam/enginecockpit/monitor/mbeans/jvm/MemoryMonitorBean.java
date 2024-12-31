package ch.ivyteam.enginecockpit.monitor.mbeans.jvm;

import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.attribute;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.composite;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.delta;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.format;

import java.lang.management.ManagementFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.monitor.Series;
import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.monitor.value.ValueProvider;

@ManagedBean
@ViewScoped
public class MemoryMonitorBean {
  private final Monitor heapMemoryMonitor = Monitor.build().name("Heap Memory")
      .icon("analytics-board-graph-line").yAxisLabel("Memory").toMonitor();
  private final Monitor nonHeapMemoryMonitor = Monitor.build().name("Non Heap Memory")
      .icon("analytics-board-graph-line").yAxisLabel("Memory").toMonitor();
  private final Monitor gcMonitor = Monitor.build().name("Garbage Collection").icon("recycling-trash-bin-2")
      .yAxisLabel("Time").toMonitor();

  public MemoryMonitorBean() {
    setupHeapMemoryMonitor();
    setupNonHeapMemoryMonitor();
    setupGcMonitors();
  }

  private void setupHeapMemoryMonitor() {
    heapMemoryMonitor.addInfoValue(format("Used %5d", heapMemoryUsed()));
    heapMemoryMonitor.addInfoValue(format("Committed %5d", heapMemoryCommitted()));
    heapMemoryMonitor.addInfoValue(format("Init %5d", heapMemoryInit()));
    heapMemoryMonitor.addInfoValue(format("Max %5d", heapMemoryMax()));
    heapMemoryMonitor.addSeries(Series.build(heapMemoryUsed(), "Used").toSeries());
    heapMemoryMonitor.addSeries(Series.build(heapMemoryCommitted(), "Committed").toSeries());
  }

  private void setupNonHeapMemoryMonitor() {
    nonHeapMemoryMonitor.addInfoValue(format("Used %5d", nonHeapMemoryUsed()));
    nonHeapMemoryMonitor.addInfoValue(format("Committed %5d", nonHeapMemoryCommitted()));
    nonHeapMemoryMonitor.addSeries(Series.build(nonHeapMemoryUsed(), "Used").toSeries());
    nonHeapMemoryMonitor.addSeries(Series.build(nonHeapMemoryCommitted(), "Committed").toSeries());
  }

  private void setupGcMonitors() {
    try {
      var garbageCollectors = ManagementFactory.getPlatformMBeanServer()
          .queryNames(new ObjectName("java.lang:type=GarbageCollector,name=*"), null);
      for (ObjectName garbageCollector : garbageCollectors) {
        String label = garbageCollector.getKeyProperty("name");
        gcMonitor.addInfoValue(format(label + " %t/%t Count %5d", deltaGcCollectionTime(garbageCollector),
            gcCollectionTime(garbageCollector), gcCollections(garbageCollector)));
        gcMonitor.addSeries(Series.build(deltaGcCollectionTime(garbageCollector), label).toSeries());
      }
    } catch (MalformedObjectNameException ex) {
      throw new RuntimeException(ex);
    }
  }

  public Monitor getHeapMemoryMonitor() {
    return heapMemoryMonitor;
  }

  public Monitor getNonHeapMemoryMonitor() {
    return nonHeapMemoryMonitor;
  }

  public Monitor getGarbageCollectorsMonitor() {
    return gcMonitor;
  }

  private ValueProvider heapMemoryMax() {
    return composite(attribute(ManagementFactory.MEMORY_MXBEAN_NAME, "HeapMemoryUsage", Unit.ONE), "max",
        Unit.BYTES);
  }

  private ValueProvider heapMemoryInit() {
    return composite(attribute(ManagementFactory.MEMORY_MXBEAN_NAME, "HeapMemoryUsage", Unit.ONE), "init",
        Unit.BYTES);
  }

  private ValueProvider heapMemoryUsed() {
    return composite(attribute(ManagementFactory.MEMORY_MXBEAN_NAME, "HeapMemoryUsage", Unit.ONE), "used",
        Unit.BYTES);
  }

  private ValueProvider heapMemoryCommitted() {
    return composite(attribute(ManagementFactory.MEMORY_MXBEAN_NAME, "HeapMemoryUsage", Unit.ONE),
        "committed", Unit.BYTES);
  }

  private ValueProvider nonHeapMemoryCommitted() {
    return composite(attribute(ManagementFactory.MEMORY_MXBEAN_NAME, "NonHeapMemoryUsage", Unit.ONE),
        "committed", Unit.BYTES);
  }

  private ValueProvider nonHeapMemoryUsed() {
    return composite(attribute(ManagementFactory.MEMORY_MXBEAN_NAME, "NonHeapMemoryUsage", Unit.ONE), "used",
        Unit.BYTES);
  }

  private ValueProvider deltaGcCollectionTime(ObjectName garbabeCollector) {
    return delta(gcCollectionTime(garbabeCollector));
  }

  private ValueProvider gcCollectionTime(ObjectName garbabeCollector) {
    return attribute(garbabeCollector, "CollectionTime", Unit.MILLI_SECONDS);
  }

  private ValueProvider gcCollections(ObjectName garbabeCollector) {
    return attribute(garbabeCollector, "CollectionCount", Unit.ONE);
  }
}
