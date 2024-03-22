package ch.ivyteam.enginecockpit.monitor.monitor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.line.LineChartDataSet;

import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.monitor.value.Value;
import ch.ivyteam.enginecockpit.monitor.value.ValueProvider;

public class Series {
  private final ValueProvider valueProvider;
  private final LineChartDataSet dataSet;
  private final ChartData chartData;
  private final Map<Object, Value> data = new LinkedHashMap<>();

  protected Series(Builder builder) {
    this.valueProvider = builder.valueProvider;
    dataSet = new LineChartDataSet();
    chartData = new ChartData();
    
    
    dataSet.setTension(builder.smoothLine ? 0.1 : 0);
    dataSet.setFill(builder.fill);
    dataSet.setLabel(builder.name);    
  }

  public ChartData getSeries() {
    return chartData;
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
    List<Object> scaledNumbers = new ArrayList<Object>(data.size());
    List<String> keys = new ArrayList<>(data.size());
    data.entrySet().forEach(entry -> {
    	scaledNumbers.add(scaleTo(entry.getValue(), scaleToUnit));
    	ZonedDateTime time = ZonedDateTime.ofInstant(Instant.ofEpochMilli((long)entry.getKey()), ZoneId.systemDefault());
    	keys.add(time.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    });
    dataSet.setData(scaledNumbers);
    chartData.setLabels(keys);
    chartData.addChartDataSet(dataSet);
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
