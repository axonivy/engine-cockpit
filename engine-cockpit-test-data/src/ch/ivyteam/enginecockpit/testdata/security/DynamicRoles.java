package ch.ivyteam.enginecockpit.testdata.security;

import java.util.UUID;

import ch.ivyteam.ivy.application.IApplicationConfigurationManager;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.exec.Sudo;

public class DynamicRoles {
  public static void createRoles() {
    Sudo.exec(() -> {
      for (var i = 0; i < 110; i++) {
        getEverybody().createChildRole("role-" + UUID.randomUUID(), "dynamic role", "dynamic role", true);
      }
    });
  }

  public static void cleanupRoles() {
    Sudo.exec(() -> {
      for (IRole child : getEverybody().getChildRoles()) {
        if (child.isDynamic()) {
          child.delete();
        }
      }
    });
  }

  private static IRole getEverybody() {
    return IApplicationConfigurationManager.instance().findApplication("test").getSecurityContext()
            .findRole("Everybody");
  }
}
