package ch.ivyteam.enginecockpit.services.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.commons.Feature;

class TestWebServiceClientAuthTypeCalcuator {

  @Test
  void get() {
    var features = List.of(new Feature("ch.ivyteam.ivy.webservice.exec.cxf.feature.NTLMAuthenticationFeature"));
    var authType = new WebServiceClientAuthTypeCalcuator(features).get();
    assertThat(authType).isEqualTo("NTLM");
  }

  @Test
  void get_custom() {
    var features = List.of(new Feature("com.axonivy.webservice.NewNTLMAuthenticationFeature"));
    var authType = new WebServiceClientAuthTypeCalcuator(features).get();
    assertThat(authType).isEmpty();
  }
}
