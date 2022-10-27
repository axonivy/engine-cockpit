package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.enginecockpit.security.PermissionBean;
import ch.ivyteam.ivy.security.IPermissionGroup;
import ch.ivyteam.ivy.security.IPermissionGroupAccess;

public class PermissionGroup extends AbstractPermission
{
  private boolean someGrant;
  private boolean someDeny;
  private PermissionBean bean;
  private IPermissionGroup permissionGroup;

  public PermissionGroup(IPermissionGroupAccess groupAccess, PermissionBean bean)
  {
    super(groupAccess.getPermissionGroup().getName(),
            groupAccess.getPermissionGroup().getId(),
            groupAccess.isGrantedAllPermissions(),
            groupAccess.isDeniedAllPermissions());
    this.someDeny = groupAccess.isDeniedAnyPermission();
    this.someGrant = groupAccess.isGrantedAnyPermission();
    this.bean = bean;
    this.permissionGroup = groupAccess.getPermissionGroup();
  }
  
  public PermissionGroup(String dummy) {
    super(dummy, -1, false, false);
  }

  @Override
  public boolean isGrantDisabled()
  {
    return false;
  }

  @Override
  public boolean isUnGrantDisabled()
  {
    return isDeny() || !someGrant;
  }

  @Override
  public boolean isDenyDisabled()
  {
    return false;
  }

  @Override
  public boolean isUnDenyDisabled()
  {
    return isGrant() || !someDeny;
  }
  
  public void setSomeDeny(boolean someDeny)
  {
    this.someDeny = someDeny;
  }
  
  public void setSomeGrant(boolean someGrant)
  {
    this.someGrant = someGrant;
  }
  
  @Override
  public boolean isSomeGrant()
  {
    return someGrant;
  }
  
  @Override
  public boolean isSomeDeny()
  {
    return someDeny;
  }

  @Override
  public boolean isGroup()
  {
    return true;
  }

  @Override
  public void grant()
  {
    bean.grant(permissionGroup);
  }

  @Override
  public void ungrant()
  {
    bean.ungrant(permissionGroup);
  }

  @Override
  public void deny()
  {
    bean.deny(permissionGroup);
  }

  @Override
  public void undeny()
  {
    bean.undeny(permissionGroup);
  }
  
  public IPermissionGroup permissionGroup() 
  {
    return permissionGroup;
  }
  
}
