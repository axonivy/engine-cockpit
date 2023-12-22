package ch.ivyteam.enginecockpit.services.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.services.model.RestClientDto;
import ch.ivyteam.ivy.rest.client.RestClient;

class TestUiStateClient {

  @Test
  void modifyTimeout() {
    var restClient = RestClient.create("").toRestClient();
    var client = new UiStateClient(restClient);
    client.setTimeout(TimeUnit.SECONDS, 2);
    var uiStateClient = client.toClient();
    assertThat(uiStateClient.properties().get("jersey.config.client.connectTimeout")).isEqualTo("2000");
  }

  @Test
  void modifyTimeout_keepExistingProperty() {
    var restClient = RestClient.create("").property("User", "Fritz").toRestClient();
    var client = new UiStateClient(restClient);
    client.setTimeout(TimeUnit.SECONDS, 2);
    var uiStateClient = client.toClient();
    assertThat(uiStateClient.properties().get("jersey.config.client.connectTimeout")).isEqualTo("2000");
    assertThat(uiStateClient.properties().get("User")).isEqualTo("Fritz");
  }

  @Test
  void modifyTimeout_existingTimeout() {
    var restClient = RestClient.create("").property("jersey.config.client.connectTimeout", "3000").toRestClient();
    var client = new UiStateClient(restClient);
    client.setTimeout(TimeUnit.SECONDS, 2);
    var uiStateClient = client.toClient();
    assertThat(uiStateClient.properties().get("jersey.config.client.connectTimeout")).isEqualTo("2000");
    assertThat(restClient.properties().get("jersey.config.client.connectTimeout")).isEqualTo("3000");
  }

  @Test
  void modifyRestDto() {
    var restClient = RestClient.create("").toRestClient();
    var client = new UiStateClient(restClient);
    var dto = new RestClientDto(restClient);
    dto.setUrl("https://url.com");
    dto.setPassword("password");
    dto.setUsername("Fritz");
    client.setUiState(dto);

    var uiStateClient = client.toClient();
    assertThat(uiStateClient.properties().get("username")).isEqualTo("Fritz");
    assertThat(uiStateClient.properties().get("password")).isEqualTo("password");
    assertThat(uiStateClient.uri()).isEqualTo("https://url.com");
  }
}
