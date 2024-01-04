package ch.ivyteam.enginecockpit.dynamic.config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.configuration.meta.Metadata;

public class ConfigProperty {

  private final BiConsumer<String, Map<String, String>> keyValueSaver;
  private final String key;
  private String value;
  private final Metadata metadata;
  private final String label;

  // encapsulate the special handling for a key value property
  private final KeyValueProperty keyValueProperty;

  public ConfigProperty(BiConsumer<String, Map<String, String>> keyValueSaver, String key, String value, Map<String, String> keyValue, Metadata metadata) {
    this.keyValueSaver = keyValueSaver;
    this.key = key;
    this.value = value;
    this.metadata = metadata;
    this.label = toLabel();
    this.keyValueProperty = new KeyValueProperty(keyValue);
  }

  public String getName() {
    return key;
  }

  private String toLabel() {
    if (isKeyValue()) {
      return Arrays.stream(key.split(".")).collect(Collectors.joining(" "));
    }
    if (key.contains(".")) {
      return StringUtils.substringAfter(key, ".");
    }
    return key;
  }

  public String getLabel() {
    return label;
  }

  public String getValue() {
    if (isBoolean()) {
      return value;
    }
    if (isDropdown()) {
      return value;
    }
    if (value.equals(getDefaultValue())){
      return null;
    }
    return value;
  }

  public void setValue(String value) {
    if (metadata.isPassword() && StringUtils.isEmpty(value)) {
      return;
    }
    if (this.value.equals(getDefaultValue()) && StringUtils.isEmpty(value) && !value.equals(getDefaultValue())){
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

  public String getDescriptionFirstline() {
    String[] firstline = metadata.description().split("\n");
    return firstline[0];
  }

  public boolean hasSecondLine() {
    String[] lines = metadata.description().split("\n");
    return lines.length > 1;
  }

  public List<String> getEnumerationValues() {
    return metadata.enumerationValues();
  }

  public boolean isString() {
    return !isKeyValue() && !isPassword() && !isBoolean() && !isDropdown() && !isNumber();
  }

  public boolean isBoolean() {
    return metadata.isBoolean();
  }

  public boolean isNumber() {
    return metadata.isNumber();
  }

  public boolean isDropdown() {
    return metadata.isDropdown();
  }

  public boolean isPassword() {
    return metadata.isPassword();
  }

  public boolean isKeyValue() {
    return metadata.isKeyValue();
  }

  public boolean isDirectoryBrowser() {
    return metadata.isDirectoryBrowser();
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
      save();
    }

    public void removeKeyValue(String k) {
      keyValue.remove(k);
      save();
    }

    private void save() {
      keyValueSaver.accept(getName(), keyValue());
      DynamicConfig.message();
    }

    public String getName() {
      return ConfigProperty.this.getName();
    }

    public String getKeyValueKeyName() {
      return type.keyName();
    }

    public String getKeyValueValueName() {
      return type.valueName();
    }

    public boolean isDirectoryBrowser() {
      return metadata.isDirectoryBrowser();
    }
  }
}
