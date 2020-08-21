package ch.ivyteam.enginecockpit.monitor.value;

class DifferenceValue implements ValueProvider
{
  private ValueProvider minuend;
  private ValueProvider subtrahend;

  public DifferenceValue(ValueProvider minuend, ValueProvider subtrahend)
  {
    this.minuend = minuend;
    this.subtrahend = subtrahend;
  }

  @Override
  public Value nextValue()
  {
    Value min = minuend.nextValue();
    return new Value(min.longValue() - subtrahend.nextValue().longValue(), min.unit());
  }
}
