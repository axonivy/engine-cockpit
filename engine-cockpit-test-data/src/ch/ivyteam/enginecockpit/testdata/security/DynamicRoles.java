package ch.ivyteam.enginecockpit.testdata.security;

import java.util.UUID;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.exec.Sudo;

public class DynamicRoles
{
  public static void createRoles()
  {
    Sudo.run(() -> {
      IRole everybody = Ivy.wf().getApplication().getSecurityContext().findRole("Everybody");
      for (var i = 0; i < 110; i++)
      {
        everybody.createChildRole("role-" + UUID.randomUUID(), "dynamic role", "dynamic role", true);
      }
    });
  }
}
