package ch.ivyteam.enginecockpit.monitor.mbeans;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.management.Attribute;
import javax.management.MBeanAttributeInfo;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.TabularData;

import org.apache.commons.lang.ArrayUtils;

public class MAttribute implements Comparable<MAttribute>
{
  private Attribute attribute;
  private MBeanAttributeInfo info;
  
  MAttribute(Attribute attribute, MBeanAttributeInfo info)
  {
    this.attribute = attribute;
    this.info = info;    
  }
  
  public String getName()
  {
    return attribute.getName();
  }
  
  public String getTooltip()
  {
    return info.getDescription();
  }

  @Override
  public int compareTo(MAttribute other)
  {
    return getName().compareTo(other.getName());
  }

  public boolean getIsTraceable()
  {
    String type = info.getType();
    return compositeWithNumbersOnly(type)       ||
           isNumber(type);
  }
  
  private boolean isNumber(String type)
  {
    return Byte.class.getName().equals(type)    ||
           Byte.TYPE.getName().equals(type)     ||
           Short.class.getName().equals(type)   ||
           Short.TYPE.getName().equals(type)    ||
           Integer.class.getName().equals(type) ||
           Integer.TYPE.getName().equals(type)  ||
           Long.class.getName().equals(type)    ||
           Long.TYPE.getName().equals(type)     ||
           Float.class.getName().equals(type)   ||
           Float.TYPE.getName().equals(type)    ||
           Double.class.getName().equals(type)  ||
           Double.TYPE.getName().equals(type);
  }

  private boolean compositeWithNumbersOnly(String type)
  {
    if (isComposite(type))
    {
      CompositeData data = (CompositeData)attribute.getValue();
      if (data == null)
      {
        return false;
      }
      CompositeType ctype = data.getCompositeType();
      return ctype
          .keySet()
          .stream()
          .map(ctype::getType)
          .map(OpenType::getTypeName)
          .allMatch(this::isNumber);     
    }
    return false;
  }
  
  public boolean isComposite()
  {
    return isComposite(info.getType());
  }

  private static boolean isComposite(String type)
  {
    return CompositeData.class.getName().equals(type);
  }
  
  public Set<String> getCompositeNames()
  {
    CompositeData data = (CompositeData)attribute.getValue();
    CompositeType ctype = data.getCompositeType();
    return ctype.keySet();
  }

  public String getValue()
  {
    Object value = attribute.getValue();
    return getValue(value);
  }
  
  private static String getValue(Object value)
  {
    if (value instanceof TabularData)
    {
      return getTabValue((TabularData) value);
    }
    if (value instanceof CompositeData)
    {
      return getCompositeValue((CompositeData) value);
    }
    if (value != null && value.getClass().isArray())
    {
      return getArrayValue(value);
    }
    return Objects.toString(value);
  }

  private static String getTabValue(TabularData tab)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("<table>");
    tab
        .values()
        .forEach(entry -> appendRow(entry, builder));
    builder.append("</table>");
    return builder.toString();
  }
  
  private static void appendRow(Object row, StringBuilder builder)
  {
    builder.append("<tr>");
    ((CompositeData)row).values().forEach(value -> builder.append("<td>"+getValue(value)+"</td>"));
    builder.append("</tr>");
  }
  
  private static String getCompositeValue(CompositeData value)
  {
    CompositeType type = value.getCompositeType();
    return type
        .keySet()
        .stream()
        .map(key -> key.toString()+"="+getValue(value.get(key)))
        .collect(Collectors.joining(", "));
  }

  private static String getArrayValue(Object array)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("[");
    boolean first = true;
    for (Object entry : getEntries(array))
    {
      if (!first)
      {
        builder.append(", ");
      }
      first = false;
      builder.append(getValue(entry));
    }
    builder.append("]");
    return builder.toString();
  }

  private static Object[] getEntries(Object array)
  {
    if (array instanceof byte[])
    {
      return ArrayUtils.toObject((byte[]) array);
    }
    if (array instanceof short[])
    {
      return ArrayUtils.toObject((short[]) array);
    }
    if (array instanceof int[])
    {
      return ArrayUtils.toObject((int[]) array);
    }
    if (array instanceof long[])
    {
      return ArrayUtils.toObject((long[]) array);
    }
    if (array instanceof float[])
    {
      return ArrayUtils.toObject((float[]) array);
    }
    if (array instanceof double[])
    {
      return ArrayUtils.toObject((double[]) array);
    }
    if (array instanceof boolean[])
    {
      return ArrayUtils.toObject((boolean[]) array);
    }
    return (Object[])array;
  }
}
