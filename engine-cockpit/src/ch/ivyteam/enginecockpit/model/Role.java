package ch.ivyteam.enginecockpit.model;

import javax.ws.rs.core.UriBuilder;

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

  public Role(String name)
  {
    this.name = name;
  }

  public Role(IRole role)
  {
    this.name = role.getName();
    this.description = role.getDisplayDescription();
    this.displayName = role.getDisplayName();
    this.externalName = role.getExternalSecurityName();
    this.member = false;
    this.dynamic = role.isDynamic();
  }
  
  public Role(IRole role, boolean member)
  {
    this(role);
    this.member = member;
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

  public String getViewUrl()
  {
    return UriBuilder.fromPath("roledetail.xhtml")
            .queryParam("roleName", name)
            .build()
            .toString();
  }
}
