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
    assertThat(getPropertyValue(uiStateClient, "jersey.config.client.connectTimeout")).isEqualTo("2000");
  }

  @Test
  void modifyTimeout_keepExistingProperty() {
    var restClient = RestClient.create("").property("User", "Fritz").toRestClient();
    var client = new UiStateClient(restClient);
    client.setTimeout(TimeUnit.SECONDS, 2);
    var uiStateClient = client.toClient();
    assertThat(getPropertyValue(uiStateClient, "jersey.config.client.connectTimeout")).isEqualTo("2000");
    assertThat(getPropertyValue(uiStateClient, "User")).isEqualTo("Fritz");
  }

  @Test
  void modifyTimeout_existingTimeout() {
    var restClient = RestClient.create("").property("jersey.config.client.connectTimeout", "3000").toRestClient();
    var client = new UiStateClient(restClient);
    client.setTimeout(TimeUnit.SECONDS, 2);
    var uiStateClient = client.toClient();
    uiStateClient.properties();
    assertThat(getPropertyValueTime(uiStateClient, "jersey.config.client.connectTimeout")).isEqualTo("2000");
    assertThat(getPropertyValue(restClient, "jersey.config.client.connectTimeout")).isEqualTo("3000");
  }

  @Test
  void modifyReadTimeout() {
    var restClient = RestClient.create("").toRestClient();
    var client = new UiStateClient(restClient);
    client.setReadTimeout(TimeUnit.SECONDS, 2);
    var uiStateClient = client.toClient();
    assertThat(getPropertyValue(uiStateClient, "jersey.config.client.readTimeout")).isEqualTo("2000");
  }

  @Test
  void modifyReadTimeout_keepExistingProperty() {
    var restClient = RestClient.create("").property("User", "Fritz").toRestClient();
    var client = new UiStateClient(restClient);
    client.setReadTimeout(TimeUnit.SECONDS, 2);
    var uiStateClient = client.toClient();
    assertThat(getPropertyValue(uiStateClient, "jersey.config.client.readTimeout")).isEqualTo("2000");
    assertThat(getPropertyValue(uiStateClient, "User")).isEqualTo("Fritz");
  }

  @Test
  void modifyReadTimeout_existingTimeout() {
    var restClient = RestClient.create("").property("jersey.config.client.readTimeout", "3000").toRestClient();
    var client = new UiStateClient(restClient);
    client.setReadTimeout(TimeUnit.SECONDS, 2);
    var uiStateClient = client.toClient();
    assertThat(getPropertyValueTime(uiStateClient, "jersey.config.client.readTimeout")).isEqualTo("2000");
    assertThat(getPropertyValue(restClient, "jersey.config.client.readTimeout")).isEqualTo("3000");
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
    assertThat(getPropertyValue(uiStateClient, "username")).isEqualTo("Fritz");
    assertThat(getPropertyValue(uiStateClient, "password")).isEqualTo("password");
    assertThat(uiStateClient.uri()).isEqualTo("https://url.com");
  }
  
  private String getPropertyValueTime(RestClient client, String key) {
    return client.properties().stream()
            .filter(p -> p.key().equals(key))
            .filter(p -> p.value().equals("2000"))
            .findAny()
            .get()
            .value();
  }
  
  private String getPropertyValue(RestClient client, String key) {
    return client.properties().stream()
            .filter(p -> p.key().equals(key))
            .findAny()
            .get()
            .value();
  }
}
