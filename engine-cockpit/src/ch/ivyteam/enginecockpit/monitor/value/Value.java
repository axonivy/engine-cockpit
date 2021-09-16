package ch.ivyteam.enginecockpit.monitor.value;

import ch.ivyteam.enginecockpit.monitor.unit.Unit;

public class Value implements Comparable<Value> {
  public static final Value NO_VALUE = new Value(0, Unit.ONE);
  private final Object value;
  private final Unit unit;

  Value(Object value, Unit unit) {
    this.value = value;
    this.unit = unit;
  }

  public Object value() {
    return value;
  }

  public Unit unit() {
    return unit;
  }

  public Number numberValue() {
    return (Number) value;
  }

  public long longValue() {
    return numberValue().longValue();
  }

  public double doubleValue() {
    return numberValue().doubleValue();
  }

  boolean isFloating() {
    return value instanceof Float ||
            value instanceof Double;
  }

  @Override
  public int compareTo(Value other) {
    if (isFloating() || other.isFloating()) {
      return Double.compare(doubleValue(), other.doubleValue());
    }
    return Long.compare(longValue(), other.longValue());
  }

  @Override
  public String toString() {
    if (this == NO_VALUE) {
      return "-";
    }
    StringBuilder builder = new StringBuilder();
    builder.append(value);
    if (unit.hasSymbol()) {
      builder.append(' ');
      builder.append(unit.symbolWithBracesOrEmpty());
    }
    return builder.toString();
  }
}
