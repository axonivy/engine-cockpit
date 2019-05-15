package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.enginecockpit.util.Configuration;

public class SimpleVariable
{
  private String name;
  private String description;
  private String value;
  private boolean yamlConfig;

  public SimpleVariable()
  {
    
  }

  public SimpleVariable(String name, String desc, String value, String appName)
  {
    this.name = name;
    this.description = desc;
    this.value = value;
    this.yamlConfig = Configuration.get("Applications." + appName + ".GlobalVariables." + name).isPresent();
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

  public void setValue(String value)
  {
    this.value = value;
  }

  public String getValue()
  {
    return value;
  }
  
  public boolean isYamlConfig()
  {
    return yamlConfig;
  }
}