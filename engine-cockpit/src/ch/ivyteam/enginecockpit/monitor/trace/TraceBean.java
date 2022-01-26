package ch.ivyteam.enginecockpit.monitor.trace;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;

import org.primefaces.util.ComponentUtils;

import ch.ivyteam.enginecockpit.util.DateUtil;
import ch.ivyteam.ivy.trace.Trace;
import ch.ivyteam.ivy.trace.Tracer;

@ManagedBean
@ViewScoped
public class TraceBean {

  private static final double ONE_MILLION = 1_000_000d;
  private Tracer tracer = Tracer.instance();
  private List<Trc> traces = readData();
  private List<Trc> filteredTraces;
  private String filter;

  private List<Trc> readData() {
    var builder = new MaxBuilder();
    var slowTraces = tracer.slowTraces();
    slowTraces.forEach(builder::add);
    var max = builder.toMax();
    return slowTraces
        .stream()
        .map(trace -> new Trc(trace, max))
        .collect(Collectors.toList());
  }

  public List<Trc> getSlowTraces() {
    return traces;
  }

  public List<Trc> getFilteredTraces() {
    return filteredTraces;
  }

  public void setFilteredTraces(List<Trc> filteredCaches) {
    this.filteredTraces = filteredCaches;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public boolean isNotRefreshable() {
    return !isRunning();
  }

  public boolean isNotStartable() {
    return isRunning();
  }

  public boolean isNotStoppable() {
    return !isRunning() || ! tracer.canBeStopped();
  }

  private boolean isRunning() {
    return tracer.isRunning();
  }

  public boolean isNotCleanable() {
    return traces.isEmpty();
  }

  public void start() {
    tracer.start();
  }

  public void stop() {
    refresh();
    tracer.stop();
  }

  public void clear() {
    tracer.clear();
    traces = readData();
  }

  public void refresh() {
    traces = readData();
  }

  static String toLocalTime(Instant instant) {
    return DateUtil.formatInstant(instant, "HH:mm:ss.SSS");
  }

  static double toMillis(Duration duration) {
    return duration.toNanos() / ONE_MILLION;
  }

  public String export(UIColumn column) {
    for (UIComponent child : column.getChildren()) {
      if (child instanceof ValueHolder) {
        return ComponentUtils.getValueToRender(FacesContext.getCurrentInstance(), child);
      }
    }
    return "No value";
  }

  private static final class MaxBuilder {
    private long max = Integer.MIN_VALUE;

    private void add(Trace trace) {
      max = Math.max(max, trace.rootSpan().times().executionTime().toNanos());
    }

    public long toMax() {
      return max;
    }
  }

  public static final class Trc {
    private final Trace trace;
    private final long max;

    private Trc(Trace trace, long max) {
      this.trace = trace;
      this.max = max;
    }

    public String getId() {
      return trace.id();
    }

    public String getName() {
      return trace.rootSpan().name();
    }

    public String getInfo() {
      return SpanBean.attributes(trace.rootSpan(), "\n");
    }

    public String getStart() {
      return toLocalTime(trace.rootSpan().times().start());
    }

    public String getEnd() {
      return toLocalTime(trace.rootSpan().times().end());
    }

    public double getExecutionTime() {
      return toMillis(trace.rootSpan().times().executionTime());
    }

    public String getExecutionTimeBackground() {
      return BackgroundMeterUtil.background(trace.rootSpan().times().executionTime().toNanos(), max);
    }

    public String getStatusClass() {
      return SpanBean.getStatucClass(trace.rootSpan().status());
    }
  }
}
