package ch.ivyteam.enginecockpit.monitor.mbeans.value;

import java.lang.management.ManagementFactory;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

final class MAttributeReader implements MValueProvider
{
  private final ObjectName mBeanName;
  private final String attributeName;
  
  MAttributeReader(ObjectName mBeanName, String attributeName)
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
      return server.getAttribute(mBeanName, attributeName);
    }
    catch (InstanceNotFoundException | AttributeNotFoundException | ReflectionException | MBeanException ex)
    {
      throw new RuntimeException("Cannot read attribute "+attributeName+" of MBean "+mBeanName, ex);
    }
  }
}