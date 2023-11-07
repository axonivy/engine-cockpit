package ch.ivyteam.enginecockpit.services.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.model.RestClientAuthTypeCalculator;

class TestRestClientAuthTypeCalculator {

  @Test
  void get() {
    var features = List.of("ch.ivyteam.ivy.rest.client.authentication.NtlmAuthenticationFeature");
    var authType = new RestClientAuthTypeCalculator(features).get();
    assertThat(authType).isEqualTo("Ntlm");
  }

  @Test
  void get_custom() {
    var features = List.of("com.axonivy.authentication.NewNTLMFeature");
    var authType = new RestClientAuthTypeCalculator(features).get();
    assertThat(authType).isEmpty();
  }
}
