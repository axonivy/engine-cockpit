package ch.ivyteam.enginecockpit.monitor.mbeans.value;

class MPercentageValue implements MValueProvider
{
  private final MValueProvider original;

  MPercentageValue(MValueProvider original)
  {
    this.original = original;
  }

  @Override
  public Object nextValue()
  {
    var value = (Number)original.nextValue();
    if (isFloating(value))
    {
      return value.doubleValue() * 100.0d;
    }
    else 
    {
      return value.longValue() * 100L;
    }
  }

  private boolean isFloating(Number value)
  {
    return value instanceof Float || 
           value instanceof Double;
  }
}
