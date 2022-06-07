package ch.ivyteam.enginecockpit.testdata.user;

import java.util.UUID;

import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.exec.Sudo;

public class TestDataUser {
  public static void createDisabledUser() {
    Sudo.run(TestDataUser::newDisabledUser);
  }

  public static void create200kUsers() {
    Sudo.run(TestDataUser::new200kUsers);
  }

  public static void cleanup200kUsers() {
    Sudo.run(TestDataUser::delete200kUsers);
  }

  private static void newDisabledUser() {
    var app = IApplicationRepository.instance().findByName("test").orElse(null);
    var user = app.getSecurityContext().users().find("disableduser");
    if (user == null) {
      user = app.getSecurityContext().users().create("disableduser");
    }
    user.disable();
  }

  private static void new200kUsers() {
    var app = IApplicationRepository.instance().create("200kUsers");
    var userRepo = app.getSecurityContext().users();
    for (var i = 1; i <= 200000; i++) {
      if (i % 10000 == 0) {
        Ivy.log().info(i + " Users created");
      }
      var user = userRepo.create("testUser-" + i);
      user.setFullName(UUID.randomUUID().toString());
    }
  }

  private static void delete200kUsers() {
    IApplicationRepository.instance().delete("200kUsers");
  }
}
