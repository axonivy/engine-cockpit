package ch.ivyteam.enginecockpit.system;

import ch.ivyteam.db.jdbc.DatabaseCreationParameter;

public class SystemDbCreationParameter
{
  
  private String value;
  private DatabaseCreationParameter param;
  
  public SystemDbCreationParameter(DatabaseCreationParameter param, String defaultValue)
  {
    this.param = param;
    this.value = defaultValue;
  }

  public void setValue(String value)
  {
    this.value = value;
  }
  
  public String getValue()
  {
    return value;
  }
  
  public String getLabel()
  {
    return param.getLabel();
  }
  
  public boolean isDisabled()
  {
    return "databaseName".equals(param.getName());
  }
  
  public boolean isPassword()
  {
    return param.isPassword();
  }
  
  public boolean isRequired()
  {
    return param.isRequired();
  }
  
  public String getCssClass()
  {
    return "creation-param-" + getLabel().replace(" ", "").toLowerCase();
  }
  
  public DatabaseCreationParameter getParam()
  {
    return param;
  }
  
}
