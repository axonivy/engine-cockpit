package ch.ivyteam.enginecockpit.security.identity;

import ch.ivyteam.ivy.configuration.meta.Metadata;

public class ConfigProperty {

  private final String key;
  private String value;
  private final Metadata metadata;

  public ConfigProperty(String key, String value, Metadata metadata) {
    this.key = key;
    this.value = value;
    this.metadata = metadata;
  }

  public String getName() {
    return key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getDefaultValue() {
    return metadata.defaultValue();
  }

  public String getDescription() {
    return metadata.description();
  }
}
