package ch.ivyteam.enginecockpit.monitor.mbeans;

import java.util.ArrayList;
import java.util.List;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;

import ch.ivyteam.enginecockpit.monitor.Monitor;

public class MTraceMonitor extends Monitor
{
  private final List<MTrace> traces = new ArrayList<>();
  
  public MTraceMonitor()
  {
    initMonitor();
  }
  
  private void initMonitor()
  {
    Axis xAxis = model.getAxis(AxisType.X);
    xAxis.setTickCount(11);
    xAxis.setLabel("Time [s]");
    model.setLegendPosition("ne");
  }
  
  @Override
  public String getInfo()
  {
    return "Traces";
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
    traces.forEach(trace -> trace.calcNewValue(actualSec));
    traces.forEach(trace -> cleanUpOldData(trace.getData()));
  }

  public void addTrace(MTrace jmxTrace)
  {    
    traces.add(jmxTrace);
    model.addSeries(jmxTrace.getSeries());
    if (traces.size() == 1)
    {
      startMonitor();
    }
  }

  public void removeTrace(MTrace trace)
  {
    traces.remove(trace);
    model.getSeries().remove(trace.getSeries());
  }    

  public List<MTrace> getTraces()
  {
    return traces;
  }
  
  @Override
  public boolean isRunning()
  {
    return !traces.isEmpty();
  }
}
