package ch.ivyteam.enginecockpit.monitor.value;

import javax.management.openmbean.CompositeData;

import ch.ivyteam.enginecockpit.monitor.unit.Unit;

final class MCompositeValue implements ValueProvider
{
  private final ValueProvider original;
  private final String compositeName;
  private final Unit unit;

  MCompositeValue(ValueProvider original, String compositeName, Unit unit)
  {
    this.original = original;
    this.compositeName = compositeName;
    this.unit = unit;
  }

  @Override
  public Value nextValue()
  {
    var composite = (CompositeData)original.nextValue().value();
    return new Value(composite.get(compositeName), unit);
  }
}
