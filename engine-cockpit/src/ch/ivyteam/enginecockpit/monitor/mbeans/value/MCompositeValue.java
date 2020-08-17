package ch.ivyteam.enginecockpit.monitor.mbeans.value;

import javax.management.openmbean.CompositeData;

final class MCompositeValue implements MValueProvider
{
  private final MValueProvider original;
  private final String compositeName;

  MCompositeValue(MValueProvider original, String compositeName)
  {
    this.original = original;
    this.compositeName = compositeName;
  }

  @Override
  public Object nextValue()
  {
    var composite = (CompositeData)original.nextValue();
    return composite.get(compositeName);
  }
}
