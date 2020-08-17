package ch.ivyteam.enginecockpit.monitor.mbeans.value;

class MScaleValue implements MValueProvider
{
  private final MValueProvider original;
  private final long factor;

  MScaleValue(MValueProvider original, long factor)
  {
    this.original = original;
    this.factor = factor;
  }

  @Override
  public Object nextValue()
  {
    var value = (Number)original.nextValue();
    return value.longValue() / factor;
  }
}
