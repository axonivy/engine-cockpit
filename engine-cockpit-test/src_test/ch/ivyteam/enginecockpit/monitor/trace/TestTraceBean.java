package ch.ivyteam.enginecockpit.monitor.trace;

import static ch.ivyteam.ivy.trace.Attribute.attribute;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.ivyteam.ivy.trace.Span;
import ch.ivyteam.ivy.trace.SpanResult;

class TestTraceBean {
  private TraceBean bean = new TraceBean();

  @BeforeEach
  void beforeEach() {
    bean.stop();
    bean.clear();
  }

  @Test
  void isNotStartable() {
    assertThat(bean.isNotStartable()).isFalse();
    bean.start();
    assertThat(bean.isNotStartable()).isTrue();
    bean.stop();
    assertThat(bean.isNotStartable()).isFalse();
  }

  @Test
  void isNotStoppable() {
    assertThat(bean.isNotStoppable()).isTrue();
    bean.start();
    assertThat(bean.isNotStoppable()).isFalse();
    bean.stop();
    assertThat(bean.isNotStoppable()).isTrue();
  }

  @Test
  void isNotRefreshable() {
    assertThat(bean.isNotRefreshable()).isTrue();
    bean.start();
    assertThat(bean.isNotRefreshable()).isFalse();
    bean.stop();
    assertThat(bean.isNotRefreshable()).isTrue();
  }

  @Test
  void isNotCleanable() {
    assertThat(bean.isNotCleanable()).isTrue();
    bean.start();
    assertThat(bean.isNotCleanable()).isTrue();
    try (var span = Span.open(() -> new TstSpan("test"))) {
    }
    bean.refresh();
    assertThat(bean.isNotCleanable()).isFalse();
    bean.stop();
    assertThat(bean.isNotCleanable()).isFalse();
    bean.clear();
    assertThat(bean.isNotCleanable()).isTrue();
  }

  @Test
  void slowTraces() {
    bean.start();
    assertThat(bean.getSlowTraces()).isEmpty();
    try (var span = Span.open(() -> new TstSpan("test name"))) {
    }
    bean.refresh();
    assertThat(bean.getSlowTraces()).hasSize(1);
  }

  @Test
  void trace_undef() {
    bean.start();
    assertThat(bean.getSlowTraces()).isEmpty();
    try (var span = Span.open(() -> new TstSpan("test name", List.of(attribute("attr", 1234), attribute("hello", "world"))))) {
    }
    bean.refresh();
    assertThat(bean.getSlowTraces()).hasSize(1);
    var trc = bean.getSlowTraces().get(0);
    assertThat(trc.getId()).isNotBlank();
    assertThat(trc.getName()).isEqualTo("test name");
    assertThat(trc.getInfo()).contains("attr=1234").contains("\n").contains("hello=world");
    assertThat(trc.getStatusClass()).isEqualTo("si-time-clock-circle");
    assertThat(trc.getExecutionTime()).isGreaterThan(0.0d);
    assertThat(trc.getExecutionTimeBackground()).startsWith("linear-gradient(90deg, hsl(");
    assertThat(trc.getStart()).isNotBlank();
    assertThat(trc.getEnd()).isNotBlank();
  }

  @Test
  void trace_error() {
    bean.start();
    assertThat(bean.getSlowTraces()).isEmpty();
    try (var span = Span.open(() -> new TstSpan("with error"))) {
      span.error(new Throwable());
    }
    bean.refresh();
    assertThat(bean.getSlowTraces()).hasSize(1);
    var trc = bean.getSlowTraces().get(0);
    assertThat(trc.getName()).isEqualTo("with error");
    assertThat(trc.getStatusClass()).isEqualTo("si-alert-circle error");
  }

  @Test
  void trace_ok() {
    bean.start();
    assertThat(bean.getSlowTraces()).isEmpty();
    try (var span = Span.open(() -> new TstSpan("with ok"))) {
      span.result(SpanResult.ok(attribute("status", 200)));
    }
    bean.refresh();
    assertThat(bean.getSlowTraces()).hasSize(1);
    var trc = bean.getSlowTraces().get(0);
    assertThat(trc.getName()).isEqualTo("with ok");
    assertThat(trc.getInfo()).isEqualTo("status=200");
    assertThat(trc.getStatusClass()).isEqualTo("si-check-circle-1 success");
  }
}
