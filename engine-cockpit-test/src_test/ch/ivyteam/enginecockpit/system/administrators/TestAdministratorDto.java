package ch.ivyteam.enginecockpit.system.administrators;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import ch.ivyteam.ivy.security.administrator.Administrator;

class TestAdministratorDto {

  @Test
  void externalFlagReflectsSourceAdmin() {
    var externalAdmin = Administrator.create()
        .username("externalAdmin")
        .externalId("external-id")
        .language(Locale.GERMAN)
        .formattingLanguage(Locale.of("de", "CH"))
        .toAdministrator();

    var dto = new AdministratorDto(externalAdmin);
    var roundTripped = dto.toAdministrator();

    assertThat(dto.isExternal()).isTrue();
    assertThat(roundTripped.username()).isEqualTo("externalAdmin");
    assertThat(roundTripped.language()).isEqualTo(Locale.GERMAN);
    assertThat(roundTripped.formattingLanguage()).isEqualTo(Locale.of("de", "CH"));
  }
}
