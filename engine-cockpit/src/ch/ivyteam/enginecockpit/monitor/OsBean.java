package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.delta;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.difference;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.format;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.percentage;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.value;
import static ch.ivyteam.ivy.environment.Ivy.cm;
import static java.lang.String.join;
import static org.apache.commons.lang3.StringUtils.SPACE;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.function.DoubleSupplier;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.monitor.Series;
import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.monitor.value.ValueProvider;

@ManagedBean
@ViewScoped
public class OsBean {
  private final Monitor memoryMonitor = Monitor.build().name(cm().co("/monitor/Memory"))
      .icon("analytics-board-graph-line")
      .yAxisLabel(cm().co("/monitor/Memory"))
      .reverseColors()
      .toMonitor();
  private final Monitor cpuMonitor = Monitor.build().name(cm().co("/monitor/CPULoad"))
      .icon("computer-chip")
      .yAxisLabel(cm().co("/monitor/Load"))
      .toMonitor();
  private final Monitor networkMonitor = Monitor.build().name(cm().co("/monitor/Network"))
      .icon("network-signal")
      .yAxisLabel(cm().co("/monitor/SendRecv"))
      .toMonitor();
  private final Monitor ioMonitor = Monitor.build().name(cm().co("/monitor/IO"))
      .icon("cd")
      .yAxisLabel(cm().co("/monitor/ReadWrite"))
      .toMonitor();

  private long[] oldTicks;
  private double cpuLoad;

  public OsBean() {
    setupCpuMonitor();
    setupMemoryMonitor();
    setupNetworkMonitor();
    setupIoMonitor();
  }

  private void setupCpuMonitor() {
    var cpuUsed = join(SPACE, cm().co("/monitor/Cores"), "%d");
    var threadsUsed = join(SPACE, cm().co("/monitor/Thread"), "%d");
    cpuMonitor.addInfoValue(format("%.1f", cpuLoad()));
    cpuMonitor.addInfoValue(format(cpuUsed, cpuCores()));
    cpuMonitor.addInfoValue(format(threadsUsed, cpuThreads()));
    cpuMonitor.addSeries(Series.build(cpuLoad(), cm().co("/monitor/Load")).fill().smoothLine().toSeries());
  }

  private void setupMemoryMonitor() {
    var memoryOf = "%4d " + cm().co("/monitor/Of") + " %4d (%.1f)";
    var jvmOf = "JVM %4d " + cm().co("/monitor/Of") + " %4d";
    memoryMonitor.addInfoValue(format(memoryOf, memoryUsed(), memoryMax(), memoryUsage()));
    memoryMonitor.addInfoValue(format(jvmOf, memoryJvmUsed(), memoryJvmMax()));
    memoryMonitor.addSeries(Series.build(memoryJvmMax(), cm().co("/monitor/JVMMaxMemory")).smoothLine().toSeries());
    memoryMonitor.addSeries(Series.build(memoryJvmUsed(), cm().co("/monitor/JVMMemoryUsage")).fill().smoothLine().toSeries());
    memoryMonitor.addSeries(Series.build(memoryUsed(), cm().co("/monitor/MemoryUsage")).fill().smoothLine().toSeries());

  }

  private void setupNetworkMonitor() {
    var sendingData = join(SPACE, cm().co("/monitor/Sending"), "%4d / %4d");
    var receivingData = join(SPACE, cm().co("/monitor/Receiving"), "%4d / %4d");
    networkMonitor.addInfoValue(format(sendingData, networkSend(), networkSendTotal()));
    networkMonitor.addInfoValue(format(receivingData, networkReceive(), networkReceiveTotal()));
    networkMonitor.addSeries(Series.build(networkSend(), cm().co("/common/Send")).smoothLine().toSeries());
    networkMonitor.addSeries(Series.build(networkReceive(), cm().co("/common/Receive")).smoothLine().toSeries());
  }

  private void setupIoMonitor() {
    var writeData = join(SPACE, cm().co("/monitor/Write"), "%4d / %4d");
    var readData = join(SPACE, cm().co("/monitor/Read"), "%4d / %4d");
    ioMonitor.addInfoValue(format(writeData, ioWrite(), ioWriteTotal()));
    ioMonitor.addInfoValue(format(readData, ioRead(), ioReadTotal()));
    ioMonitor.addSeries(Series.build(ioWrite(), cm().co("/common/Write")).smoothLine().toSeries());
    ioMonitor.addSeries(Series.build(ioRead(), cm().co("/common/Read")).smoothLine().toSeries());
  }

  public Monitor getCpuMonitor() {
    return cpuMonitor;
  }

  public Monitor getMemoryMonitor() {
    return memoryMonitor;
  }

  public Monitor getNetworkMonitor() {
    return networkMonitor;
  }

  public Monitor getIoMonitor() {
    return ioMonitor;
  }

  private ValueProvider cpuLoad() {
    return percentage(value((DoubleSupplier) this::processorLoad, Unit.PERCENTAGE));
  }

  private double processorLoad() {
    var hardware = SystemHardware.current();
    if (oldTicks == null) {
      oldTicks = hardware.cpuTicks();
    }
    var currentLoad = hardware.processorLoad(oldTicks);
    if (currentLoad != 0) {
      // sometimes 0, false-positive
      cpuLoad = currentLoad;
    }
    return cpuLoad;
  }

  private ValueProvider cpuCores() {
    return value(() -> SystemHardware.current().processorPhysicalCount(), Unit.ONE);
  }

  private ValueProvider cpuThreads() {
    return value(() -> SystemHardware.current().processorLogicalCount(), Unit.ONE);
  }

  private ValueProvider memoryUsed() {
    return difference(memoryMax(), memoryAvailable());
  }

  private ValueProvider memoryMax() {
    return value(() -> SystemHardware.current().memoryTotal(), Unit.BYTES);
  }

  private ValueProvider memoryAvailable() {
    return value(() -> SystemHardware.current().memoryAvailable(), Unit.BYTES);
  }

  private ValueProvider memoryUsage() {
    return ValueProvider.percentage(ValueProvider.quotient(memoryUsed(), memoryMax()));
  }

  private ValueProvider memoryJvmUsed() {
    return value(this::jvmUsed, Unit.BYTES);
  }

  private long jvmUsed() {
    // return Runtime.getRuntime().totalMemory();
    MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
    return memory.getHeapMemoryUsage().getCommitted() + memory.getNonHeapMemoryUsage().getCommitted();
  }

  private ValueProvider memoryJvmMax() {
    return value(Runtime.getRuntime()::maxMemory, Unit.BYTES);
  }

  private ValueProvider networkSend() {
    return delta(networkSendTotal());
  }

  private ValueProvider networkSendTotal() {
    return value(() -> SystemHardware.current().networkSendTotal(), Unit.BYTES);
  }

  private ValueProvider networkReceive() {
    return delta(networkReceiveTotal());
  }

  private ValueProvider networkReceiveTotal() {
    return value(() -> SystemHardware.current().networkReceiveTotal(), Unit.BYTES);
  }

  private ValueProvider ioWrite() {
    return delta(ioWriteTotal());
  }

  private ValueProvider ioWriteTotal() {
    return value(() -> SystemHardware.current().ioWriteTotal(), Unit.BYTES);
  }

  private ValueProvider ioRead() {
    return delta(ioReadTotal());
  }

  private ValueProvider ioReadTotal() {
    return value(() -> SystemHardware.current().ioReadTotal(), Unit.BYTES);
  }
}
