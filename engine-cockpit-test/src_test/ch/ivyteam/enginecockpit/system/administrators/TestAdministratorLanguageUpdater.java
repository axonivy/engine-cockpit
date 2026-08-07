package ch.ivyteam.enginecockpit.system.administrators;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import ch.ivyteam.ivy.environment.IvyTest;
import ch.ivyteam.ivy.security.ISecurityContextRepository;
import ch.ivyteam.ivy.security.administrator.Administrator;
import ch.ivyteam.ivy.security.administrator.AdministratorService;
import ch.ivyteam.ivy.security.user.NewUser;

@IvyTest
class TestAdministratorLanguageUpdater {

  private static final String EXTERNAL_ADMIN = "ext-admin";
  private static final String LOCAL_ADMIN = "local-admin";

  @Test
  void savesManagedLanguageToSystemUserNotToConfig() {
    var system = ISecurityContextRepository.instance().getSystem();
    system.users().create(NewUser.create(EXTERNAL_ADMIN)
        .fullName("External Admin")
        .mailAddress("ext-admin@ivyteam.ch")
        .language(Locale.ENGLISH)
        .formattingLanguage(Locale.ENGLISH)
        .externalId("ext-4711")
        .externalName("ext-admin")
        .toNewUser());
    var service = AdministratorService.instance();
    var admin = service.db().all().stream()
        .filter(a -> a.username().equals(EXTERNAL_ADMIN))
        .findAny().orElseThrow();
    assertThat(admin.external()).isTrue();
    // language differs from what we will set, so the post-save assertion proves a real change
    assertThat(admin.language()).isEqualTo(Locale.ENGLISH);
    assertThat(admin.formattingLanguage()).isEqualTo(Locale.ENGLISH);

    new AdministratorLanguageUpdater(service).saveLanguage(
        Administrator.create().username(EXTERNAL_ADMIN).language(Locale.GERMAN)
            .formattingLanguage(Locale.GERMAN).toAdministrator(),
        true);

    assertThat(system.users().find(EXTERNAL_ADMIN).getLanguage()).isEqualTo(Locale.GERMAN);
    assertThat(system.users().find(EXTERNAL_ADMIN).getFormattingLanguage()).isEqualTo(Locale.GERMAN);
    // nothing written to ivy.yaml Administrators section
    assertThat(service.config().all()).noneMatch(a -> a.username().equals(EXTERNAL_ADMIN));
  }

  @Test
  void savesLocalLanguageToConfigNotToDatabase() {
    var service = AdministratorService.instance();
    new AdministratorLanguageUpdater(service).saveLanguage(
        Administrator.create().username(LOCAL_ADMIN).password("local-pw").language(Locale.GERMAN)
            .formattingLanguage(Locale.GERMAN).toAdministrator(),
        false);

    assertThat(service.config().all().stream()
        .filter(a -> a.username().equals(LOCAL_ADMIN))
        .findAny().orElseThrow().language()).isEqualTo(Locale.GERMAN);
    service.config().delete(LOCAL_ADMIN); // cleanup: remove the test entry from engine config
  }
}
