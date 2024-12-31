package ch.ivyteam.enginecockpit.monitor.value;

import java.lang.management.ManagementFactory;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import ch.ivyteam.enginecockpit.monitor.unit.Unit;

final class MPatternAttributeReader implements ValueProvider {
  private final ObjectName mBeanName;
  private final String attributeName;
  private final Unit unit;

  MPatternAttributeReader(ObjectName mBeanName, String attributeName, Unit unit) {
    this.mBeanName = mBeanName;
    this.attributeName = attributeName;
    this.unit = unit;
  }

  @Override
  public Value nextValue() {
    var server = ManagementFactory.getPlatformMBeanServer();
    try {
      var names = server.queryNames(mBeanName, null);
      long value = 0l;
      for (ObjectName name : names) {
        Number val = (Number) server.getAttribute(name, attributeName);
        value += val.longValue();
      }
      return new Value(value, unit);
    } catch (InstanceNotFoundException | AttributeNotFoundException | ReflectionException
        | MBeanException ex) {
      throw new RuntimeException("Cannot read attribute " + attributeName + " of MBean " + mBeanName, ex);
    }
  }
}
