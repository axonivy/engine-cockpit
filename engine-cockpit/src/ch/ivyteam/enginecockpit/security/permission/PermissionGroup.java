package ch.ivyteam.enginecockpit.security.permission;

import ch.ivyteam.ivy.security.IPermissionGroup;
import ch.ivyteam.ivy.security.IPermissionGroupAccess;

public class PermissionGroup extends AbstractPermission {
  private boolean someGrant;
  private boolean someDeny;
  private final PermissionBean bean;
  private final IPermissionGroup permissionGroup;

  public PermissionGroup(IPermissionGroupAccess groupAccess, PermissionBean bean) {
    super(groupAccess.getPermissionGroup().getName(),
            groupAccess.isGrantedAllPermissions(),
            groupAccess.isDeniedAllPermissions());
    this.someDeny = groupAccess.isDeniedAnyPermission();
    this.someGrant = groupAccess.isGrantedAnyPermission();
    this.bean = bean;
    this.permissionGroup = groupAccess.getPermissionGroup();
  }

  public PermissionGroup(String dummy) {
    super(dummy, false, false);
    this.permissionGroup = null;
    this.bean = null;
  }

  @Override
  public boolean isGrantDisabled() {
    return false;
  }

  @Override
  public boolean isUnGrantDisabled() {
    return isDeny() || !someGrant;
  }

  @Override
  public boolean isDenyDisabled() {
    return false;
  }

  @Override
  public boolean isUnDenyDisabled() {
    return isGrant() || !someDeny;
  }

  public void setSomeDeny(boolean someDeny) {
    this.someDeny = someDeny;
  }

  public void setSomeGrant(boolean someGrant) {
    this.someGrant = someGrant;
  }

  @Override
  public boolean isSomeGrant() {
    return someGrant;
  }

  @Override
  public boolean isSomeDeny() {
    return someDeny;
  }

  @Override
  public boolean isGroup() {
    return true;
  }

  @Override
  public void grant() {
    bean.grant(this);
  }

  @Override
  public void ungrant() {
    bean.ungrant(this);
  }

  @Override
  public void deny() {
    bean.deny(this);
  }

  @Override
  public void undeny() {
    bean.undeny(this);
  }

  public IPermissionGroup permissionGroup() {
    return permissionGroup;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || ! obj.getClass().equals(PermissionGroup.class)) {
      return false;
    }
    var other = (PermissionGroup)obj;
    return permissionGroup != null &&
           other.permissionGroup != null &&
           permissionGroup.equals(other.permissionGroup);
  }

  @Override
  public int hashCode() {
    return permissionGroup.hashCode();
  }
}
