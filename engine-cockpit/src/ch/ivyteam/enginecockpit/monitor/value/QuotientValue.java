package ch.ivyteam.enginecockpit.monitor.value;

public class QuotientValue implements ValueProvider
{
  private final ValueProvider divident;
  private final ValueProvider divisor;

  public QuotientValue(ValueProvider divident, ValueProvider divisor)
  {
    this.divident = divident;
    this.divisor = divisor;
  }

  @Override
  public Value nextValue()
  {
    var dividentValue = divident.nextValue();
    var divisorValue = divisor.nextValue();
    if (divisorValue == null || divisorValue.longValue() == 0)
    {
      return Value.NO_VALUE;
    }
    Object result;
    if (divisorValue.longValue() > dividentValue.longValue())
    {
      result = dividentValue.doubleValue() / divisorValue.doubleValue();  
    }
    else
    {
      result = dividentValue.longValue() / divisorValue.longValue();
    }
    return new Value(result, dividentValue.unit());
  }

}
