package ch.ivyteam.enginecockpit.monitor.trace;

import java.util.List;

import ch.ivyteam.ivy.trace.Attribute;
import ch.ivyteam.ivy.trace.SpanInstance;
import ch.ivyteam.ivy.trace.SpanResult;

final class TstSpan implements SpanInstance<SpanResult> {

  private final String name;
  private final List<Attribute> attributes;

  public TstSpan(String name, List<Attribute> attributes) {
    this.name = name;
    this.attributes = attributes;
  }

  public TstSpan(String name) {
    this(name, List.of());
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public List<Attribute> attributes() {
    return attributes;
  }

  @Override
  public SpanResult result(SpanResult result) {
    if (result == null) {
      return SpanResult.ok();
    }
    return result;
  }
}
