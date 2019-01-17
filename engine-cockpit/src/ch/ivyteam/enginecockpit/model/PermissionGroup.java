package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.ivy.security.IPermissionGroup;
import ch.ivyteam.ivy.security.IPermissionGroupAccess;

public class PermissionGroup extends Permission
{
  private boolean anyGrant;
  private boolean anyDeny;

  public PermissionGroup(IPermissionGroup group, IPermissionGroupAccess groupAccess)
  {
    super(group.getName(),
            group.getId(),
            true,
            groupAccess.isGrantedAllPermissions(),
            groupAccess.isDeniedAllPermissions());
    this.anyDeny = groupAccess.isDeniedAnyPermission();
    this.anyGrant = groupAccess.isGrantedAnyPermission();
  }

  @Override
  public boolean isGrantDisabled()
  {
    return false;
  }

  @Override
  public boolean isUnGrantDisabled()
  {
    return isDeny() || !anyGrant;
  }

  @Override
  public boolean isDenyDisabled()
  {
    return false;
  }

  @Override
  public boolean isUnDenyDisabled()
  {
    return isGrant() || !anyDeny;
  }
  
  public void setAnyDeny(boolean anyDeny)
  {
    this.anyDeny = anyDeny;
  }
  
  public void setAnyGrant(boolean anyGrant)
  {
    this.anyGrant = anyGrant;
  }
  
}
