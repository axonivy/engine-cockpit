package ch.ivyteam.enginecockpit.services.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.model.WebServiceClientAuthTypeCalcuator;
import ch.ivyteam.util.Property;

class TestWebServiceClientAuthTypeCalcuator {

  @Test
  void get() {
    var features = List.of("ch.ivyteam.ivy.webservice.exec.cxf.feature.NTLMAuthenticationFeature");
    var properties = List.of(new Property("authType", "test"));
    var authType = new WebServiceClientAuthTypeCalcuator(features, properties).get();
    assertThat(authType).isEqualTo("NTLM");
  }

  @Test
  void get_custom() {
    var features = List.of("com.axonivy.webservice.NewNTLMAuthenticationFeature");
    var properties = List.of(new Property("other", "test"));
    var authType = new WebServiceClientAuthTypeCalcuator(features, properties).get();
    assertThat(authType).isEmpty();
  }
  
  @Test
  void get_custom_authType() {
    var features = List.of("com.axonivy.webservice.NewNTLMAuthenticationFeature");
    var properties = List.of(new Property("authType", "myAuth"));
    var authType = new WebServiceClientAuthTypeCalcuator(features, properties).get();
    assertThat(authType).isEqualTo("myAuth");
  }
  
  @Test
  void get_custom_noFeatures() {
    var features = new ArrayList<String>();
    var properties = List.of(new Property("authType", "myAuth"));
    var authType = new WebServiceClientAuthTypeCalcuator(features, properties).get();
    assertThat(authType).isEqualTo("myAuth");
  }
}
