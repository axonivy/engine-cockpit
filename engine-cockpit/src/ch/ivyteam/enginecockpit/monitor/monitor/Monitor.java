package ch.ivyteam.enginecockpit.monitor.monitor;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.ChartModel;
import org.primefaces.model.charts.axes.cartesian.CartesianScaleTitle;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.optionconfig.animation.Animation;
import org.primefaces.model.charts.optionconfig.legend.Legend;

import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.monitor.value.Value;
import ch.ivyteam.enginecockpit.monitor.value.ValueProvider;

public class Monitor {
  private long lastTimestamp;
  protected final LineChartModel model;
  protected final ChartData chartData;
  protected final LineChartOptions options;
  protected CartesianScales scales;
  protected CartesianLinearAxes xAxis;
  protected CartesianLinearAxes yAxis;
  protected CartesianLinearTicks xTicks;
  protected CartesianLinearTicks yTicks;
  protected CartesianScaleTitle xTitle;
  protected CartesianScaleTitle yTitle;
  private static final Duration MAX_DURATION = Duration.ofMinutes(10);
  private static final long MAX_DATA = MAX_DURATION.toSeconds();
  private final MonitorInfo info;

  private final List<Series> series = new ArrayList<>();
  private final List<ValueProvider> infoValues = new ArrayList<>();
  private final List<String> labels = new ArrayList<>();
  private final String[] fillColors = {"#607D8B", "#FFC107", "#FF5722"};

  protected Monitor(MonitorInfo info) {
    this.info = info;
    model = new LineChartModel();
    options = new LineChartOptions();
    chartData = new ChartData();
    scales = new CartesianScales();
    xAxis = new CartesianLinearAxes();
    yAxis = new CartesianLinearAxes();
    xTicks = new CartesianLinearTicks();
    yTicks = new CartesianLinearTicks();
    xTitle = new CartesianScaleTitle();
    yTitle = new CartesianScaleTitle();

    xTicks.setMaxTicksLimit(6);
    xTicks.setAutoSkip(true);
    xTicks.setAutoSkipPadding(0);
    xTicks.setMaxRotation(0);

    xAxis.setTicks(xTicks);
    yAxis.setTicks(yTicks);

    xTitle.setDisplay(true);
    xTitle.setText("Time");
    xTitle.setFontSize(14);
    xAxis.setScaleTitle(xTitle);

    yTitle.setDisplay(true);
    yTitle.setFontSize(14);
    yAxis.setScaleTitle(yTitle);

    scales.addXAxesData(xAxis);
    scales.addYAxesData(yAxis);

    Animation animation = new Animation();
    animation.setDuration(0);

    Legend legend = new Legend();
    legend.setAlign("end");

    options.setShowLines(true);
    options.setScales(scales);
    options.setScales(scales);
    options.setAnimation(animation);
    options.setLegend(legend);
    options.setTooltip(null);

    chartData.setLabels(labels);

    model.setOptions(options);
    model.setData(chartData);
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
          .map(Value::value)
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
    for (int i = 0; i < series.size(); i++) {
      series.get(i).setFillColor(getColor(i));
    }
    chartData.addChartDataSet(mSeries.getSeries());
  }

  private String getColor(int i) {
    i = i % fillColors.length;
    if (info.reverseColors) {
      return fillColors[fillColors.length - (i + 1)];
    } else {
      return fillColors[i];
    }
  }

  public void removeSeries(Series mSeries) {
    series.remove(mSeries);
    model.getData().getDataSet().clear();
    series.forEach(s -> model.getData().addChartDataSet(s.getSeries()));
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
    series.forEach(Series::calcNewValue);
    ZonedDateTime stamp = ZonedDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
    labels.add(stamp.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
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
      yTicks.setMaxTicksLimit(6);
    }
  }

  private void setYAxisUnit(Unit unit) {
    String label = StringUtils.defaultString(info.yAxisLabel, info.name);
    if (unit != null && unit.hasSymbol()) {
      label += " " + unit.symbolWithBracesOrEmpty();
    }
    yTitle.setText(label);
  }

  private void cleanUpOldData(List<Value> data) {
    if (data.size() > MAX_DATA) {
      data.remove(0);
    }
    if (labels.size() > MAX_DATA) {
      labels.remove(0);
    }
  }

  public static Builder build() {
    return new Builder();
  }

  public static final class Builder {
    private final MonitorInfo.Builder builder = MonitorInfo.build();

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

    public Builder reverseColors() {
      builder.setReverseColors();
      return this;
    }

    public Monitor toMonitor() {
      return new Monitor(builder.toInfo());
    }
  }
}
