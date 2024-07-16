package ch.ivyteam.enginecockpit.security.export.sheets;

import java.util.List;

import ch.ivyteam.ivy.security.IPermission;
import ch.ivyteam.ivy.security.IPermissionAccess;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityMember;

public class PermissionShortcut {
  private List<IPermissionAccess> permissionChecks;

  public PermissionShortcut(ISecurityContext securityContext, ISecurityMember member) {
    permissionChecks = securityContext.securityDescriptor().getPermissionAccesses(member);
  }

  public String getPermissionShortcut(IPermission permission) {
    var permissionCheck = permissionChecks.stream().filter(check->check.getPermission().equals(permission)).findAny().orElseThrow();
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
