package ch.ivyteam.enginecockpit.monitor.mbeans;

import java.util.LinkedHashMap;
import java.util.Map;

import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartSeries;

import ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider;

public class MSeries
{
  private final MValueProvider valueProvider;
  private final LineChartSeries series;
  private final Map<Object, Number> data = new LinkedHashMap<>();

  public MSeries(MValueProvider valueProvider, String label)
  {
    this.valueProvider = valueProvider;
    series = new LineChartSeries();
    series.setSmoothLine(false);
    series.setShowMarker(false);
    series.setData(data);    
    series.setLabel(label);    
  }
  
  public ChartSeries getSeries()
  {
    return series;
  }

  public void calcNewValue(long actualSec)
  {
    data.put(actualSec, nextValue());
  }
  
  protected Number nextValue()
  {
    return (Number) valueProvider.nextValue();
  }

  public Map<Object, Number> getData()
  {
    return data;
  }
}
