package ch.ivyteam.enginecockpit.system;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.db.jdbc.ConnectionProperty;

public class SystemDbConnectionProperty
{
  
  private String value;
  private String defaultValue;
  private boolean isDefault;
  private ConnectionProperty property;
  
  public SystemDbConnectionProperty(ConnectionProperty connectionProperty, String defaultValue)
  {
    this.property = connectionProperty;
    this.value = defaultValue;
    this.defaultValue = defaultValue;
    this.isDefault = StringUtils.equals(value, defaultValue);
  }

  public void setValue(String value)
  {
    this.isDefault = StringUtils.equals(value, defaultValue);
    this.value = value;
  }
  
  public String getValue()
  {
    return value;
  }
  
  public String getPasswordPlaceholder()
  {
    return "*".repeat(value.length());
  }
  
  public void setDefaultValue(boolean defaultValue)
  {
    isDefault = defaultValue;
    if (defaultValue)
    {
      this.value = this.defaultValue;
    }
  }
  
  public boolean isDefaultValue()
  {
    return isDefault;
  }
  
  public String getName()
  {
    return property.getName();
  }
  
  public String getLabel()
  {
    return property.getLabel();
  }
  
  public String getCssClass()
  {
    return "sysdb-dynamic-form-" + getLabel().replace(" ", "").toLowerCase();
  }
  
  public boolean isInput()
  {
    return !isNumber() && !isPassword();
  }
  
  public boolean isNumber()
  {
    return property.isNumber();
  }
  
  public boolean isPassword()
  {
    return property.isConfidential();
  }
  
  public boolean isRequired()
  {
    return property.isMandatory();
  }
  
  public ConnectionProperty getProperty()
  {
    return property;
  }

}
