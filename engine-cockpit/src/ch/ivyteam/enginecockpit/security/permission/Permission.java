package ch.ivyteam.enginecockpit.security.permission;

import java.util.Optional;

import ch.ivyteam.ivy.security.IPermission;
import ch.ivyteam.ivy.security.IPermissionAccess;

public class Permission extends AbstractPermission {
  private boolean explicit;
  private String permissionHolder;
  private final IPermission permission;
  private final PermissionBean bean;

  public Permission(IPermissionAccess access, PermissionBean bean) {
    super(access.getPermission().getName(),
            access.isGranted(),
            access.isDenied());
    this.explicit = access.isExplicit();
    this.permissionHolder = Optional.ofNullable(access.getPermissionHolder()).map(r -> r.getName())
            .orElse(null);
    this.permission = access.getPermission();
    this.bean = bean;
  }

  public void setExplicit(boolean explicit) {
    this.explicit = explicit;
  }

  public boolean isExplicit() {
    return explicit;
  }

  public String getPermissionHolder() {
    return permissionHolder;
  }

  public void setPermissionHolder(String permissionHolder) {
    this.permissionHolder = permissionHolder;
  }

  @Override
  public boolean isGrantDisabled() {
    return explicit && isGrant();
  }

  @Override
  public boolean isUnGrantDisabled() {
    return !isGrantDisabled();
  }

  @Override
  public boolean isDenyDisabled() {
    return explicit && isDeny();
  }

  @Override
  public boolean isUnDenyDisabled() {
    return !isDenyDisabled();
  }

  @Override
  public boolean isSomeGrant() {
    return false;
  }

  @Override
  public boolean isSomeDeny() {
    return false;
  }

  @Override
  public boolean isGroup() {
    return false;
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

  public IPermission permission() {
    return permission;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || ! obj.getClass().equals(Permission.class)) {
      return false;
    }
    var other = (Permission)obj;
    return permission.equals(other.permission);
  }

  @Override
  public int hashCode() {
    return permission.hashCode();
  }
}
