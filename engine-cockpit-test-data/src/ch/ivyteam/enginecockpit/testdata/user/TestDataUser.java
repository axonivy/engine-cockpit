package ch.ivyteam.enginecockpit.testdata.user;

import ch.ivyteam.ivy.application.IApplicationConfigurationManager;
import ch.ivyteam.ivy.security.exec.Sudo;

public class TestDataUser
{
  public static void createDisabledUser()
  {
    Sudo.exec(TestDataUser::newDisabledUser);
  }

  private static void newDisabledUser()
  {
    var app = IApplicationConfigurationManager.instance().findApplication("test");
    var user = app.getSecurityContext().users().find("disableduser");
    if (user == null)
    {
      user = app.getSecurityContext().users().create("disableduser");
    }
    user.disable();
  }
}
