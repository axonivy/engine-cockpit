package ch.ivyteam.enginecockpit.monitor;

import java.util.LinkedHashMap;
import java.util.Map;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.LineChartSeries;

public class CpuMonitor extends SystemMonitor
{
  private LineChartSeries cpuLoad;
  private Map<Object, Number> cpuData;
  private double actualCpuLoad;
  private int cores;
  private int threads;

  public CpuMonitor() 
  {
    initMonitor();
  }
  
  private void initMonitor()
  {
    Axis yAxis = model.getAxis(AxisType.Y);
    yAxis.setMin(0);
    yAxis.setMax(100);
    yAxis.setLabel("Load [%]");
    Axis xAxis = model.getAxis(AxisType.X);
    xAxis.setTickCount(11);
    xAxis.setLabel("Time [s]");
    cpuData = new LinkedHashMap<>();
    
    cpuLoad = new LineChartSeries();
    cpuLoad.setFill(true);
    cpuLoad.setSmoothLine(true);
    cpuLoad.setData(cpuData);
    model.addSeries(cpuLoad);
    
    cores = hardware.getProcessor().getPhysicalProcessorCount();
    threads = hardware.getProcessor().getLogicalProcessorCount();
  }
  
  @Override
  public String getInfo()
  {
    if (!isRunning())
    {
      return String.format("Cpu load: -- / %d Cores, %d Threads", cores, threads);
    }
    calcNewValues();
    return String.format("Cpu load: %.1f%% / %d Cores, %d Threads", actualCpuLoad, cores, threads);
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
    actualCpuLoad = hardware.getProcessor().getSystemCpuLoad() * 100;
    cpuData.put(actualSec, actualCpuLoad);
    cleanUpOldData(cpuData);
  }
  
}
