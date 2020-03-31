package ch.ivyteam.enginecockpit.monitor;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.LineChartSeries;

public class IOMonitor extends SystemMonitor
{
  private LineChartSeries ioWrite;
  private LineChartSeries ioRead;
  
  private Map<Object, Number> writeData;
  private Map<Object, Number> readData;
  
  private long totalWrite;
  private long totalRead;
  private long actualWrite;
  private long actualRead;
  
  public IOMonitor()
  {
    initMonitor();
  }
  
  private void initMonitor()
  {
    Axis yAxis = model.getAxis(AxisType.Y);
    yAxis.setMin(0);
    yAxis.setLabel("Read / Write [kB]");
    Axis xAxis = model.getAxis(AxisType.X);
    xAxis.setTickCount(11);
    xAxis.setLabel("Time [s]");
    
    writeData = new LinkedHashMap<>();
    readData = new LinkedHashMap<>();
    
    ioWrite = new LineChartSeries();
    ioWrite.setSmoothLine(true);
    ioWrite.setLabel("Write");
    ioWrite.setData(writeData);
    ioWrite.setShowMarker(false);
    ioRead = new LineChartSeries();
    ioRead.setSmoothLine(true);
    ioRead.setLabel("Read");
    ioRead.setData(readData);
    ioRead.setShowMarker(false);
    model.setLegendPosition("ne");
    model.addSeries(ioWrite);
    model.addSeries(ioRead);
    totalWrite = Arrays.asList(hardware.getDiskStores()).stream().mapToLong(d -> d.getWriteBytes()).sum();
    totalRead = Arrays.asList(hardware.getDiskStores()).stream().mapToLong(d -> d.getReadBytes()).sum();
  }
  
  @Override
  public String getInfo()
  {
    if (!isRunning())
    {
      return "Write: -- kB / Read: -- kB";
    }
    calcNewValues();
    return String.format("Write: %dkB / Read: %dkB", actualWrite / 1000, actualRead / 1000);
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
    actualWrite = Arrays.asList(hardware.getDiskStores()).stream().mapToLong(d -> d.getWriteBytes()).sum() - totalWrite;
    actualRead = Arrays.asList(hardware.getDiskStores()).stream().mapToLong(d -> d.getReadBytes()).sum() - totalRead;
    writeData.put(actualSec, actualWrite / 1000);
    readData.put(actualSec, actualRead / 1000);
    totalWrite += actualWrite;
    totalRead += actualRead;
    cleanUpOldData(writeData);
    cleanUpOldData(readData);
  }

}
