package ch.ivyteam.enginecockpit.monitor.trace;

import java.util.Objects;

import ch.ivyteam.ivy.trace.Attribute;

public final class SpanAttribute {

  private final Attribute attribute;

  SpanAttribute(Attribute attribute) {
    this.attribute = attribute;
  }

  public String getName() {
    return attribute.name();
  }

  public String getValue() {
    return attribute.value();
  }

  @Override
  public int hashCode() {
    return attribute.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    SpanAttribute other = (SpanAttribute) obj;
    return Objects.equals(attribute, other.attribute);
  }

  @Override
  public String toString() {
    return "SpanAttribute [attribute=" + attribute + "]";
  }
}
