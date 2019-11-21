package ch.ivyteam.enginecockpit.system;

import ch.ivyteam.ivy.persistence.db.DatabaseCreationParameter;

@SuppressWarnings("restriction")
public class SystemDbCreationParameter
{
  
  private String value;
  private DatabaseCreationParameter parameter;
  
  public SystemDbCreationParameter(DatabaseCreationParameter parameter)
  {
    this.parameter = parameter;
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
    return parameter.getName();
  }
  
  public DatabaseCreationParameter getParameter()
  {
    return parameter;
  }

}
