package ch.ivyteam.enginecockpit.monitor.mbeans;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public class MName {
  public static final MName ROOT = MName.parse("");
  private final ObjectName name;
  private final String domain;
  private final List<Property> properties = new ArrayList<>();

  public static MName parse(String nameStr) {
    try {
      return new MName(ObjectName.getInstance(nameStr));
    } catch (MalformedObjectNameException ex) {
      throw new RuntimeException("Cannot parse MBean name '"+nameStr+"'", ex);
    }
  }

  public MName(ObjectName name) {
    this.name = name;
    if (name.getDomain().equals(ObjectName.WILDCARD.getDomain())) {
      this.domain = "";
    } else {
      this.domain = name.getDomain();
    }
    name.getKeyPropertyList().entrySet().forEach(entry -> properties.add(new Property(entry.getKey(), entry.getValue())));
    Collections.sort(properties, new Property.PositionComparator(name.getKeyPropertyListString()));
    ensureTypeIsFirst();
  }

  private void ensureTypeIsFirst() {
    Property type = properties.stream().filter(property -> property.key.equals("type")).findAny()
            .orElse(null);
    if (type != null) {
      properties.remove(type);
      properties.add(0, type);
    }
  }

  public ObjectName getObjectName() {
    return name;
  }

  public String getDescription() {
    try {
      return ManagementFactory.getPlatformMBeanServer().getMBeanInfo(name).getDescription();
    } catch (IntrospectionException | InstanceNotFoundException | ReflectionException ex) {
      return "n.a.";
    }
  }

  public String getDisplayName() {
    if (properties.isEmpty()) {
      return name.getDomain();
    }
    return properties.get(properties.size() - 1).value;
  }

  public String getFullDisplayName() {
    StringBuilder builder = new StringBuilder();
    builder.append(name.getDomain());
    if (!properties.isEmpty()) {
      for (Property property : properties) {
        builder.append('/');
        builder.append(property.value);
      }
    }

    return builder.toString();
  }

  private boolean isChild(MName child) {
    if (domain.isEmpty()) {
      return !child.domain.isEmpty();
    }
    return domain.equals(child.domain) &&
            properties.size() < child.properties.size() &&
            samePrefixProperties(child.properties);
  }

  private MName getDirectChild(MName other) {
    if (domain.isEmpty()) {
      return toJmxName(other.domain, Collections.emptyList());
    }
    return toJmxName(other.domain, other.properties.subList(0, properties.size() + 1));
  }

  private static MName toJmxName(String domain, List<Property> properties) {
    StringBuilder builder = new StringBuilder();
    builder.append(domain);
    builder.append(':');
    if (!properties.isEmpty()) {
      String props = properties
              .stream()
              .map(prop -> {
                return prop.key + "=" + prop.value;
              })
              .collect(Collectors.joining(","));
      builder.append(props);
    } else {
      builder.append("*");
    }
    return parse(builder.toString());
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(domain);
    if (!properties.isEmpty()) {
      builder.append(':');
      builder.append(
              properties
                      .stream()
                      .map(prop -> {
                        return prop.key + "=" + prop.value;
                      })
                      .collect(Collectors.joining(",")));
    }
    return builder.toString();
  }

  private boolean samePrefixProperties(List<Property> props) {
    int pos = 0;
    for (Property property : properties) {
      if (!property.equals(props.get(pos++))) {
        return false;
      }
    }
    return true;
  }

  public Set<MName> getDirectChildren(Set<MName> allNames) {
    return allNames
            .stream()
            .filter(this::isChild)
            .map(this::getDirectChild)
            .collect(Collectors.toSet());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    MName other = (MName) obj;
    return other.name.equals(other.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  private static final class Property {
    private final String key;
    private final String value;

    private Property(String key, String value) {
      this.key = key;
      this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (obj == null || obj.getClass() != this.getClass()) {
        return false;
      }
      Property other = (Property) obj;
      return Objects.equals(key, other.key) &&
              Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
      return Objects.hash(key, value);
    }

    @Override
    public String toString() {
      return "Property [key=" + key + ", value=" + value + "]";
    }

    private static final class PositionComparator implements Comparator<Property> {

      private String propertyList;

      public PositionComparator(String propertyList) {
        this.propertyList = propertyList;
      }

      @Override
      public int compare(Property p1, Property p2) {
        return toPosition(p1) - toPosition(p2);
      }

      private int toPosition(Property property) {
        int pos = propertyList.indexOf(property.key +"=" + property.value);
        if (pos >= 0) {
          return pos;
        }
        throw new IllegalArgumentException("Cannot found property " + property +" in property list '"+propertyList +"'");
      }
    }
  }
}
