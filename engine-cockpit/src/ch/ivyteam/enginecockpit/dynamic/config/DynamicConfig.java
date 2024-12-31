package ch.ivyteam.enginecockpit.dynamic.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.ivy.configuration.configurator.Config;
import ch.ivyteam.ivy.configuration.configurator.Configurator;
import ch.ivyteam.ivy.configuration.configurator.ConfiguratorMetadataProvider;
import ch.ivyteam.ivy.configuration.meta.Metadata;
import ch.ivyteam.ivy.configuration.restricted.ConfigKey;

@SuppressWarnings("restriction")
public class DynamicConfig {

  private final List<ConfigPropertyGroup> groups;
  private final BiConsumer<ConfigKey, String> setter;

  public DynamicConfig(List<ConfigPropertyGroup> groups, BiConsumer<ConfigKey, String> setter) {
    this.groups = groups;
    this.setter = setter;
  }

  public List<ConfigPropertyGroup> getGroups() {
    return groups;
  }

  public void save(ConfigPropertyGroup group) {
    var gKey = ConfigKey.create(group.getName());
    for (var p : group.getProperties()) {
      var shortKey = StringUtils.substringAfter(p.getName(), group.getName() + ".");
      var pKey = ConfigKey.create(p.getName());
      if (!shortKey.isBlank()) {
        pKey = gKey.append(shortKey);
      }
      int separator = shortKey.indexOf('.');
      if (separator != -1) {
        var before = shortKey.substring(0, separator);
        var next = shortKey.substring(separator + 1);
        pKey = gKey.append(before).append(next);
      }
      setter.accept(pKey, p.getValue());
    }
    message();
  }

  public static void message() {
    Message.info()
        .clientId("dynamicConfigFormSaveSuccess")
        .summary("Successfully saved")
        .show();
  }

  public static Builder create() {
    return new Builder();
  }

  @SuppressWarnings("hiding")
  public static class Builder {

    private Configurator configurator;
    private Config config;

    public Builder configurator(Configurator configurator) {
      this.configurator = configurator;
      return this;
    }

    public Builder config(Config config) {
      this.config = config;
      return this;
    }

    public DynamicConfig toDynamicConfig() {
      var properties = new ConfiguratorMetadataProvider(configurator).get().entrySet().stream()
          .map(entry -> toConfigProperty(entry.getKey(), entry.getValue()))
          .collect(Collectors.toList());
      var propertyGroups = ConfigPropertyGroup.toGroups(properties);
      return new DynamicConfig(propertyGroups, config::setProperty);
    }

    private ConfigProperty toConfigProperty(String key, Metadata metadata) {
      var value = "";
      Map<String, String> keyValue = Map.of();
      if (metadata.isKeyValue()) {
        keyValue = new HashMap<>(config.getPropertyAsKeyValue(key));
      } else {
        value = config.getProperty(key);
      }
      return new ConfigProperty(config::setProperty, key, value, keyValue, metadata);
    }
  }
}
