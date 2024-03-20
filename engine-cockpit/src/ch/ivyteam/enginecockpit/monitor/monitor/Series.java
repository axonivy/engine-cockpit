package ch.ivyteam.enginecockpit.monitor.monitor;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.charts.ChartData;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.charts.ChartDataSet;
import org.primefaces.model.charts.line.LineChartDataSet;

import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.monitor.value.Value;
import ch.ivyteam.enginecockpit.monitor.value.ValueProvider;

public class Series {
  private final ValueProvider valueProvider;
  private final LineChartDataSet dataSet;
  private final Map<Object, Value> data = new LinkedHashMap<>();

  protected Series(Builder builder) {
    this.valueProvider = builder.valueProvider;
    dataSet = new LineChartDataSet();
    dataSet.setTension(3); // TODO: before -> .setSmoothLine(builder.smoothLine);
    dataSet.setFill(builder.fill);
    dataSet.setLabel(builder.name);
    // TODO series.setShowMarker(false);
    
  }

  public ChartDataSet getSeries() {
    return dataSet;
  }

  public void calcNewValue(long actualSec) {
    data.put(actualSec, nextValue());
  }

  public Optional<Value> maxValue() {
    return data.values().stream().max(Comparator.naturalOrder());
  }

  protected Value nextValue() {
    return valueProvider.nextValue();
  }

  public Map<Object, ?> getData() {
    return data;
  }

  public void scale(Unit scaleToUnit) {
    Map<Object, Number> scaledData = new LinkedHashMap<>();
    data.entrySet().forEach(entry -> scaledData.put(entry.getKey(), scaleTo(entry.getValue(), scaleToUnit)));
    dataSet.setData(sc);;
  }

  private Number scaleTo(Value value, Unit scaleToUnit) {
    if (value == Value.NO_VALUE || scaleToUnit == null) {
      return value.doubleValue();
    }
    return value.unit().convertTo(value.doubleValue(), scaleToUnit);
  }

  public static Builder build(ValueProvider valueProvider, String name) {
    return new Builder(valueProvider, name);
  }

  public static final class Builder {
    private final ValueProvider valueProvider;
    private final String name;
    private boolean fill;
    private boolean smoothLine;

    public Builder(ValueProvider valueProvider, String name) {
      this.valueProvider = valueProvider;
      this.name = name;
    }

    public Builder fill() {
      fill = true;
      return this;
    }

    public Builder smoothLine() {
      smoothLine = true;
      return this;
    }

    public Series toSeries() {
      return new Series(this);
    }
  }
}
