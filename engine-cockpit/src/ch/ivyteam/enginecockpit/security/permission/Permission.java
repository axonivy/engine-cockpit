package ch.ivyteam.enginecockpit.security.permission;

import java.util.Optional;

import ch.ivyteam.ivy.security.IPermission;
import ch.ivyteam.ivy.security.IPermissionAccess;

public class Permission extends AbstractPermission
{
  private boolean explicit;
  private String permissionHolder;
  private IPermission iPermission;
  private PermissionBean bean;

  public Permission(IPermissionAccess access, String path, PermissionBean bean)
  {
    super(access.getPermission().getName(),
            path,
            access.getPermission().getId(),
            access.isGranted(),
            access.isDenied());
    this.explicit = access.isExplicit();
    this.permissionHolder = Optional.ofNullable(access.getPermissionHolder()).map(r -> r.getName()).orElse(null);
    this.iPermission = access.getPermission();
    this.bean = bean;
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

  public void setPermissionHolder(String permissionHolder)
  {
    this.permissionHolder = permissionHolder;
  }

  @Override
  public boolean isGrantDisabled()
  {
    return explicit && isGrant();
  }

  @Override
  public boolean isUnGrantDisabled()
  {
    return !isGrantDisabled();
  }

  @Override
  public boolean isDenyDisabled()
  {
    return explicit && isDeny();
  }

  @Override
  public boolean isUnDenyDisabled()
  {
    return !isDenyDisabled();
  }

  @Override
  public boolean isSomeGrant()
  {
    return false;
  }

  @Override
  public boolean isSomeDeny()
  {
    return false;
  }

  @Override
  public boolean isGroup()
  {
    return false;
  }

  @Override
  public void grant()
  {
    bean.getSecurityDescriptor().grantPermission(iPermission, bean.getSecurityMember());
    bean.reSetRootPermissionGroup();
  }

  @Override
  public void ungrant()
  {
    bean.getSecurityDescriptor().ungrantPermission(iPermission, bean.getSecurityMember());
    bean.reSetRootPermissionGroup();
  }

  @Override
  public void deny()
  {
    bean.getSecurityDescriptor().denyPermission(iPermission, bean.getSecurityMember());
    bean.reSetRootPermissionGroup();
  }

  @Override
  public void undeny()
  {
    bean.getSecurityDescriptor().undenyPermission(iPermission, bean.getSecurityMember());
    bean.reSetRootPermissionGroup();
  }

}
