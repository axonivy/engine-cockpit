package ch.ivyteam.enginecockpit.monitor.trace;

import static ch.ivyteam.ivy.trace.Attribute.attribute;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import ch.ivyteam.ivy.trace.Attribute;
import ch.ivyteam.ivy.trace.Tracer;

class TestSpanBean {
  private final SpanBean bean = new SpanBean();
  private String traceId;

  @RegisterExtension
  TracerAccess tracer = new TracerAccess();

  @BeforeEach
  void beforeEach() {
    try (var undef = ch.ivyteam.ivy.trace.Span.open(() -> new TstSpan("undef", List.of(attribute("attr", 1234), attribute("hello", "world"))))) {
      try (var ok = ch.ivyteam.ivy.trace.Span.open(() -> new TstSpan("ok"))) {
        try (var error = ch.ivyteam.ivy.trace.Span.open(() -> new TstSpan("error"))) {
          error.error(new Throwable());
        }
        ok.result(null);
      }
    }
    traceId = Tracer.instance().slowTraces().all().get(0).id();
    bean.setTraceId(traceId);
    bean.onload();
  }

  @Test
  void getTraceId() {
    assertThat(bean.getTraceId()).isEqualTo(traceId);
  }

  @Test
  void getName() {
    var span = getSpan(1);
    assertThat(span.getName()).isEqualTo("undef");
    span = getSpan(2);
    assertThat(span.getName()).isEqualTo("ok");
    span = getSpan(3);
    assertThat(span.getName()).isEqualTo("error");
  }

  @Test
  void depth() {
    var span = getSpan(1);
    assertThat(span.depth()).isEqualTo(0);
    span = getSpan(2);
    assertThat(span.depth()).isEqualTo(1);
    span = getSpan(3);
    assertThat(span.depth()).isEqualTo(2);
  }

  @Test
  void getAttributeInfo() {
    var span = getSpan(1);
    assertThat(span.getAttributesInfo()).contains("attr=1234").contains("\n").contains("hello=world");
    span = getSpan(2);
    assertThat(span.getAttributesInfo()).isEmpty();
    span = getSpan(3);
    assertThat(span.getAttributesInfo()).contains("error.message=null").contains("\n").contains("error.class=java.lang.Throwable");
  }

  @Test
  void getSpanAttributes() {
    var span = getSpan(1);
    assertThat(span.getAttributes()).contains("attr=1234").contains(",").contains("hello=world");
    span = getSpan(2);
    assertThat(span.getAttributes()).isEmpty();
    span = getSpan(3);
    assertThat(span.getAttributes()).contains("error.message=null").contains(",").contains("error.class=java.lang.Throwable");
  }

  @Test
  void getAttributes() {
    assertThat(bean.getAttributes()).containsExactlyInAnyOrder(
        new SpanAttribute(Attribute.attribute("attr", "1234")),
        new SpanAttribute(Attribute.attribute("hello", "world")));
  }

  @Test
  void getId() {
    assertSpans(span -> assertThat(span.getId()).isNotBlank());
  }

  @Test
  void getStart() {
    assertSpans(span -> assertThat(span.getStart()).isNotBlank());
  }

  @Test
  void getEnd() {
    assertSpans(span -> assertThat(span.getEnd()).isNotBlank());
  }

  @Test
  void getExecutionTimeBackground() {
    assertSpans(span -> assertThat(span.getExecutionTimeBackground()).startsWith("linear-gradient(90deg, hsl("));
  }

  @Test
  void getExecutionTime() {
    assertSpans(span -> assertThat(span.getExecutionTime()).isGreaterThan(0.0d));
  }

  @Test
  void exportName() {
    var span = getSpan(1);
    assertThat(bean.exportName(span)).isEqualTo("undef");
    span = getSpan(2);
    assertThat(bean.exportName(span)).isEqualTo("> ok");
    span = getSpan(3);
    assertThat(bean.exportName(span)).isEqualTo("> > error");
  }

  private void assertSpans(Consumer<Span> asserter) {
    for (int deep = 1; deep <= 3; deep++) {
      asserter.accept(getSpan(deep));
    }
  }

  private Span getSpan(int deep) {
    var node = bean.getTree();
    while (deep > 0) {
      node = node.getChildren().get(0);
      assertThat(node).isNotNull();
      deep--;
    }
    var span = node.getData();
    assertThat(span).isInstanceOf(Span.class);
    return span;
  }
}
