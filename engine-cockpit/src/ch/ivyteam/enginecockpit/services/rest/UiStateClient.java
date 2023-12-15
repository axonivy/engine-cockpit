package ch.ivyteam.enginecockpit.services.rest;

import ch.ivyteam.enginecockpit.services.model.RestClientDto;
import ch.ivyteam.ivy.rest.client.RestClient;

public class UiStateClient {

  private static final String REST_PROP_USERNAME = "username";
  private static final String REST_PROP_PASSWORD = "password";

  public static RestClient toUiStateClient(RestClient original, RestClientDto dto) {
    var restBuilder = original.toBuilder()
            .uri(dto.getConnectionUrl())
            .property(REST_PROP_USERNAME, dto.getUsername());
    if (dto.passwordChanged()) {
      restBuilder.property(REST_PROP_PASSWORD, dto.getPassword());
    }
    return restBuilder.toRestClient();
  }

}
