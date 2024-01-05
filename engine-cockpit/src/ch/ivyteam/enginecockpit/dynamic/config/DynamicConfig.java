package ch.ivyteam.enginecockpit.dynamic.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.ivy.configuration.configurator.Configurator;
import ch.ivyteam.ivy.configuration.configurator.ConfiguratorMetadataProvider;
import ch.ivyteam.ivy.configuration.meta.Metadata;
import ch.ivyteam.ivy.configuration.restricted.ConfigKey;

@SuppressWarnings("restriction")
public class DynamicConfig {

  private List<ConfigPropertyGroup> groups;
  private BiConsumer<ConfigKey, String> setter;

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
        var next = shortKey.substring(separator+1);
        pKey = gKey.append(before).append(next);
      }
      setter.accept(pKey, p.getValue());
    }
    message();
  }

  public static void message() {
    Message.info("dynamicConfigFormSaveSuccess", "Successfully saved");
  }

  public static Builder create() {
    return new Builder();
  }

  @SuppressWarnings("hiding")
  public static class Builder {

    private Configurator configurator;

    private Function<String, String> getter;
    private BiConsumer<ConfigKey, String> setter;

    private Function<String, Map<String, String>> keyValueGetter;
    private BiConsumer<String, Map<String, String>> keyValueSetter;

    public Builder configurator(Configurator configurator) {
      this.configurator = configurator;
      return this;
    }

    public Builder getter(Function<String, String> getter) {
      this.getter = getter;
      return this;
    }

    public Builder setter(BiConsumer<ConfigKey, String> setter) {
      this.setter = setter;
      return this;
    }

    public Builder keyValueGetter(Function<String, Map<String, String>> keyValueGetter) {
      this.keyValueGetter = keyValueGetter;
      return this;
    }

    public Builder keyValueSetter(BiConsumer<String, Map<String, String>> keyValueSetter) {
      this.keyValueSetter = keyValueSetter;
      return this;
    }

    public DynamicConfig toDynamicConfig() {
      var properties = new ConfiguratorMetadataProvider(configurator).get().entrySet().stream()
              .map(entry -> toConfigProperty(entry.getKey(), entry.getValue()))
              .collect(Collectors.toList());
      var propertyGroups = ConfigPropertyGroup.toGroups(properties);
      return new DynamicConfig(propertyGroups, setter);
    }

    private ConfigProperty toConfigProperty(String key, Metadata metadata) {
      var value = "";
      Map<String, String> keyValue = Map.of();
      if (metadata.isKeyValue()) {
        keyValue = new HashMap<>(keyValueGetter.apply(key));
      } else {
        value = getter.apply(key);
      }
      return new ConfigProperty(keyValueSetter, key, value, keyValue, metadata);
    }
  }
}
