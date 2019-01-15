package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.ivy.security.IPermissionAccess;
import ch.ivyteam.ivy.security.IPermissionGroup;

public class Permission
{
  private String name;
  private long id;
  private boolean group;
  private boolean grant;
  private boolean deny;

  public Permission(String name, long id, boolean group)
  {
    this(name, id, group, true, false);
  }
  
  public Permission(String name, long id, boolean group, boolean grant, boolean deny) 
  {
    this.name = name;
    this.id = id;
    this.group = group;
    this.grant = grant;
    this.deny = deny;
  }
  
  public Permission(IPermissionAccess access) 
  {
    this(access.getPermission().getName(), access.getPermission().getId(), false, access.isGranted(), access.isDenied());
  }
  
  public Permission(IPermissionGroup group) 
  {
    //TODO: get group grant and deny
    this(group.getName(), group.getId(), true, false, false);
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  public boolean isGroup()
  {
    return group;
  }

  public void setGroup(boolean group)
  {
    this.group = group;
  }

  public boolean isGrant()
  {
    return grant;
  }

  public void setGrant(boolean grant)
  {
    this.grant = grant;
  }

  public boolean isDeny()
  {
    return deny;
  }

  public void setDeny(boolean deny)
  {
    this.deny = deny;
  }
  
  

}