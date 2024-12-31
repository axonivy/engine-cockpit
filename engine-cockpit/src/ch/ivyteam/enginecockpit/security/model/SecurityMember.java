package ch.ivyteam.enginecockpit.security.model;

import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.ISecurityMember;
import ch.ivyteam.ivy.security.IUser;

public interface SecurityMember {
  String getName();
  String getCssIconClass();
  String getViewUrl();

  static SecurityMember createFor(ISecurityMember securityMember) {
    if (securityMember.isUser()) {
      return new User((IUser) securityMember);
    } else {
      return new Role((IRole) securityMember);
    }
  }
}
