package ch.ivyteam.enginecockpit.monitor;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.LineChartSeries;

import oshi.hardware.HWDiskStore;

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
    super(MonitorInfo.build().name("IO").icon("storage").yAxisLabel("Read / Write [kB]").toInfo());
    initMonitor();
  }
  
  private void initMonitor()
  {
    Axis yAxis = model.getAxis(AxisType.Y);
    yAxis.setMin(0);
    
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
    HWDiskStore[] diskStores = hardware.getDiskStores();
    totalWrite = Arrays.asList(diskStores).stream().mapToLong(d -> d.getWriteBytes()).sum();
    totalRead = Arrays.asList(diskStores).stream().mapToLong(d -> d.getReadBytes()).sum();
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
  protected void calcNewValues(long time)
  {
    HWDiskStore[] diskStores = hardware.getDiskStores();
    actualWrite = Arrays.asList(diskStores).stream().mapToLong(d -> d.getWriteBytes()).sum() - totalWrite;
    actualRead = Arrays.asList(diskStores).stream().mapToLong(d -> d.getReadBytes()).sum() - totalRead;
    writeData.put(time, actualWrite / 1000);
    readData.put(time, actualRead / 1000);
    totalWrite += actualWrite;
    totalRead += actualRead;
    cleanUpOldData(writeData);
    cleanUpOldData(readData);
  }

}
