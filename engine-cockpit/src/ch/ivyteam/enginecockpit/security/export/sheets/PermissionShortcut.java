package ch.ivyteam.enginecockpit.security.export.sheets;

import ch.ivyteam.ivy.security.IPermission;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityMember;

public class PermissionShortcut {
  private final ISecurityContext securityContext;

  public PermissionShortcut(ISecurityContext securityContext) {
    this.securityContext = securityContext;
  }

  public String getPermissionShortcut(IPermission permission, ISecurityMember member) {
    var permissionCheck = securityContext.securityDescriptor().getPermissionAccess(permission, member);
    if(permissionCheck.isGranted()) {
      if(permissionCheck.isExplicit()) {
        return "G";
      }
      else {
        return "g";
      }
    }
    else if(permissionCheck.isDenied()) {
      if(permissionCheck.isExplicit()) {
        return "D";
      }
      else {
        return "d";
      }
    }
    return "";
  }
}
