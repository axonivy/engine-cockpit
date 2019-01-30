package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.ivy.system.IProperty;

public class Property
{
  private String name;
  private String desc;
  private String value;
  private String defaultValue;
  
  public Property(IProperty prop)
  {
    name = prop.getName();
    desc = prop.getDescription();
    value = prop.getValue();
    defaultValue = prop.getDefaultValue();
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getDesc()
  {
    return desc;
  }

  public void setDesc(String desc)
  {
    this.desc = desc;
  }

  public String getValue()
  {
    return value;
  }

  public void setValue(String value)
  {
    this.value = value;
  }

  public String getDefaultValue()
  {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue)
  {
    this.defaultValue = defaultValue;
  }
  
}
