package ch.ivyteam.enginecockpit.monitor.mbeans.value;

import java.lang.management.ManagementFactory;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

final class MPatternAttributeReader implements MValueProvider
{
  private final ObjectName mBeanName;
  private final String attributeName;
  
  MPatternAttributeReader(ObjectName mBeanName, String attributeName)
  {
    this.mBeanName = mBeanName;
    this.attributeName = attributeName;
  }

  @Override
  public Object nextValue()
  {
    var server = ManagementFactory.getPlatformMBeanServer();
    try
    {
      var names = server.queryNames(mBeanName, null);
      long value = 0l;
      for (ObjectName name : names)
      {
        Number val = (Number)server.getAttribute(name, attributeName);
        value += val.longValue();
      }
      return value;
    }
    catch (InstanceNotFoundException | AttributeNotFoundException | ReflectionException | MBeanException ex)
    {
      throw new RuntimeException("Cannot read attribute "+attributeName+" of MBean "+mBeanName, ex);
    }
  }
}