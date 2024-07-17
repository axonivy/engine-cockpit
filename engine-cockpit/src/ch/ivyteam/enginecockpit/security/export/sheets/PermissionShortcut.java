package ch.ivyteam.enginecockpit.security.export.sheets;

import java.util.HashMap;
import java.util.Map;

import ch.ivyteam.ivy.security.AccessState;
import ch.ivyteam.ivy.security.IPermission;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityMember;

public class PermissionShortcut {
  private Map<IPermission, AccessState> permissionChecks = new HashMap<>();

  public PermissionShortcut(ISecurityContext securityContext, ISecurityMember member) {
    for(var checks : securityContext.securityDescriptor().getPermissionAccesses(member)) {
      permissionChecks.put(checks.getPermission(), checks.getAccessState());
    }
  }

  public String getPermissionShortcut(IPermission permission) {
    var permissionCheck = permissionChecks.get(permission);
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
