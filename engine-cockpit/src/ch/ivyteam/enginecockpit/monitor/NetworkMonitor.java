package ch.ivyteam.enginecockpit.monitor;

import java.util.Arrays;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.LineChartSeries;

public class NetworkMonitor extends Monitor
{
  private LineChartSeries networkSend;
  private LineChartSeries networkResv;
  
  private long totalSend;
  private long totalResv;
  private long actualSend;
  private long actualResv;
  
  public NetworkMonitor()
  {
    super();
  }
  
  @Override
  protected void initMonitor()
  {
    super.initMonitor();
    Axis yAxis = model.getAxis(AxisType.Y);
    yAxis.setMin(0);
    yAxis.setLabel("Send / Resv [kB]");
    Axis xAxis = model.getAxis(AxisType.X);
    xAxis.setTickCount(11);
    xAxis.setLabel("Time [s]");
    networkSend = new LineChartSeries();
    networkSend.setSmoothLine(true);
    networkSend.setLabel("Send");
    networkResv = new LineChartSeries();
    networkResv.setSmoothLine(true);
    networkResv.setLabel("Resv");
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
    networkSend.set(actualSec, actualSend / 1000);
    networkResv.set(actualSec, actualResv / 1000);
    totalSend += actualSend;
    totalResv += actualResv;
  }

}
