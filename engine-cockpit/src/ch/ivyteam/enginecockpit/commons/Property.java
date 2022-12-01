package ch.ivyteam.enginecockpit.commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ch.ivyteam.ivy.application.config.Meta;
import ch.ivyteam.ivy.configuration.restricted.ConfigValueFormat;

@SuppressWarnings("restriction")
public class Property {
  private String name;
  private String value;
  private boolean sensitive;

  public Property() {}

  public Property(String name, String value) {
    this(name, value, null);
  }

  public Property(String name, String value, Meta meta) {
    this.name = name;
    this.value = value;
    this.sensitive = meta != null ? meta.format() == ConfigValueFormat.PASSWORD : false;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public boolean isSensitive() {
    return sensitive;
  }

  public void setSensitive(boolean sensitive) {
    this.sensitive = sensitive;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Property)) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    Property other = (Property) obj;
    return new EqualsBuilder()
            .append(name, other.getName())
            .append(value, other.getValue())
            .append(sensitive, other.isSensitive())
            .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
            .append(name)
            .append(value)
            .append(sensitive)
            .toHashCode();
  }
}
