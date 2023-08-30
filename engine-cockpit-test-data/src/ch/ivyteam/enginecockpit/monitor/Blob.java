package ch.ivyteam.enginecockpit.monitor;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISecurityContextRepository;
import ch.ivyteam.ivy.security.exec.Sudo;
import ch.ivyteam.ivy.security.restricted.di.SessionContext;

public class Blob {

  public static void create() {
    var ctx = ISecurityContextRepository.instance().getDefault();
    var session = ctx.sessions().create();
    var user = ctx.users().query().executor().firstResult();
    session.authenticateSessionUser(user, "test");
    new SessionContext(session).runInContext(() -> {
      Sudo.run(() -> Ivy.wfCase().documents().add("louis.txt").write().withContentFrom("louis schreibt man mit s"));
    });
  }
}
