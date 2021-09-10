package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.delta;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.difference;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.format;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.percentage;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.value;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.monitor.Series;
import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.monitor.value.ValueProvider;

@ManagedBean
@ViewScoped
public class OsBean {
  private Monitor memoryMonitor = Monitor.build().name("Memory").icon("analytics-board-graph-line")
          .yAxisLabel("Memory").toMonitor();
  private Monitor cpuMonitor = Monitor.build().name("CPU Load").icon("computer-chip").yAxisLabel("Load")
          .toMonitor();
  private Monitor networkMonitor = Monitor.build().name("Network").icon("network-signal")
          .yAxisLabel("Send / Resv").toMonitor();
  private Monitor ioMonitor = Monitor.build().name("IO").icon("cd").yAxisLabel("Read / Write").toMonitor();

  public OsBean() {
    setupCpuMonitor();
    setupMemoryMonitor();
    setupNetworkMonitor();
    setupIoMonitor();
  }

  private void setupCpuMonitor() {
    cpuMonitor.addInfoValue(format("%.1f", cpuLoad()));
    cpuMonitor.addInfoValue(format("%d Cores", cpuCores()));
    cpuMonitor.addInfoValue(format("%d Threads", cpuThreads()));
    cpuMonitor.addSeries(Series.build(cpuLoad(), "Load").fill().smoothLine().toSeries());
  }

  private void setupMemoryMonitor() {
    memoryMonitor.addInfoValue(format("%4d of %4d (%.1f)", memoryUsed(), memoryMax(), memoryUsage()));
    memoryMonitor.addInfoValue(format("JVM %4d of %4d", memoryJvmUsed(), memoryJvmMax()));
    memoryMonitor.addSeries(Series.build(memoryUsed(), "Memory usage").fill().smoothLine().toSeries());
    memoryMonitor.addSeries(Series.build(memoryJvmUsed(), "Jvm memory usage").fill().smoothLine().toSeries());
    memoryMonitor.addSeries(Series.build(memoryJvmMax(), "Jvm max memory").smoothLine().toSeries());
  }

  private void setupNetworkMonitor() {
    networkMonitor.addInfoValue(format("Sending %4d / %4d", networkSend(), networkSendTotal()));
    networkMonitor.addInfoValue(format("Receiving %4d / %4d", networkReceive(), networkReceiveTotal()));
    networkMonitor.addSeries(Series.build(networkSend(), "Send").smoothLine().toSeries());
    networkMonitor.addSeries(Series.build(networkReceive(), "Receive").smoothLine().toSeries());
  }

  private void setupIoMonitor() {
    ioMonitor.addInfoValue(format("Write %4d / %4d", ioWrite(), ioWriteTotal()));
    ioMonitor.addInfoValue(format("Read %4d / %4d", ioRead(), ioReadTotal()));
    ioMonitor.addSeries(Series.build(ioWrite(), "Write").smoothLine().toSeries());
    ioMonitor.addSeries(Series.build(ioRead(), "Read").smoothLine().toSeries());
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
    return percentage(value(() -> SystemHardware.current().processorLoad(), Unit.PERCENTAGE));
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
    return difference(memoryJvmMax(), value(Runtime.getRuntime()::freeMemory, Unit.BYTES));
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
