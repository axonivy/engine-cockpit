package ch.ivyteam.enginecockpit.monitor.value;

final class DeltaValue implements ValueProvider {
  private final ValueProvider original;
  private Value lastValue;

  DeltaValue(ValueProvider original) {
    this.original = original;
  }

  @Override
  public Value nextValue() {
    var value = original.nextValue();
    if (lastValue == null) {
      lastValue = value;
      return Value.NO_VALUE;
    }
    Long result = value.longValue() - lastValue.longValue();
    lastValue = value;
    return new Value(result, value.unit());
  }
}
