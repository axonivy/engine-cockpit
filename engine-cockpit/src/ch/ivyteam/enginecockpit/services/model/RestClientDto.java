package ch.ivyteam.enginecockpit.services.model;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.commons.Property;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.rest.client.config.restricted.ClientProperties;

@SuppressWarnings("restriction")
public class RestClientDto implements IService {
  private String name;
  private String url;
  private String description;
  private List<Property> properties;
  private String password;
  private String username;
  private List<String> features;
  private UUID uniqueId;
  private boolean passwordChanged;
  private final Map<String, Object> connectionProps;

  public RestClientDto(RestClient client) {
    name = client.name();
    url = client.uri();
    description = client.description();
    uniqueId = client.uniqueId();
    var metas = client.metas();
    properties = client.properties().entrySet().stream()
            .map(p -> new Property(p.getKey(), p.getValue(), metas.get(p.getKey())))
            .collect(Collectors.toList());
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

  public String getViewUrl(String app, String env) {
    return "restclientdetail.xhtml?app=" + app + "&env=" + env + "&name=" + name;
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
    return features.stream()
            .filter(f -> StringUtils.contains(f, "authentication"))
            .map(f -> StringUtils.substringBetween(f, "authentication.", "AuthenticationFeature"))
            .findFirst()
            .orElse("");
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

  public List<Property> getProperties() {
    return properties;
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
