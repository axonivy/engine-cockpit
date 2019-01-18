package ch.ivyteam.enginecockpit.model;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.security.IRole;

public class Role
{
  private String name;
  private String description;
  private String displayName;
  private String externalName;
  private boolean member;
  private boolean dynamic;

  public Role(IRole role)
  {
    this(role, false);
  }
  
  public Role(IRole role, boolean member)
  {
    this(role.getName(), role.getDisplayDescription(), role.getDisplayName(), role.getExternalSecurityName(), member, role.isDynamic());
  }

  public Role(String name)
  {
    this(name, "", "", "", false, false);
  }

  public Role(String name, String description, String displayName, String externalName, boolean member, boolean dynamic)
  {
    this.name = name;
    this.description = description;
    this.displayName = displayName;
    this.externalName = externalName;
    this.member = member;
    this.dynamic = dynamic;
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
  
  public boolean isMember()
  {
    return member;
  }
  
  public void setMember(boolean member)
  {
    this.member = member;
  }
  
  public boolean isAdSynced()
  {
    return !StringUtils.isEmpty(externalName);
  }
  
  public boolean isDynamic()
  {
    return dynamic;
  }

  public void setDynamic(boolean dynamic)
  {
    this.dynamic = dynamic;
  }

  @Override
  public String toString()
  {
    return name;
  }

}
