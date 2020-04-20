package ch.ivyteam.enginecockpit.monitor;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

public abstract class SystemMonitor extends Monitor
{
  protected final SystemInfo systemInfo;
  protected final HardwareAbstractionLayer hardware;

  public SystemMonitor()
  {
    systemInfo = new SystemInfo();
    hardware = systemInfo.getHardware();
  }
}
