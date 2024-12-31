package ch.ivyteam.enginecockpit.security.export.sheets;

import java.util.HashMap;
import java.util.Map;

import ch.ivyteam.ivy.security.AccessState;
import ch.ivyteam.ivy.security.IPermission;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityMember;

public class PermissionShortcut {
  private final Map<IPermission, AccessState> accessStates = new HashMap<>();

  public PermissionShortcut(ISecurityContext securityContext, ISecurityMember member) {
    for (var checks : securityContext.securityDescriptor().getPermissionAccesses(member)) {
      accessStates.put(checks.getPermission(), checks.getAccessState());
    }
  }

  public String getPermissionShortcut(IPermission permission) {
    var accessState = accessStates.get(permission);
    if (accessState.isGranted()) {
      if (accessState.isExplicit()) {
        return "G";
      } else {
        return "g";
      }
    } else if (accessState.isDenied()) {
      if (accessState.isExplicit()) {
        return "D";
      } else {
        return "d";
      }
    }
    return "";
  }
}
