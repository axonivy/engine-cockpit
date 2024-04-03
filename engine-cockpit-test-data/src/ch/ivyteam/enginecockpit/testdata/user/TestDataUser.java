package ch.ivyteam.enginecockpit.testdata.user;

import java.util.UUID;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISecurityContextRepository;
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
    var security = ISecurityContextRepository.instance().getDefault();
    var user = security.users().find("disableduser");
    if (user == null) {
      user = security.users().create("disableduser");
    }
    user.disable();
  }

  private static void new200kUsers() {
    var security = ISecurityContextRepository.instance().create("200kUsers");
    var userRepo = security.users();
    for (var i = 1; i <= 200000; i++) {
      if (i % 10000 == 0) {
        Ivy.log().info(i + " Users created");
      }
      var user = userRepo.create("testUser-" + i);
      user.setFullName(UUID.randomUUID().toString());
    }
  }

  private static void delete200kUsers() {
    ISecurityContextRepository.instance().delete("200kUsers");
  }
}
