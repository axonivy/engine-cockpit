package ch.ivyteam.enginecockpit.monitor;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.LineChartSeries;

import oshi.hardware.NetworkIF;

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
    super(MonitorInfo.build().name("Network").icon("network_check").yAxisLabel("Send / Resv [kB]").toInfo());
    initMonitor();
  }
  
  private void initMonitor()
  {
    Axis yAxis = model.getAxis(AxisType.Y);
    yAxis.setMin(0);
    
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
    
    NetworkIF[] networkIFs = hardware.getNetworkIFs();
    totalSend = Arrays.asList(networkIFs).stream().mapToLong(n -> n.getBytesSent()).sum();
    totalResv = Arrays.asList(networkIFs).stream().mapToLong(n -> n.getBytesRecv()).sum();
  }
  
  @Override
  public String getInfo()
  {
    if (!isRunning())
    {
      return "Sending: -- kB / Receiving: -- kB";
    }
    calcNewValues();
    return String.format("Sending: %dkB / Reseiving: %dkB", actualSend / 1000, actualResv / 1000);
  }
  
  @Override
  protected void calcNewValues(long time)
  {
    NetworkIF[] networkIFs = hardware.getNetworkIFs();
    actualSend = Arrays.asList(networkIFs).stream().mapToLong(n -> n.getBytesSent()).sum() - totalSend;
    actualResv = Arrays.asList(networkIFs).stream().mapToLong(n -> n.getBytesRecv()).sum() - totalResv;
    sendData.put(time, actualSend / 1000);
    resvData.put(time, actualResv / 1000);
    totalSend += actualSend;
    totalResv += actualResv;
    cleanUpOldData(sendData);
    cleanUpOldData(resvData);
  }

}
