package ch.ivyteam.enginecockpit.monitor.mbeans;

import java.util.List;

public class MTraceMonitor extends MMonitor
{
  public MTraceMonitor()
  {
    super(MonitorInfo.build().name("Traces").icon("equalizer").toInfo());
  }

  public void addTrace(MTrace trace)
  {
    addSeries(trace);
  }

  public void removeTrace(MTrace trace)
  {
    removeSeries(trace);
  }    

  @SuppressWarnings("unchecked")
  public List<MTrace> getTraces()
  {
    return (List<MTrace>)(List<?>)getSeries();
  }
}
