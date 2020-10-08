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

  public PermissionGroup(IPermissionGroupAccess groupAccess, String path, PermissionBean bean)
  {
    super(groupAccess.getPermissionGroup().getName(),
            path,
            groupAccess.getPermissionGroup().getId(),
            groupAccess.isGrantedAllPermissions(),
            groupAccess.isDeniedAllPermissions());
    this.someDeny = groupAccess.isDeniedAnyPermission();
    this.someGrant = groupAccess.isGrantedAnyPermission();
    this.bean = bean;
    this.permissionGroup = groupAccess.getPermissionGroup();
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
    bean.getSecurityDescriptor().grantPermissions(permissionGroup, bean.getSecurityMember());
    bean.reSetRootPermissionGroup();
  }

  @Override
  public void ungrant()
  {
    bean.getSecurityDescriptor().ungrantPermissions(permissionGroup, bean.getSecurityMember());
    bean.reSetRootPermissionGroup();
  }

  @Override
  public void deny()
  {
    bean.getSecurityDescriptor().denyPermissions(permissionGroup, bean.getSecurityMember());
    bean.reSetRootPermissionGroup();
  }

  @Override
  public void undeny()
  {
    bean.getSecurityDescriptor().undenyPermissions(permissionGroup, bean.getSecurityMember());
    bean.reSetRootPermissionGroup();
  }
  
}
