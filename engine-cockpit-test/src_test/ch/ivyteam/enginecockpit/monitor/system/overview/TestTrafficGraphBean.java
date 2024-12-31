package ch.ivyteam.enginecockpit.monitor.system.overview;

import static ch.ivyteam.ivy.trace.Attribute.attribute;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.primefaces.model.diagram.Connection;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.overlay.LabelOverlay;

import ch.ivyteam.enginecockpit.monitor.trace.TraceBean;
import ch.ivyteam.enginecockpit.monitor.trace.TracerAccess;
import ch.ivyteam.enginecockpit.monitor.trace.TstSpan;
import ch.ivyteam.ivy.trace.Span;
import ch.ivyteam.ivy.trace.SpanResult;

class TestTrafficGraphBean {
  private final TrafficGraphBean bean = new TrafficGraphBean();
  private final TraceBean trace = new TraceBean();

  @RegisterExtension
  TracerAccess tracer = new TracerAccess();

  @BeforeEach
  void beforeEach() {
    trace.stop();
    bean.clear();
  }

  @Test
  void trace_undef() {
    trace.start();
    assertThat(bean.getModel().getConnections()).isEmpty();
    assertThat(bean.getModel().getElements()).hasSize(1);
    try (var span = Span.open(() -> new TstSpan("HTTP GET", List.of(attribute("url", "http://localhost:8080/"))))) {}
    bean.refresh();
    assertConnections(1, 0);

    assertThat(bean.getModel().getElements()).hasSize(2);

    var ivy = bean.getModel().getElements().get(0);
    assertSystem(ivy, "Axon Ivy Engine", null);

    var http = bean.getModel().getElements().get(1);
    assertSystem(http, "HTTP GET", "ok");
  }

  @Test
  void trace_ok() {
    trace.start();
    assertThat(bean.getModel().getConnections()).isEmpty();
    assertThat(bean.getModel().getElements()).hasSize(1);
    try (var span = Span.open(() -> new TstSpan("HTTP GET", List.of(attribute("url", "http://localhost:8080/"))))) {
      span.result(SpanResult.ok(attribute("http.statusCode", 200)));
    }
    bean.refresh();

    assertConnections(1, 0);
    assertThat(bean.getModel().getElements()).hasSize(2);
    var ivy = bean.getModel().getElements().get(0);
    assertSystem(ivy, "Axon Ivy Engine", null);

    var http = bean.getModel().getElements().get(1);
    assertSystem(http, "HTTP GET", "ok");

  }

  @Test
  void trace_error() {
    trace.start();
    assertThat(bean.getModel().getConnections()).isEmpty();
    assertThat(bean.getModel().getElements()).hasSize(1);
    try (var span = Span.open(() -> new TstSpan("HTTP GET", List.of(attribute("url", "http://localhost:8080/"))))) {
      span.error(new Exception("error"));
    }
    bean.refresh();
    assertConnections(1, 1);

    assertThat(bean.getModel().getElements()).hasSize(2);

    var ivy = bean.getModel().getElements().get(0);
    assertSystem(ivy, "Axon Ivy Engine", null);

    var http = bean.getModel().getElements().get(1);
    assertSystem(http, "HTTP GET", "error");
  }

  @Test
  void refresh() {
    trace.start();
    assertThat(bean.getModel().getConnections()).isEmpty();
    assertThat(bean.getModel().getElements()).hasSize(1);
    try (var span = Span.open(() -> new TstSpan("HTTP GET", List.of(attribute("url", "http://localhost:8080/"))))) {}

    assertThat(bean.getModel().getConnections()).isEmpty();
    assertThat(bean.getModel().getElements()).hasSize(1);

    bean.refresh();

    assertThat(bean.getModel().getConnections()).hasSize(1);
    assertThat(bean.getModel().getElements()).hasSize(2);
  }

  @Test
  void clear() {
    trace.start();
    try (var span = Span.open(() -> new TstSpan("HTTP GET", List.of(attribute("url", "http://localhost:8080/"))))) {}
    bean.refresh();

    assertThat(bean.getModel().getConnections()).hasSize(1);
    assertThat(bean.getModel().getElements()).hasSize(2);

    bean.clear();

    assertThat(bean.getModel().getConnections()).isEmpty();
    assertThat(bean.getModel().getElements()).hasSize(1);
  }

  private LabelOverlay toLabel(Connection con) {
    assertThat(con.getOverlays()).hasSize(2);
    return con
        .getOverlays()
        .stream()
        .filter(LabelOverlay.class::isInstance)
        .map(LabelOverlay.class::cast)
        .findAny()
        .orElseThrow(() -> new AssertionError("Label of connection is missing"));
  }

  private void assertConnections(int requests, int errors) {
    assertThat(bean.getModel().getConnections()).hasSize(1);
    var con = bean.getModel().getConnections().get(0);
    assertThat(con.getConnector().getPaintStyle()).startsWith("{stroke:'hsl(").endsWith(", 100%, 70%)', strokeWidth:20}");
    var label = toLabel(con);
    assertThat(label.getLabel()).startsWith(requests + " requests / " + errors + " errors / ");
  }

  private void assertSystem(Element ivy, String name, String styleClass) {
    assertThat(ivy.getData()).isEqualTo(new TrafficGraphBean.System(name));
    assertThat(ivy.getStyleClass()).isEqualTo(styleClass);
  }

}
