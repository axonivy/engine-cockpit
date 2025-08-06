package ch.ivyteam.enginecockpit.monitor.mbeans.jvm;

import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.attribute;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.composite;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.delta;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.format;
import static ch.ivyteam.ivy.environment.Ivy.cm;
import static ch.ivyteam.ivy.environment.Ivy.cms;
import static java.lang.String.join;
import static org.apache.commons.lang3.StringUtils.SPACE;

import java.lang.management.ManagementFactory;
import java.util.List;

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
  private final Monitor heapMemoryMonitor = Monitor.build().name(cm().co("/monitor/HeapMemory"))
      .icon("analytics-board-graph-line")
      .yAxisLabel(cm().co("/monitor/Memory"))
      .toMonitor();
  private final Monitor nonHeapMemoryMonitor = Monitor.build().name(cm().co("/monitor/NonHeapMemory"))
      .icon("analytics-board-graph-line")
      .yAxisLabel(cm().co("/monitor/Memory"))
      .toMonitor();
  private final Monitor gcMonitor = Monitor.build().name(cm().co("/monitor/GarbageCollection"))
      .icon("recycling-trash-bin-2")
      .yAxisLabel(cm().co("/monitor/Time"))
      .toMonitor();

  public MemoryMonitorBean() {
    setupHeapMemoryMonitor();
    setupNonHeapMemoryMonitor();
    setupGcMonitors();
  }

  private void setupHeapMemoryMonitor() {
    var usedInfo = join(SPACE, cm().co("/monitor/Used"), "%5d");
    var committedInfo = join(SPACE, cm().co("/monitor/Committed"), "%5d");
    var initInfo = join(SPACE, cm().co("/monitor/Init"), "%5d");
    var maxInfo = join(SPACE, cm().co("/monitor/Max"), "%5d");
    heapMemoryMonitor.addInfoValue(format(usedInfo, heapMemoryUsed()));
    heapMemoryMonitor.addInfoValue(format(committedInfo, heapMemoryCommitted()));
    heapMemoryMonitor.addInfoValue(format(initInfo, heapMemoryInit()));
    heapMemoryMonitor.addInfoValue(format(maxInfo, heapMemoryMax()));
    heapMemoryMonitor.addSeries(Series.build(heapMemoryUsed(), cm().co("/monitor/Used")).toSeries());
    heapMemoryMonitor.addSeries(Series.build(heapMemoryCommitted(), cm().co("/monitor/Committed")).toSeries());
  }

  private void setupNonHeapMemoryMonitor() {
    var usedInfo = join(SPACE, cm().co("/monitor/Used"), "%5d");
    var committedInfo = join(SPACE, cm().co("/monitor/Committed"), "%5d");
    nonHeapMemoryMonitor.addInfoValue(format(usedInfo, nonHeapMemoryUsed()));
    nonHeapMemoryMonitor.addInfoValue(format(committedInfo, nonHeapMemoryCommitted()));
    nonHeapMemoryMonitor.addSeries(Series.build(nonHeapMemoryUsed(), cm().co("/monitor/Used")).toSeries());
    nonHeapMemoryMonitor.addSeries(Series.build(nonHeapMemoryCommitted(), cm().co("/monitor/Committed")).toSeries());
  }

  private void setupGcMonitors() {
    try {
      var garbageCollectors = ManagementFactory.getPlatformMBeanServer()
          .queryNames(new ObjectName("java.lang:type=GarbageCollector,name=*"), null);
      for (ObjectName garbageCollector : garbageCollectors) {
        String label = garbageCollector.getKeyProperty("name");
        var gcCountLabel = cms().co("/monitor/GCCount", List.of(label, "%t/%t", "%5d"));
        gcMonitor.addInfoValue(format(gcCountLabel, deltaGcCollectionTime(garbageCollector),
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
