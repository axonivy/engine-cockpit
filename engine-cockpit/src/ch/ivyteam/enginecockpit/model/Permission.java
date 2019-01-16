package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.ivy.security.IPermissionAccess;
import ch.ivyteam.ivy.security.IPermissionGroup;
import ch.ivyteam.ivy.security.IPermissionGroupAccess;

public class Permission
{
  private String name;
  private long id;
  private boolean group;
  private boolean grant;
  private boolean deny;
  private boolean explicit;
  private String permissionHolder;

  public Permission(String name, long id, boolean group, boolean grant, boolean deny, boolean explicit,
          String permissionHolder)
  {
    this.name = name;
    this.id = id;
    this.group = group;
    this.grant = grant;
    this.deny = deny;
    this.explicit = explicit;
    this.permissionHolder = permissionHolder;
  }

  public Permission(IPermissionAccess access)
  {
    this(access.getPermission().getName(), access.getPermission().getId(), false,
            access.isGranted(), access.isDenied(), access.isExplicit(),
            access.getPermissionHolder() == null ? null : access.getPermissionHolder().getName());
  }

  public Permission(IPermissionGroup group, IPermissionGroupAccess groupAccess)
  {
    this(group.getName(), group.getId(), true,
            groupAccess.isGrantedAllPermissions(),
            groupAccess.isDeniedAllPermissions(), true, null);
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

  public boolean isExplicitAndGrant()
  {
    return explicit && grant;
  }

  @SuppressWarnings("unused")
  public void setExplicitAndGrant(boolean value)
  {
  }

  public boolean isDeny()
  {
    return deny;
  }

  public void setDeny(boolean deny)
  {
    this.deny = deny;
  }

  public boolean isExplicitAndDeny()
  {
    return explicit && deny;
  }

  @SuppressWarnings("unused")
  public void setExplicitAndDeny(boolean value)
  {
  }

  public boolean isExplicit()
  {
    return explicit;
  }

  public void setExplicit(boolean explicit)
  {
    this.explicit = explicit;
  }

  public String getPermissionHolder()
  {
    return permissionHolder;
  }

  public void setPermissionHolder(String permissionHolder)
  {
    this.permissionHolder = permissionHolder;
  }

}