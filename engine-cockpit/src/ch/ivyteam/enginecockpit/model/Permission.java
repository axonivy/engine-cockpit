package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.ivy.security.IPermissionAccess;

public class Permission
{
  private String name;
  private long id;
  private boolean group;
  private boolean grant;
  private boolean deny;
  private boolean explicit;
  private String permissionHolder;

  protected Permission(String name, long id, boolean group, boolean grant, boolean deny)
  {
    this.name = name;
    this.id = id;
    this.group = group;
    this.grant = grant;
    this.deny = deny;
  }

  public Permission(IPermissionAccess access)
  {
    this(access.getPermission().getName(), 
            access.getPermission().getId(), 
            false,
            access.isGranted(), 
            access.isDenied());
    this.explicit = access.isExplicit();
    this.permissionHolder = access.getPermissionHolder() == null ? null : access.getPermissionHolder().getName();
  }

  public String getName()
  {
    return name;
  }

  public long getId()
  {
    return id;
  }

  public boolean isGroup()
  {
    return group;
  }

  public boolean isGrant()
  {
    return grant;
  }

  public void setGrant(boolean grant)
  {
    this.grant = grant;
  }

  public boolean isGrantDisabled()
  {
    return explicit && grant;
  }
  
  public boolean isUnGrantDisabled()
  {
    return !isGrantDisabled();
  }

  public boolean isDeny()
  {
    return deny;
  }

  public void setDeny(boolean deny)
  {
    this.deny = deny;
  }

  public boolean isDenyDisabled()
  {
    return explicit && deny;
  }
  
  public boolean isUnDenyDisabled()
  {
    return !isDenyDisabled();
  }

  public void setExplicit(boolean explicit)
  {
    this.explicit = explicit;
  }
  
  public boolean isExplicit()
  {
    return explicit;
  }

  public String getPermissionHolder()
  {
    return permissionHolder;
  }

}