package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.ivy.vars.Variable;

public class SimpleVariable
{
  private String name;
  private String description;
  private String defaultValue;
  private String value;

  public SimpleVariable()
  {
  }
  
  public SimpleVariable(Variable var)
  {
    this.name = var.name();
    this.value = var.value();
    this.defaultValue = var.defaultValue();
    this.description = var.description();
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return name;
  }

  public void setDescription(String desc)
  {
    this.description = desc;
  }

  public String getDescription()
  {
    return description;
  }

  public String getDefaultValue()
  {
    return defaultValue;
  }

  public void setValue(String value)
  {
    this.value = value;
  }

  public String getValue()
  {
    return value;
  }
  
}
