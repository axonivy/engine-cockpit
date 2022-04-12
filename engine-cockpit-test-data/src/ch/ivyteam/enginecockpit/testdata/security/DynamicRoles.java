package ch.ivyteam.enginecockpit.testdata.security;

import java.util.UUID;

import ch.ivyteam.ivy.application.IApplicationConfigurationManager;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.exec.Sudo;
import ch.ivyteam.ivy.security.role.NewRole;

public class DynamicRoles {

  public static void createRoles() {
    Sudo.run(() -> {
      var roles = context().roles();
      for (var i = 0; i < 110; i++) {
        var newRole = NewRole.create("role-" + UUID.randomUUID())
          .displayName("dynamic role")
          .toNewRole();
        roles.create(newRole);
      }
    });
  }

  public static void cleanupRoles() {
    Sudo.run(() -> {
      for (var child : getEverybody().getChildRoles()) {
        if (child.isDynamic()) {
          child.delete();
        }
      }
    });
  }

  private static IRole getEverybody() {
    return context().roles().topLevel();
  }

  private static ISecurityContext context() {
    return IApplicationConfigurationManager.instance()
              .findApplication("test")
              .getSecurityContext();
  }
}
