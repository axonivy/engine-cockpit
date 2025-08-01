package ch.ivyteam.enginecockpit.monitor.trace;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.enginecockpit.commons.TreeView;
import ch.ivyteam.ivy.trace.Trace;
import ch.ivyteam.ivy.trace.TraceSpan;
import ch.ivyteam.ivy.trace.Tracer;

@ManagedBean
@ViewScoped
public final class SpanBean extends TreeView<Span> {

  private static final ArrayList<SpanAttribute> SORTABLE_EMPTY_LIST = new ArrayList<>();
  private Optional<Trace> trace = Optional.empty();
  private String traceId;
  private Span selected;

  public void setTraceId(String traceId) {
    this.traceId = traceId;
  }

  public String getTraceId() {
    return traceId;
  }

  public void onload() {
    reloadTree();
  }

  public boolean isTraceAvailable() {
    return trace.isPresent();
  }

  @Override
  protected void buildTree() {
    trace = Tracer.instance().slowTraces().find(traceId);
    if (trace.isPresent()) {
      buildTreeNode(trace.get().rootSpan(), rootTreeNode, 0);
      selected = rootTreeNode.getChildren().get(0).getData();
    }
  }

  private void buildTreeNode(TraceSpan span, TreeNode<Span> parentNode, int depth) {
    var node = new DefaultTreeNode<>(
        new Span(span, trace.get().rootSpan().times().executionTime().toNanos(), depth),
        parentNode);
    int nextDepth = ++depth;
    span.children().forEach(child -> buildTreeNode(child, node, nextDepth));
  }

  public void nodeSelect(NodeSelectEvent event) {
    @SuppressWarnings("unchecked")
    TreeNode<Span> node = event.getTreeNode();
    selected = node.getData();
  }

  public List<SpanAttribute> getAttributes() {
    if (selected == null) {
      return SORTABLE_EMPTY_LIST;
    } else {
      return selected.attributes();
    }
  }

  @Override
  protected void filterNode(TreeNode<Span> node) {}

  public String exportName(Span span) {
    return "> ".repeat(span.depth()) + span.getName();
  }
}
