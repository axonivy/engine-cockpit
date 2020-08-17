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
    super(MonitorInfo.build().name("CPU Load").icon("computer").yAxisLabel("Load [%]").toInfo());
    initMonitor();
  }
  
  private void initMonitor()
  {
    Axis yAxis = model.getAxis(AxisType.Y);
    yAxis.setMin(0);
    yAxis.setMax(100);
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
      return String.format("CPU Load: -- / %d Cores, %d Threads", cores, threads);
    }
    calcNewValues();
    return String.format("CPU Load: %.1f%% / %d Cores, %d Threads", actualCpuLoad, cores, threads);
  }
  
  @Override
  protected void calcNewValues(long time)
  {
    actualCpuLoad = hardware.getProcessor().getSystemCpuLoad() * 100;
    cpuData.put(time, actualCpuLoad);
    cleanUpOldData(cpuData);
  }
  
}
