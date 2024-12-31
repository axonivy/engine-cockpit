package ch.ivyteam.enginecockpit.services.rest;

import java.util.concurrent.TimeUnit;

import ch.ivyteam.enginecockpit.services.model.RestClientDto;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.rest.client.RestClient.Builder;

public class UiStateClient {

  private final Builder builder;

  public UiStateClient(RestClient original) {
    builder = original.toBuilder();
  }

  @SuppressWarnings("restriction")
  public UiStateClient setUiState(RestClientDto dto) {
    builder.uri(dto.getConnectionUrl())
        .property(ch.ivyteam.ivy.rest.client.config.RestClientProperty.Authentication.USERNAME, dto.getUsername());
    if (dto.passwordChanged()) {
      builder.property(ch.ivyteam.ivy.rest.client.config.RestClientProperty.Authentication.PASSWORD, dto.getPassword());
    }
    return this;
  }

  public UiStateClient setTimeout(TimeUnit unit, int amount) {
    builder.property("jersey.config.client.connectTimeout", String.valueOf(unit.toMillis(amount)));
    return this;
  }

  public UiStateClient setReadTimeout(TimeUnit unit, int amount) {
    builder.property("jersey.config.client.readTimeout", String.valueOf(unit.toMillis(amount)));
    return this;
  }

  public RestClient toClient() {
    return builder.toRestClient();
  }

}
