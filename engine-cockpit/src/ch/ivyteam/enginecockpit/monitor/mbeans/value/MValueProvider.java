package ch.ivyteam.enginecockpit.monitor.mbeans.value;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.mbeans.MAttribute;
import ch.ivyteam.enginecockpit.monitor.mbeans.MName;

public interface MValueProvider
{
  Object nextValue();
  
  static MValueProvider attribute(String mBeanName, String attributeName)
  {
    try
    {
      return attribute(new ObjectName(mBeanName), attributeName);
    }
    catch (MalformedObjectNameException ex)
    {
      throw new IllegalArgumentException("Parameter mBeanName must be valid ObjectName", ex);
    }
  }
  
  static MValueProvider attribute(ObjectName mBeanName, String attributeName)
  {
    if (mBeanName.isPattern())
    {
      return new MPatternAttributeReader(mBeanName, attributeName);
    }
    else
    {
      return new MAttributeReader(mBeanName, attributeName);
    }
  }

  static MValueProvider attribute(MName selected, MAttribute attribute)
  {
    return attribute(selected.getObjectName(), attribute.getName());
  }
  
  static MValueProvider delta(MValueProvider original)
  {
    return new MDeltaValue(original);
  }
  
  static MValueProvider composite(MValueProvider original, String compositeName)
  {
    return new MCompositeValue(original, compositeName);
  }
  
  static MValueProvider quotient(MValueProvider divident, MValueProvider divisor)
  {
    return new MQuotientValue(divident, divisor);
  }
  
  static MValueProvider derivation(MValueProvider divident, MValueProvider divisor)
  {
    return quotient(delta(divident), delta(divisor));
  }
  
  static MValueProvider percentage(MValueProvider original)
  {
    return new MPercentageValue(original);
  }
  
  static MValueProvider scale(MValueProvider original, long factor)
  {
    return new MScaleValue(original, factor);
  }
  
  static MValueProvider format(String format, MValueProvider... original)
  {
    return new MFormattedValue(format, original);
  }
  
  static MValueProvider cache(int cacheTimes, MValueProvider original)
  {
    return new MCachedValue(cacheTimes, original);
  }
}
