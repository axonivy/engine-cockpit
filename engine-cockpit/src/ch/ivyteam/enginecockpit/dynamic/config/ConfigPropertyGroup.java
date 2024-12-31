package ch.ivyteam.enginecockpit.dynamic.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.dynamic.config.ConfigProperty.KeyValueProperty;

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

  public boolean isKeyValue() {
    return properties.get(0).isKeyValue();
  }

  public KeyValueProperty getKeyValueProperty() {
    return properties.get(0).getKeyValueProperty();
  }

  public static List<ConfigPropertyGroup> toGroups(List<ConfigProperty> properties) {
    var groups = new ArrayList<ConfigPropertyGroup>();
    for (var p : properties) {
      var propertyName = p.getName();
      if (isParentKey(properties, propertyName)) {
        continue;
      }
      var name = toName(p);
      var group = groups.stream()
          .filter(g -> g.getName().equals(name))
          .findAny()
          .orElseGet(() -> {
            var g = new ConfigPropertyGroup(name);
            groups.add(g);
            return g;
          });
      group.add(p);
    }
    return groups;
  }

  private static boolean isParentKey(List<ConfigProperty> properties, String n) {
    return properties.stream()
        .map(ConfigProperty::getName)
        .anyMatch(na -> !n.equals(na) && na.startsWith(n));
  }

  private static String toName(ConfigProperty p) {
    var name = p.getName();
    if (p.isKeyValue()) {
      return Arrays.stream(StringUtils.splitByCharacterTypeCamelCase(name))
          .map(part -> StringUtils.remove(part, "."))
          .map(String::trim)
          .collect(Collectors.joining(" "));
    }
    if (name.contains(".")) {
      return StringUtils.substringBefore(name, ".");
    }
    return "Configuration";
  }
}
