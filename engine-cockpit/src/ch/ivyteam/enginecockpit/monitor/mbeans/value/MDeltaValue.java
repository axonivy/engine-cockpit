package ch.ivyteam.enginecockpit.monitor.mbeans.value;

final class MDeltaValue implements MValueProvider
{
  private final MValueProvider original;
  private Number lastValue;

  MDeltaValue(MValueProvider original)
  {
    this.original = original;
  }

  @Override
  public Object nextValue()
  {
    Number value = (Number)original.nextValue();
    if (lastValue == null)
    {
      lastValue = value;
      return 0L;
    }
    Long result = value.longValue() - lastValue.longValue(); 
    lastValue = value;
    return result;
  }
}
