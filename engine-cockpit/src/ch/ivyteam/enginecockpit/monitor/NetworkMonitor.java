package ch.ivyteam.enginecockpit.monitor;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.LineChartSeries;

public class NetworkMonitor extends SystemMonitor
{
  private LineChartSeries networkSend;
  private LineChartSeries networkResv;
  
  private Map<Object, Number> sendData;
  private Map<Object, Number> resvData;
  
  private long totalSend;
  private long totalResv;
  private long actualSend;
  private long actualResv;
  
  public NetworkMonitor()
  {
    initMonitor();
  }
  
  private void initMonitor()
  {
    Axis yAxis = model.getAxis(AxisType.Y);
    yAxis.setMin(0);
    yAxis.setLabel("Send / Resv [kB]");
    Axis xAxis = model.getAxis(AxisType.X);
    xAxis.setTickCount(11);
    xAxis.setLabel("Time [s]");
    
    sendData = new LinkedHashMap<>();
    resvData = new LinkedHashMap<>();
    
    networkSend = new LineChartSeries();
    networkSend.setSmoothLine(true);
    networkSend.setLabel("Send");
    networkSend.setData(sendData);
    networkSend.setShowMarker(false);
    networkResv = new LineChartSeries();
    networkResv.setSmoothLine(true);
    networkResv.setLabel("Resv");
    networkResv.setData(resvData);
    networkResv.setShowMarker(false);
    model.setLegendPosition("ne");
    model.addSeries(networkSend);
    model.addSeries(networkResv);
    totalSend = Arrays.asList(hardware.getNetworkIFs()).stream().mapToLong(n -> n.getBytesSent()).sum();
    totalResv = Arrays.asList(hardware.getNetworkIFs()).stream().mapToLong(n -> n.getBytesRecv()).sum();
  }
  
  @Override
  public String getInfo()
  {
    if (!isRunning())
    {
      return "Sending: -- kB / Reseiving: -- kB";
    }
    calcNewValues();
    return String.format("Sending: %dkB / Reseiving: %dkB", actualSend / 1000, actualResv / 1000);
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
    actualSend = Arrays.asList(hardware.getNetworkIFs()).stream().mapToLong(n -> n.getBytesSent()).sum() - totalSend;
    actualResv = Arrays.asList(hardware.getNetworkIFs()).stream().mapToLong(n -> n.getBytesRecv()).sum() - totalResv;
    sendData.put(actualSec, actualSend / 1000);
    resvData.put(actualSec, actualResv / 1000);
    totalSend += actualSend;
    totalResv += actualResv;
    cleanUpOldData(sendData);
    cleanUpOldData(resvData);
  }

}
