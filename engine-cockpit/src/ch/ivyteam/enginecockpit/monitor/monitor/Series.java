package ch.ivyteam.enginecockpit.monitor.monitor;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartSeries;

import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.monitor.value.Value;
import ch.ivyteam.enginecockpit.monitor.value.ValueProvider;

public class Series
{
  private final ValueProvider valueProvider;
  private final LineChartSeries series;
  private final Map<Object, Value> data = new LinkedHashMap<>();

  protected Series(Builder builder)
  {
    this.valueProvider = builder.valueProvider;
    series = new LineChartSeries();
    series.setSmoothLine(builder.smoothLine);
    series.setShowMarker(false);
    series.setFill(builder.fill);
    series.setLabel(builder.name);    
  }
  
  public ChartSeries getSeries()
  {
    return series;
  }

  public void calcNewValue(long actualSec)
  {
    data.put(actualSec, nextValue());
  }
  
  public Optional<Value> maxValue()
  {
    return data.values().stream().max(Comparator.naturalOrder());
  }
  
  protected Value nextValue()
  {
    return valueProvider.nextValue();
  }

  public Map<Object, ?> getData()
  {
    return data;
  }

  public void scale(Unit scaleToUnit)
  {
    Map<Object, Number> scaledData = new LinkedHashMap<>();
    data.entrySet().forEach(entry -> scaledData.put(entry.getKey(), scaleTo(entry.getValue(), scaleToUnit)));
    series.setData(scaledData);
  }

  private Number scaleTo(Value value, Unit scaleToUnit)
  {
    if (value == Value.NO_VALUE || scaleToUnit == null)
    {
      return value.doubleValue();
    }
    return value.unit().convertTo(value.doubleValue(), scaleToUnit);
  }
  
  public static Builder build(ValueProvider valueProvider, String name)
  {
    return new Builder(valueProvider, name);    
  }
  
  public static final class Builder
  {
    private final ValueProvider valueProvider;
    private final String name;
    private boolean fill;
    private boolean smoothLine;

    public Builder(ValueProvider valueProvider, String name)
    {
      this.valueProvider = valueProvider;
      this.name = name;
    }
    
    public Builder fill()
    {
      fill = true;
      return this;
    }
    
    public Builder smoothLine()
    {
      smoothLine = true;
      return this;
    }

    public Series toSeries()
    {
      return new Series(this);
    }
  }
}
