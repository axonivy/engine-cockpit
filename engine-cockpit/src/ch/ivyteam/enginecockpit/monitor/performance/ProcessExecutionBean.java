package ch.ivyteam.enginecockpit.monitor.performance;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;

import org.ocpsoft.prettytime.PrettyTime;
import org.primefaces.util.ComponentUtils;

import ch.ivyteam.di.restricted.DiCore;
import ch.ivyteam.ivy.bpm.engine.restricted.IBpmEngineManager;
import ch.ivyteam.ivy.bpm.engine.restricted.statistic.IExecutionStatistic;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@ManagedBean
@ViewScoped
@SuppressWarnings("restriction")
public final class ProcessExecutionBean {

  private final IExecutionStatistic statistic = DiCore.getGlobalInjector().getInstance(IBpmEngineManager.class).getExecutionStatistic();
  private String filter;
  private List<ProcessElementStatistic> filtered;
  private List<ProcessElementStatistic> elements = readData();

  private List<ProcessElementStatistic> readData() {
    var builder = new MaxBuilder();
    var stats = statistic.getProcessElementExecutionStatistic();
    Stream.of(stats).forEach(builder::add);
    var max = builder.toMax();
    return Stream
        .of(stats)
        .map(stat -> new ProcessElementStatistic(stat, max))
        .collect(Collectors.toList());
  }

  public List<ProcessElementStatistic> getProcessElements() {
    return elements;
  }

  public List<ProcessElementStatistic> getFilteredProcessElements() {
    return filtered;
  }

  public void setFilteredProcessElements(List<ProcessElementStatistic> filtered) {
    this.filtered = filtered;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public String getFilter() {
    return filter;
  }

  public boolean isNotRefreshable() {
    return !isRunning();
  }

  public boolean isNotStartable() {
    return isRunning() || isLoggingActive();
  }

  public boolean isNotStoppable() {
    return !isRunning() || isLoggingActive();
  }

  public boolean isNotCleanable() {
    return elements.isEmpty() || isLoggingActive();
  }

  public void start() {
    statistic.start();
  }

  public void stop() {
    refresh();
    statistic.stop();
  }

  public void clear() {
    statistic.clear();
    elements = readData();
  }

  public void refresh() {
    elements = readData();
  }

  public boolean isLoggingActive() {
    return IConfiguration.instance().getOrDefault("ProcessEngine.FiringStatistic.Active", boolean.class);
  }

  public String getLoggingInterval() {
    var seconds = IConfiguration.instance().getOrDefault("ProcessEngine.FiringStatistic.Interval", long.class);
    if (seconds == 1) {
      return "1 second";
    }
    if (seconds < 60) {
      return seconds + " seconds";
    }
    Instant now = Instant.now();
    var then = now.plus(Duration.ofSeconds(seconds));
    var time = new PrettyTime(Date.from(now), Locale.US);
    var durations = time.calculatePreciseDuration(then);
    return time.formatDurationUnrounded(durations);
  }

  public String export(UIColumn column) {
    for (UIComponent child : column.getChildren()) {
      if (child instanceof ValueHolder) {
        return ComponentUtils.getValueToRender(FacesContext.getCurrentInstance(), child);
      }
    }
    return "No value";
  }

  private boolean isRunning() {
    return statistic.isRunning();
  }
}
