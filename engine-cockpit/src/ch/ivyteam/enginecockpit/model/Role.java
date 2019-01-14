package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.ivy.security.IRole;

public class Role
{
  private String name;
  private String description;
  private String displayName;
  private String externalName;

  public Role(IRole role)
  {
    this(role.getName(), role.getDisplayDescription(), role.getDisplayName(), role.getExternalSecurityName());
  }

  public Role(String name)
  {
    this(name, "", "", "");
  }

  public Role(String name, String description, String displayName, String externalName)
  {
    this.name = name;
    this.description = description;
    this.displayName = displayName;
    this.externalName = externalName;
  }

  public Role()
  {

  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public String getDisplayName()
  {
    return displayName;
  }

  public void setDisplayName(String displayName)
  {
    this.displayName = displayName;
  }

  public String getExternalName()
  {
    return externalName;
  }

  public void setExternalName(String externalName)
  {
    this.externalName = externalName;
  }

  @Override
  public String toString()
  {
    return name;
  }

}
