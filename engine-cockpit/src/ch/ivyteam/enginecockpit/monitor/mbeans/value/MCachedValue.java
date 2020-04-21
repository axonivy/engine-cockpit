package ch.ivyteam.enginecockpit.monitor.mbeans.value;

public class MCachedValue implements MValueProvider
{
  private final int cacheTimes;
  private final MValueProvider original;
  private int read = 0;
  private Object cachedValue;

  public MCachedValue(int cacheTimes, MValueProvider original)
  {
    this.cacheTimes = cacheTimes;
    this.original = original;
  }

  @Override
  public Object nextValue()
  {
    if (read == 0)
    {
      cachedValue = original.nextValue();
      read = cacheTimes;
    }
    else
    {
      read--;
    }
    return cachedValue; 
  }
}
