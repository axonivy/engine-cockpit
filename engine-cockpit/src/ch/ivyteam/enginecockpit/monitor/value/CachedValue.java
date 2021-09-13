package ch.ivyteam.enginecockpit.monitor.value;

public class CachedValue implements ValueProvider {
  private final int cacheTimes;
  private final ValueProvider original;
  private int read = 0;
  private Value cachedValue;

  public CachedValue(int cacheTimes, ValueProvider original) {
    this.cacheTimes = cacheTimes;
    this.original = original;
  }

  @Override
  public Value nextValue() {
    if (read == 0) {
      cachedValue = original.nextValue();
      read = cacheTimes;
    } else {
      read--;
    }
    return cachedValue;
  }
}
