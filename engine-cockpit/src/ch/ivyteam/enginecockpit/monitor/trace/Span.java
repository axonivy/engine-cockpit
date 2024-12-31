package ch.ivyteam.enginecockpit.monitor.trace;

import java.util.List;
import java.util.stream.Collectors;

import ch.ivyteam.ivy.trace.TraceSpan;

public final class Span {

  private final TraceSpan span;
  private final long rootExecutionTime;
  private final int depth;
  private final List<SpanAttribute> attributes;

  Span(TraceSpan span, long rootExecutionTime, int depth) {
    this.span = span;
    this.rootExecutionTime = rootExecutionTime;
    this.depth = depth;
    this.attributes = span
        .attributes()
        .stream()
        .map(SpanAttribute::new)
        .collect(Collectors.toList());
  }

  public String getId() {
    return span.id();
  }

  public String getName() {
    return span.name();
  }

  public String getStart() {
    return TraceBean.toLocalTime(span.times().start());
  }

  public String getEnd() {
    return TraceBean.toLocalTime(span.times().end());
  }

  public double getExecutionTime() {
    return TraceBean.toMillis(span.times().executionTime());
  }

  public String getExecutionTimeBackground() {
    return BackgroundMeterUtil.background(span.times().executionTime().toNanos(), rootExecutionTime);
  }

  public String getStatusClass() {
    return getStatusClass(span);
  }

  public String getStatusTooltip() {
    return getStatusTooltip(span);
  }

  public int depth() {
    return depth;
  }

  List<SpanAttribute> attributes() {
    return attributes;
  }

  public String getAttributes() {
    return attributes(", ");
  }

  public String getAttributesInfo() {
    return attributes("\n");
  }

  private String attributes(String delimiter) {
    return attributes(span, delimiter);
  }

  static String attributes(TraceSpan span, String delimiter) {
    return span
        .attributes()
        .stream()
        .map(attr -> attr.name() + "=" + attr.value())
        .collect(Collectors.joining(delimiter));
  }

  static String getStatusClass(TraceSpan span) {
    return switch (span.status()) {
      case ERROR -> "error";
      case OK -> "success";
      case UNSET -> "";
      default -> "";
    };
  }

  static String getStatusTooltip(TraceSpan span) {
    return switch (span.status()) {
      case ERROR -> "Error status. See attributes for error details.";
      case OK -> "OK status. There might be some result attributes available.";
      case UNSET -> "Status not set. No result attributes available.";
      default -> "Status not set. No result attributes available.";
    };
  }
}
