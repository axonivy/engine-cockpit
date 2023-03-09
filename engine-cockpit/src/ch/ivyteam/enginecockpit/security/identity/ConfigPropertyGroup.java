package ch.ivyteam.enginecockpit.security.identity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class ConfigPropertyGroup {

  private final String name;
  private final List<ConfigProperty> properties = new ArrayList<>();

  public ConfigPropertyGroup(String name) {
    this.name = name;
  }

  public void add(ConfigProperty property) {
    properties.add(property);
  }

  public List<ConfigProperty> getProperties() {
    return properties;
  }

  public String getName() {
    return name;
  }

  public static List<ConfigPropertyGroup> toGroups(List<ConfigProperty> properties) {
    var propertyGroups = new HashMap<String, ConfigPropertyGroup>();
    for (var p : properties) {
      var propertyName = p.getName();
      if (isParentKey(properties, propertyName)) {
        continue;
      }
      var name = toName(propertyName);
      var group = propertyGroups.computeIfAbsent(name, k -> new ConfigPropertyGroup(name));
      group.add(p);
    }
    return propertyGroups.values().stream().collect(Collectors.toList());
  }

  private static boolean isParentKey(List<ConfigProperty> properties, String n) {
    return properties.stream().map(ConfigProperty::getName).anyMatch(na -> !n.equals(na) && na.startsWith(n));
  }

  private static String toName(String name) {
    if (name.contains(".")) {
      return StringUtils.substringBefore(name, ".");
    }
    return "Configuration";
  }
}
