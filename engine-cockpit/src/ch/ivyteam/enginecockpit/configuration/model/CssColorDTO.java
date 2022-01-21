package ch.ivyteam.enginecockpit.configuration.model;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.configuration.restricted.branding.CssColor;

@SuppressWarnings("restriction")
public class CssColorDTO implements Serializable {
  private String color;
  private String value;
  private String defaultValue;
  private boolean isDefault;

  public CssColorDTO() {}

  public CssColorDTO(CssColor cssColor) {
    this(cssColor.getColor(), cssColor.getValue(), cssColor.getDefaultValue());
  }

  public CssColorDTO(String color, String value, String defaultValue) {
    this.color = color;
    this.value = value;
    this.defaultValue = defaultValue;
    this.isDefault = StringUtils.equals(value, defaultValue);
  }

  public String getColor() {
    return color;
  }
  public void setColor(String color) {
    this.color = color;
  }
  public String getValue() {
    return value;
  }
  public void setValue(String value) {
    this.value = value;
  }
  public String getDefaultValue() {
    return defaultValue;
  }
  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }
  public boolean isDefault() {
    return isDefault;
  }
  public void setDefault(boolean isDefault) {
    this.isDefault = isDefault;
  }

}
