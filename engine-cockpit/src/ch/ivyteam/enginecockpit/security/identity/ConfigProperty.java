package ch.ivyteam.enginecockpit.security.identity;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.configuration.meta.Metadata;

public class ConfigProperty {

  private final String key;
  private String value;
  private final Metadata metadata;

  // encapsulate the special handling for a key value property
  private final KeyValueProperty keyValueProperty;

  public ConfigProperty(String key, String value, Map<String, String> keyValue, Metadata metadata) {
    this.key = key;
    this.value = value;
    this.metadata = metadata;
    this.keyValueProperty = new KeyValueProperty(keyValue);
  }

  public String getName() {
    return key;
  }

  public String getLabel() {
    if (isKeyValue()) {
      return Arrays.stream(key.split(".")).collect(Collectors.joining(" "));
    }
    if (key.contains(".")) {
      return StringUtils.substringAfter(key, ".");
    }
    return key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    if (metadata.isPassword() && StringUtils.isEmpty(value)) {
      return;
    }
    this.value = value;
  }

  public String getDefaultValue() {
    return metadata.defaultValue();
  }

  public String getDescription() {
    return metadata.description();
  }

  public boolean isString() {
    return !isKeyValue() && !isPassword();
  }

  public boolean isPassword() {
    return metadata.isPassword();
  }

  public boolean isKeyValue() {
    return metadata.isKeyValue();
  }

  public String getPasswordPlaceholder() {
    return "*".repeat(value.length());
  }

  public KeyValueProperty getKeyValueProperty() {
    return keyValueProperty;
  }

  @SuppressWarnings("restriction")
  public class KeyValueProperty {

    private final Map<String, String> keyValue;
    private final ch.ivyteam.ivy.configuration.restricted.meta.KeyValueVarType type;

    private KeyValueProperty(Map<String, String> keyValue) {
      this.keyValue = keyValue;
      this.type = toType();
    }

    private ch.ivyteam.ivy.configuration.restricted.meta.KeyValueVarType toType() {
      if (metadata.type() instanceof ch.ivyteam.ivy.configuration.restricted.meta.KeyValueVarType t) {
        return t;
      }
      return null;
    }

    public Set<Entry<String, String>> getKeyValue() {
      return keyValue.entrySet();
    }

    public Map<String, String> keyValue() {
      return keyValue;
    }

    public void addKeyValue(String k, String v) {
      keyValue.put(k, v);
    }

    public void removeKeyValue(String k) {
      keyValue.remove(k);
    }

    public String getKeyValueKeyName() {
      return type.keyName();
    }

    public String getKeyValueValueName() {
      return type.valueName();
    }
  }
}
