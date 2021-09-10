package ch.ivyteam.enginecockpit.monitor.mbeans;

import java.util.List;
import java.util.Optional;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.monitor.MonitorInfo;
import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.monitor.value.Value;

public class MTraceMonitor extends Monitor {
  public MTraceMonitor() {
    super(MonitorInfo.build().name("Traces").icon("equalizer").toInfo());
  }

  public void addTrace(MTrace trace) {
    addSeries(trace);
  }

  public void removeTrace(MTrace trace) {
    removeSeries(trace);
  }

  @SuppressWarnings("unchecked")
  public List<MTrace> getTraces() {
    return (List<MTrace>) (List<?>) getSeries();
  }

  @Override
  protected Unit scaleUnit(Optional<Value> maxValue) {
    return null;
  }
}
