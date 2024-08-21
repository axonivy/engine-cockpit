package ch.ivyteam.enginecockpit.services.model;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.application.config.Meta;
import ch.ivyteam.ivy.configuration.restricted.ConfigValueFormat;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.rest.client.config.restricted.ClientProperties;

@SuppressWarnings("restriction")
public class RestClientDto implements IService {
  private String name;
  private String url;
  private String description;
  private Map<String, String> properties;
  private String password;
  private String username;
  private List<String> features;
  private UUID uniqueId;
  private boolean passwordChanged;
  private final Map<String, Object> connectionProps;
  private Map<String, Meta> metas;

  public RestClientDto(RestClient client) {
    name = client.name();
    url = client.uri();
    description = client.description();
    uniqueId = client.uniqueId();
    metas = client.metas();
    properties = client.properties();
    password = client.properties().getOrDefault("password", "");
    username = client.properties().getOrDefault("username", "");
    features = client.features();
    passwordChanged = false;
    connectionProps = ClientProperties.clientProps(client.properties());
  }

  public UUID getUniqueId() {
    return uniqueId;
  }

  public String getName() {
    return name;
  }

  public String getViewUrl(String app) {
    return "restclientdetail.xhtml?app=" + app + "&name=" + name;
  }

  public String getUrl() {
    return url;
  }

  public String getConnectionUrl() {
    try {
      return UriBuilder.fromUri(url).resolveTemplates(connectionProps).build().toASCIIString();
    } catch (Exception ex) {
      return url;
    }
  }

  public String getDescription() {
    return description;
  }

  public String getAuthType() {
    return new RestClientAuthTypeCalculator(features).get();
  }

  public String getUsername() {
    return username;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public boolean passwordChanged() {
    return passwordChanged;
  }

  public Map<String, String> getProperties() {
      return properties.entrySet().stream()
          .collect(Collectors.toMap(entry -> entry.getKey(), entry -> {
        var meta = metas.get(entry.getKey());
        if (meta.format() == ConfigValueFormat.PASSWORD) {
          return "******";
        }
        return entry.getValue();
      }));
    }

  public List<String> getFeatures() {
    return features;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setPassword(String password) {
    if (StringUtils.isNotBlank(password)) {
      this.password = password;
      passwordChanged = true;
    }
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
