package ch.ivyteam.enginecockpit.monitor.mbeans.value;

import java.util.Arrays;

class MFormattedValue implements MValueProvider
{
  private final String format;
  private final MValueProvider[] original;

  MFormattedValue(String format, MValueProvider... original)
  {
    this.format = format;
    this.original = original;
  }

  @Override
  public Object nextValue()
  {
    Object[] values = Arrays.stream(original).map(MValueProvider::nextValue).toArray();
    return String.format(format, values);
  }
}
