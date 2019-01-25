package ch.ivyteam.enginecockpit.monitor;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class MonitorBean
{
  private MemoryMonitor memory;
  private CpuMonitor cpu;
  private NetworkMonitor network;
  private IOMonitor io;
  
  public MonitorBean()
  {
    memory = new MemoryMonitor();
    cpu = new CpuMonitor();
    network = new NetworkMonitor();
    io = new IOMonitor();
  }

  public CpuMonitor getCpuMonitor()
  {
    return cpu;
  }
  
  public MemoryMonitor getMemoryMonitor()
  {
    return memory;
  }
  
  public NetworkMonitor getNetworkMonitor()
  {
    return network;
  }
  
  public IOMonitor getIoMonitor()
  {
    return io;
  }

}
