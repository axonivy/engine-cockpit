package ch.ivyteam.enginecockpit.monitor.monitor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import software.xdev.chartjs.model.dataset.LineDataset;

import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.monitor.value.Value;
import ch.ivyteam.enginecockpit.monitor.value.ValueProvider;

public class Series {
  private final ValueProvider valueProvider;
  private final LineDataset dataSet;
  private final List<Value> data = new ArrayList<>();
  private String fillColor = "";

  protected Series(Builder builder) {
    this.valueProvider = builder.valueProvider;
    dataSet = new LineDataset();
    dataSet.setTension(builder.smoothLine ? 0.2 : 0);
    dataSet.setFill(builder.fill);
    dataSet.setPointRadius(List.of(0));
    dataSet.setLabel(builder.name);
    dataSet.setBorderWidth(1);
    updateColor();
  }

  public LineDataset getSeries() {
    return dataSet;
  }

  public void calcNewValue() {
    data.add(nextValue());
  }

  public Optional<Value> maxValue() {
    return data.stream().max(Comparator.naturalOrder());
  }

  protected Value nextValue() {
    return valueProvider.nextValue();
  }

  public List<Value> getData() {
    return data;
  }

  public void setFillColor(String color) {
    this.fillColor = color;
    updateColor();
  }

  private void updateColor() {
    dataSet.setBackgroundColor(fillColor);
    dataSet.setBorderColor(fillColor);
  }

  public void scale(Unit scaleToUnit) {
    List<Number> scaledNumbers = new ArrayList<>(data.size());
    data.forEach(datapoint -> {
      scaledNumbers.add(scaleTo(datapoint, scaleToUnit));
    });
    dataSet.setData(scaledNumbers);
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
