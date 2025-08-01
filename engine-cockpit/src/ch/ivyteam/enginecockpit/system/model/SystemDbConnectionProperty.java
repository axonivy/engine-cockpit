package ch.ivyteam.enginecockpit.system.model;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.db.jdbc.ConnectionProperty;

public class SystemDbConnectionProperty {

  private String value;
  private final String defaultValue;
  private boolean isDefault;
  private final ConnectionProperty property;

  public SystemDbConnectionProperty(ConnectionProperty connectionProperty, String defaultValue) {
    this.property = connectionProperty;
    this.value = defaultValue;
    this.defaultValue = defaultValue;
    this.isDefault = Objects.equals(value, defaultValue);
  }

  public void setValue(String value) {
    if (ConnectionProperty.PASSWORD == this.property && StringUtils.isBlank(value)) {
      return;
    }
    this.isDefault = Objects.equals(value, defaultValue);
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public String getPasswordPlaceholder() {
    return "*".repeat(value.length());
  }

  public void setDefaultValue(boolean defaultValue) {
    isDefault = defaultValue;
    if (defaultValue) {
      this.value = this.defaultValue;
    }
  }

  public boolean isDefaultValue() {
    return isDefault;
  }

  public String getName() {
    return property.getName();
  }

  public String getLabel() {
    return property.getLabel();
  }

  public String getCssClass() {
    return "sysdb-dynamic-form-" + getLabel().replace(" ", "").toLowerCase();
  }

  public boolean isInput() {
    return !isNumber() && !isPassword();
  }

  public boolean isNumber() {
    return property.isNumber();
  }

  public boolean isPassword() {
    return property.isConfidential();
  }

  public boolean isRequired() {
    return property.isMandatory();
  }

  public ConnectionProperty getProperty() {
    return property;
  }

}
