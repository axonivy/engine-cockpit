package ch.ivyteam.enginecockpit.monitor;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.LineChartSeries;

import oshi.hardware.GlobalMemory;

public class MemoryMonitor extends Monitor
{
  private LineChartSeries memoryLoad;
  private LineChartSeries jvmMemory;
  private LineChartSeries totalJvmMemory;
  private GlobalMemory memory;
  private double maxMem;
  private double totalJvmMem;
  private double actualMem;
  private double actualJvmMem;
  
  public MemoryMonitor() 
  {
    super();
  }
  
  @Override
  protected void initMonitor()
  {
    super.initMonitor();
    memory = systemInfo.getHardware().getMemory();
    maxMem = memory.getTotal();
    totalJvmMem = Runtime.getRuntime().maxMemory() / 1000000000.0;
    Axis yAxis = model.getAxis(AxisType.Y);
    yAxis.setMin(0);
    yAxis.setMax(maxMem / 1000000000.0);
    yAxis.setLabel("Memory [GB]");
    Axis xAxis = model.getAxis(AxisType.X);
    xAxis.setTickCount(11);
    xAxis.setLabel("Time [s]");
    memoryLoad = new LineChartSeries();
    memoryLoad.setFill(true);
    memoryLoad.setSmoothLine(true);
    memoryLoad.setLabel("Memory usage");
    jvmMemory = new LineChartSeries();
    jvmMemory.setFill(true);
    jvmMemory.setSmoothLine(true);
    jvmMemory.setLabel("Jvm memory usage");
    totalJvmMemory = new LineChartSeries();
    totalJvmMemory.setShowMarker(false);
    totalJvmMemory.setLabel("Jvm max memory");
    model.addSeries(memoryLoad);
    model.addSeries(jvmMemory);
    model.addSeries(totalJvmMemory);
    model.setLegendPosition("ne");
  }
  
  @Override
  public String getInfo()
  {
    if (!isRunning())
    {
      return String.format("Memory: %.1fGB total", maxMem / 1000000000.0);
    }
    calcNewValues();
    return String.format("Memory: %.1fGB of %.1fGB (%.1f%%) / JVM: %.1fGB of %.1fGB", actualMem, maxMem / 1000000000.0,
            (actualMem / (maxMem / 1000000000)) * 100, actualJvmMem, totalJvmMem);
  }

  @Override
  protected void calcNewValues()
  {
    long time = newTime();
    if (time == actualSec && actualSec != 0)
    {
      return;
    }
    actualSec = time;
    setXAxis(actualSec);
    actualMem = (maxMem - memory.getAvailable()) / 1000000000.0;
    memoryLoad.set(actualSec, actualMem);
    actualJvmMem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000000.0;
    jvmMemory.set(actualSec, actualJvmMem);
    totalJvmMemory.set(actualSec, totalJvmMem);
  }
  
}
