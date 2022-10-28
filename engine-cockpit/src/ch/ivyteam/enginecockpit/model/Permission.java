package ch.ivyteam.enginecockpit.model;

import java.util.Optional;

import ch.ivyteam.enginecockpit.security.PermissionBean;
import ch.ivyteam.ivy.security.IPermission;
import ch.ivyteam.ivy.security.IPermissionAccess;

public class Permission extends AbstractPermission
{
  private boolean explicit;
  private String permissionHolder;
  private IPermission iPermission;
  private PermissionBean bean;

  public Permission(IPermissionAccess access, PermissionBean bean)
  {
    super(access.getPermission().getName(),
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
    bean.grant(iPermission);
  }

  @Override
  public void ungrant()
  {
    bean.ungrant(iPermission);
  }

  @Override
  public void deny()
  {
    bean.deny(iPermission);
  }

  @Override
  public void undeny()
  {
    bean.undeny(iPermission);
  }
  
  public IPermission permission() 
  {
    return iPermission;
  }

}