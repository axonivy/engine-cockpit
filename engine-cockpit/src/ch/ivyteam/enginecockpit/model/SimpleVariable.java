package ch.ivyteam.enginecockpit.model;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.vars.Variable;

public class SimpleVariable
{
  private String name;
  private String description;
  private String defaultValue;
  private String value;
  private boolean isResetable;

  public SimpleVariable()
  {
  }
  
  public SimpleVariable(Variable var, String env)
  {
    this.name = var.name();
    this.value = var.value();
    this.defaultValue = var.defaultValue();
    this.description = var.description();
    this.isResetable = isSourceResetable(var, env);
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
  
  public boolean isResetable()
  {
    return isResetable;
  }
  
  private static boolean isSourceResetable(Variable var, String env) 
  {
    var isDefault = StringUtils.equals(var.value(), var.defaultValue());
    boolean isResetable = true;
    if (var.source().startsWith("file:"))
    {
      if (env == null)
      {
        isResetable = var.source().endsWith("app.yaml");
      }
      else
      {
        isResetable = var.source().endsWith("_" + env + "/app.yaml");
      }
    }
    return isResetable && !isDefault;
  }
  
}
