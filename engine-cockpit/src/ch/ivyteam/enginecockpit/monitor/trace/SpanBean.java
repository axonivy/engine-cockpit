package ch.ivyteam.enginecockpit.monitor.trace;

import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.enginecockpit.commons.TreeView;
import ch.ivyteam.ivy.trace.Trace;
import ch.ivyteam.ivy.trace.TraceSpan;
import ch.ivyteam.ivy.trace.TraceSpan.Status;
import ch.ivyteam.ivy.trace.Tracer;

@ManagedBean
@ViewScoped
public class SpanBean extends TreeView {
  private Trace trace;
  private String traceId;

  public void setTraceId(String traceId) {
    this.traceId = traceId;
    reloadTree();
  }

  public String getTraceId() {
    return traceId;
  }

  @Override
  protected void buildTree() {
    trace = Tracer.instance().slowTraces().stream().filter(trc -> trc.id().equals(traceId)).findAny().orElse(null);
    buildTreeNode(trace.rootSpan(), rootTreeNode);
  }

  private void buildTreeNode(TraceSpan span, TreeNode parentNode) {
    var node = new DefaultTreeNode(new Span(span), parentNode);
    span.children().forEach(child -> buildTreeNode(child, node));
  }

  @Override
  protected void filterNode(TreeNode node) {
  }

  public class Span {
    private TraceSpan span;

    private Span(TraceSpan span) {
      this.span = span;
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
      return BackgroundMeterUtil.background(span.times().executionTime().toNanos(), trace.rootSpan().times().executionTime().toNanos());
    }

    public String getStatusClass() {
      return getStatucClass(span.status());
    }

    public String getAttributes() {
      return attributes(", ");
    }

    public String getAttributesInfo() {
      return attributes("\n");
    }

    private String attributes(String delimiter) {
      return SpanBean.attributes(span, delimiter);
    }
  }

  static String attributes(TraceSpan span, String delimiter) {
    return span
        .attributes()
        .stream()
        .map(attr -> attr.name() + "=" + attr.value())
        .collect(Collectors.joining(delimiter));
  }

  static String getStatucClass(Status status) {
    switch(status) {
      case ERROR:
        return "si-alert-circle error";
      case OK:
        return "si-check-circle-1 success";
      case UNSET:
      default:
        return "si-time-clock-circle";
    }
  }
}
