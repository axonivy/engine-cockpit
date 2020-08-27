package ch.ivyteam.enginecockpit.monitor.value;

import ch.ivyteam.enginecockpit.monitor.unit.Unit;

class PercentageValue implements ValueProvider
{
  private final ValueProvider original;

  PercentageValue(ValueProvider original)
  {
    this.original = original;
  }

  @Override
  public Value nextValue()
  {
    var value = original.nextValue();
    if (value.isFloating())
    {
      return new Value(value.doubleValue() * 100.0d, Unit.PERCENTAGE);
    }
    else 
    {
      return new Value(value.longValue() * 100L, Unit.PERCENTAGE);
    }
  }
}
