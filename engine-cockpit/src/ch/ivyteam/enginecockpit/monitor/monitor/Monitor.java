package ch.ivyteam.enginecockpit.monitor.monitor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.primefaces.model.charts.ChartModel;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;

import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.monitor.value.Value;
import ch.ivyteam.enginecockpit.monitor.value.ValueProvider;

public class Monitor {
  private long lastTimestamp;
  protected final LineChartModel model;
  protected final LineChartOptions options;
  protected CartesianScales scales;
  protected CartesianLinearAxes xAxis;
  protected CartesianLinearAxes yAxis;
  protected CartesianLinearTicks xTicks;
  protected CartesianLinearTicks yTicks;
  private static final Duration MAX_DURATION = Duration.ofMinutes(10);
  private static final long MAX_DATA = MAX_DURATION.toSeconds();
  private final MonitorInfo info;

  private final List<Series> series = new ArrayList<>();
  private final List<ValueProvider> infoValues = new ArrayList<>();

  protected Monitor(MonitorInfo info) {
	this.info = info;
    model = new LineChartModel();
    options = new LineChartOptions();
    
    scales = new CartesianScales();
    xAxis = new CartesianLinearAxes();
    yAxis = new CartesianLinearAxes();
    
    xTicks = new CartesianLinearTicks();
    yTicks = new CartesianLinearTicks();
    
    xAxis.setTicks(xTicks);
    yAxis.setTicks(yTicks);
    
    scales.addXAxesData(xAxis);
    scales.addYAxesData(yAxis);
    
    options.setShowLines(true);
    
    //options.setScales(scales);  
    model.setOptions(options);
  }

  public String getTitle() {
    return info.title;
  }

  public String getIcon() {
    return info.icon;
  }

  public String getName() {
    return info.name;
  }

  public String getInfo() {
    StringBuilder builder = new StringBuilder();
    builder.append(getName());
    if (!infoValues.isEmpty()) {
      builder.append(": ");
      builder.append(infoValues
              .stream()
              .map(ValueProvider::nextValue)
              .map(v -> v.value())
              .map(Object::toString)
              .collect(Collectors.joining(", ")));
    }
    return builder.toString();
  }

  public boolean isRunning() {
    return !series.isEmpty();
  }

  public void addSeries(Series mSeries) {
    series.add(mSeries);
    model.setData(mSeries.getSeries());
    //model.getData().getDataSet().add();
  }

  public void removeSeries(Series mSeries) { // TODO: remove existing series
    series.remove(mSeries);
    
    model.getData().getDataSet().removeAll(mSeries.getSeries().getDataSet());
    //model.remove(mSeries.getSeries());
  }

  public void addInfoValue(ValueProvider valueProvider) {
    infoValues.add(valueProvider);
  }

  public List<Series> getSeries() {
    return series;
  }

  public ChartModel getModel() {
    calcNewValues();
    return model;
  }

  private void calcNewValues() {
    long time = newTime();
    if (lastTimestamp != 0 && time / 1000 == lastTimestamp / 1000) {
      return;
    }
    lastTimestamp = time;
    setXAxis(lastTimestamp);
    calcNewValues(time);
  }

  private long newTime() {
    return Calendar.getInstance().getTimeInMillis();
  }

  private void setXAxis(long max) {
	xAxis.setMax(max);
  }

  private void calcNewValues(long time) {
    series.forEach(serie -> serie.calcNewValue(time));
    series.forEach(serie -> cleanUpOldData(serie.getData()));
    Optional<Value> maxValue = series
            .stream()
            .map(Series::maxValue)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .max(Comparator.naturalOrder());
    Unit scaleToUnit = scaleUnit(maxValue);
    setYAxisMaxValue(maxValue, scaleToUnit);
    setYAxisUnit(scaleToUnit);
    series.forEach(serie -> serie.scale(scaleToUnit));
  }

  protected Unit scaleUnit(Optional<Value> maxValue) {
    if (maxValue.isEmpty() || maxValue.get() == Value.NO_VALUE) {
      return null;
    }
    long origValue = maxValue.get().longValue();
    long scaleValue = origValue;
    Unit origUnit = maxValue.get().unit();
    Unit scaleUnit = origUnit;
    do {
      Unit unit = scaleUnit.scaleUp();
      if (unit == null) {
        return scaleUnit;
      }
      scaleValue = origUnit.convertTo(origValue, unit);
      if (scaleValue == 0) {
        return scaleUnit;
      }
      scaleUnit = unit;
    } while (true);
  }

  private void setYAxisMaxValue(Optional<Value> maxValue, Unit scaleToUnit) {
    if (maxValue.isPresent()) {
      var max = maxValue.get();
      var value = max.doubleValue();
      yAxis.setMin(0);
      if (scaleToUnit != null) {
        value = max.unit().convertTo(value, scaleToUnit);
      }
      value = Math.floor((value + 4.0d) / 4.0d * 1.1d);
      value = value * 4.0d;
      yAxis.setMax(value);
    }
  }

  private void setYAxisUnit(Unit unit) {
    String label = StringUtils.defaultString(info.yAxisLabel, info.name);
    String format = "%2$.3g";
    if (unit != null && unit.hasSymbol()) {
      label += " " + unit.symbolWithBracesOrEmpty();
      format += " " + unit.symbol();
    }
    //Axis yAxis = model.getAxis(AxisType.Y);
    //yAxis.setLabel(label);
    //yAxis.setTickFormat("%3g");
    //model.setDatatipFormat(format);
  }

  private void cleanUpOldData(Map<Object, ?> data) {
    if (data.size() > MAX_DATA) {
      data.remove(data.keySet().iterator().next());
    }
    var keys = data.keySet().iterator();
    if (keys.hasNext()) {
      xAxis.setMin((long)keys.next());
    }
  }

  public static Builder build() {
    return new Builder();
  }

  public static final class Builder {
    private MonitorInfo.Builder builder = MonitorInfo.build();

    public Builder title(String t) {
      builder.title(t);
      return this;
    }

    public Builder name(String nm) {
      builder.name(nm);
      return this;
    }

    public Builder icon(String icn) {
      builder.icon(icn);
      return this;
    }

    public Builder yAxisLabel(String label) {
      builder.yAxisLabel(label);
      return this;
    }

    public Monitor toMonitor() {
      return new Monitor(builder.toInfo());
    }
  }
}
