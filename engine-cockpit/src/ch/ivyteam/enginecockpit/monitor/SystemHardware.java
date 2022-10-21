package ch.ivyteam.enginecockpit.monitor;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

@ManagedBean
@RequestScoped
public class SystemHardware {
  private static final HardwareAbstractionLayer HARDWARE;
  private static final GlobalMemory MEMORY;
  private static final CentralProcessor PROCESSOR;
  private final long ioWriteTotal;
  private final long ioReadTotal;
  private final long networkReceiveTotal;
  private final long networkSendTotal;
  private final long memoryTotal;
  private final long memoryAvailable;
  private final int processorLogicalCount;
  private final double processorLoad;
  private final int processorPhysicalCount;

  static {
    HARDWARE = new SystemInfo().getHardware();
    MEMORY = HARDWARE.getMemory();
    PROCESSOR = HARDWARE.getProcessor();
  }

  public SystemHardware() {
    var diskStores = HARDWARE.getDiskStores();
    ioWriteTotal = diskStores.stream().mapToLong(n -> n.getWriteBytes()).sum();
    ioReadTotal = diskStores.stream().mapToLong(n -> n.getReadBytes()).sum();
    var networkIFs = HARDWARE.getNetworkIFs();
    networkReceiveTotal = networkIFs.stream().mapToLong(n -> n.getBytesRecv()).sum();
    networkSendTotal = networkIFs.stream().mapToLong(n -> n.getBytesSent()).sum();
    memoryAvailable = MEMORY.getAvailable();
    memoryTotal = MEMORY.getTotal();
    processorLogicalCount = PROCESSOR.getLogicalProcessorCount();
    processorPhysicalCount = PROCESSOR.getPhysicalProcessorCount();
    processorLoad = PROCESSOR.getSystemCpuLoad(300);
  }

  static SystemHardware current() {
    FacesContext context = FacesContext.getCurrentInstance();
    return context.getApplication().evaluateExpressionGet(context, "#{systemHardware}", SystemHardware.class);
  }

  long ioWriteTotal() {
    return ioWriteTotal;
  }

  long ioReadTotal() {
    return ioReadTotal;
  }

  long networkReceiveTotal() {
    return networkReceiveTotal;
  }

  long networkSendTotal() {
    return networkSendTotal;
  }

  long memoryAvailable() {
    return memoryAvailable;
  }

  long memoryTotal() {
    return memoryTotal;
  }

  int processorLogicalCount() {
    return processorLogicalCount;
  }

  int processorPhysicalCount() {
    return processorPhysicalCount;
  }

  double processorLoad() {
    return processorLoad;
  }
}
