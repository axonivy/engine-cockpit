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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import software.xdev.chartjs.model.charts.LineChart;
import software.xdev.chartjs.model.data.LineData;
import software.xdev.chartjs.model.dataset.LineDataset;
import software.xdev.chartjs.model.options.Font;
import software.xdev.chartjs.model.options.LegendOptions;
import software.xdev.chartjs.model.options.LineOptions;
import software.xdev.chartjs.model.options.Plugins;
import software.xdev.chartjs.model.options.animation.DefaultAnimation;
import software.xdev.chartjs.model.options.scale.Scales;
import software.xdev.chartjs.model.options.scale.Scales.ScaleAxis;
import software.xdev.chartjs.model.options.scale.cartesian.AbstractCartesianScaleOptions.Title;
import software.xdev.chartjs.model.options.scale.cartesian.category.CategoryScaleOptions;
import software.xdev.chartjs.model.options.scale.cartesian.category.CategoryTickOptions;
import software.xdev.chartjs.model.options.scale.cartesian.linear.LinearScaleOptions;
import software.xdev.chartjs.model.options.scale.cartesian.linear.LinearTickOptions;
import software.xdev.chartjs.model.options.tooltip.TooltipOptions;

import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.monitor.value.Value;
import ch.ivyteam.enginecockpit.monitor.value.ValueProvider;
import ch.ivyteam.ivy.environment.Ivy;

public class Monitor {
  private long lastTimestamp;
  protected final LineChart model;
  protected final LineData chartData;
  protected final LineOptions options;
  protected final CategoryScaleOptions xAxis;
  protected final LinearScaleOptions yAxis;
  protected final CategoryTickOptions xTicks;
  protected final LinearTickOptions yTicks;
  protected final Title xTitle;
  protected final Title yTitle;
  private static final Duration MAX_DURATION = Duration.ofMinutes(10);
  private static final long MAX_DATA = MAX_DURATION.toSeconds();
  private final MonitorInfo info;

  private final List<Series> series = new ArrayList<>();
  private final List<ValueProvider> infoValues = new ArrayList<>();
  private final List<String> labels = new ArrayList<>();
  private final String[] fillColors = {"#607D8B", "#FFC107", "#FF5722"};

  protected Monitor(MonitorInfo info) {
    this.info = info;
    model = new LineChart();
    options = new LineOptions();
    chartData = new LineData();
    xAxis = new CategoryScaleOptions();
    yAxis = new LinearScaleOptions();
    xTicks = new CategoryTickOptions();
    yTicks = new LinearTickOptions();
    xTitle = new Title();
    yTitle = new Title();

    xTicks.setMaxTicksLimit(6);
    xTicks.setAutoSkip(true);
    xTicks.setAutoSkipPadding(0);
    xTicks.setMaxRotation(0);

    xAxis.setTicks(xTicks);
    yAxis.setTicks(yTicks);

    xTitle.setDisplay(true);
    xTitle.setText(Ivy.cm().co("/common/Time"));
    xTitle.setFont(new Font().setSize(14));
    xAxis.setTitle(xTitle);

    yTitle.setDisplay(true);
    yTitle.setFont(new Font().setSize(14));
    yAxis.setTitle(yTitle);

    Scales scales = new Scales();
    scales.addScale(ScaleAxis.X, xAxis);
    scales.addScale(ScaleAxis.Y, yAxis);

    options.setShowLine(true);
    options.setScales(scales);
    options.setAnimation(new DefaultAnimation().setDuration(0));
    options.setPlugins(new Plugins()
      .setLegend(new LegendOptions().setAlign("end"))
      .setTooltip(new TooltipOptions().setEnabled(false)));
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
    chartData.addDataset(mSeries.getSeries());
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
    chartData.setDatasets(series.stream().map(Series::getSeries).toList());
  }

  public void addInfoValue(ValueProvider valueProvider) {
    infoValues.add(valueProvider);
  }

  public List<Series> getSeries() {
    return series;
  }

  public String getModel() {
    calcNewValues();
    return model.toJson();
  }

  public List<LineDataset> getDataSets() {
    calcNewValues();
    return series.stream().map(Series::getSeries).toList();
  }

  private void calcNewValues() {
    long time = newTime();
    if (lastTimestamp != 0 && time / 1000 == lastTimestamp / 1000) {
      return;
    }
    lastTimestamp = time;
    calcNewValues(time);
  }

  private long newTime() {
    return Calendar.getInstance().getTimeInMillis();
  }

  private void calcNewValues(long time) {
    series.forEach(Series::calcNewValue);
    ZonedDateTime stamp = ZonedDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
    labels.add(stamp.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    series.forEach(serie -> cleanUpOldData(serie.getData()));
    chartData.setLabels(labels);
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
      yAxis.setMax(value * 4.0d);
      yTicks.setMaxTicksLimit(6);
    }
  }

  private void setYAxisUnit(Unit unit) {
    String label = Objects.toString(info.yAxisLabel, info.name);
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
