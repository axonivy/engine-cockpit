package ch.ivyteam.enginecockpit.monitor.mbeans.value;

public class MQuotientValue implements MValueProvider
{
  private final MValueProvider divident;
  private final MValueProvider divisor;

  public MQuotientValue(MValueProvider divident, MValueProvider divisor)
  {
    this.divident = divident;
    this.divisor = divisor;
  }

  @Override
  public Object nextValue()
  {
    var dividentValue = (Number)divident.nextValue();
    var divisorValue = (Number)divisor.nextValue();
    if (divisorValue == null || divisorValue.longValue() == 0)
    {
      return 0;
    }
    var result = dividentValue.longValue() / divisorValue.longValue();
    return result;
  }

}
