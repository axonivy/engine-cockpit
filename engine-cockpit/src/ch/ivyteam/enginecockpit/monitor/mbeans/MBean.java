package ch.ivyteam.enginecockpit.monitor.mbeans;

import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;

import ch.ivyteam.enginecockpit.util.DateUtil;
import ch.ivyteam.enginecockpit.util.DurationFormat;
import ch.ivyteam.enginecockpit.util.ErrorHandler;
import ch.ivyteam.enginecockpit.util.ErrorValue;

public class MBean {

  public static final String NOT_AVAILABLE = DurationFormat.NOT_AVAILABLE_STR;
  private static final Object[] EMPTY_PARAMS = new Object[0];
  private static final String[] EMPTY_TYPES = new String[0];

  private ErrorHandler handler;
  private ObjectName name;

  private MBean(ErrorHandler handler, ObjectName name) {
    this.handler = handler;
    this.name = name;
  }

  public static MBean create(ErrorHandler handler, ObjectName name) {
    return new MBean(handler, name);
  }

  public static MBean create(ErrorHandler handler, String name) {
    try {
      return create(handler, new ObjectName(name));
    } catch (MalformedObjectNameException ex) {
      handler.showError("Could not parse MBean name '"+name+"'", ex);
      return null;
    }
  }

  public Attribute readAttribute(String attribute) {
    return new Attribute(attribute);
  }

  public String getNameKeyProperty(String key) {
    return name.getKeyProperty(key);
  }

  public void invokeMethod(String method) {
    try {
      ManagementFactory.getPlatformMBeanServer().invoke(name, method, EMPTY_PARAMS, EMPTY_TYPES);
    } catch (InstanceNotFoundException | ReflectionException | MBeanException ex) {
      handler.showError("Cannot invoke method '" + method + "' on MBean '" + name + "'", ex);
    }
  }

  public final class Attribute {

    private String attribute;

    public Attribute(String attribute) {
      this.attribute = attribute;
    }

    public Object asObject() {
      try {
        return ManagementFactory.getPlatformMBeanServer().getAttribute(name, attribute);
      } catch (InstanceNotFoundException | AttributeNotFoundException | ReflectionException | MBeanException ex) {
        handler.showError("Cannot read attribute " + attribute, ex);
        return ex.getMessage();
      }
    }

    public Long asNullableLong() {
      return (Long)asObject();
    }

    public long asLong() {
      return (long)asObject();
    }

    public ErrorValue asError() {
      return new ErrorValue((CompositeData)asObject());
    }

    public String asDateString() {
      Date date = (Date) asObject();
      if (date == null) {
        return NOT_AVAILABLE;
      }
      return DateUtil.formatDate(date);
    }

    public String asMillis() {
      return DurationFormat.NOT_AVAILABLE.milliSeconds(asNullableLong());
    }

    public String asMicros() {
      return DurationFormat.NOT_AVAILABLE.microSeconds(asNullableLong());
    }

    public boolean asBoolean() {
      return (boolean)asObject();
    }

    public String asString() {
      return (String) asObject();
    }

    public <T> List<T> asList(Function<CompositeData, T> mapper) {
      var array = (CompositeData[])asObject();
      return Stream.of(array).map(mapper).toList();
    }

    public String asMinExecutionTime() {
      long executions = asLong();
      if (executions == 0) {
        return NOT_AVAILABLE;
      }
      return readAttribute(attribute+"MinExecutionTimeInMicroSeconds").asMicros();
    }

    public String asAvgExecutionTime() {
      var total = readAttribute(attribute+"TotalExecutionTimeInMicroSeconds").asNullableLong();
      if (total == null) {
        return DurationFormat.NOT_AVAILABLE.microSeconds(total);
      }
      long executions = asLong();
      if (executions == 0) {
        return NOT_AVAILABLE;
      }
      return DurationFormat.NOT_AVAILABLE.microSeconds(total / executions);
    }

    public String asMaxExecutionTime() {
      long executions = asLong();
      if (executions == 0) {
        return NOT_AVAILABLE;
      }
      return readAttribute(attribute+"MaxExecutionTimeInMicroSeconds").asMicros();
    }
  }
}
