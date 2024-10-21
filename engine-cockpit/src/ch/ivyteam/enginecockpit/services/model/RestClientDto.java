package ch.ivyteam.enginecockpit.services.model;

import java.util.HashMap;
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
    properties = client.properties().stream()
            .map(p -> new Property(p.key(), p.value(), metas.get(p.key()), p.isDefault()))
            .collect(Collectors.toList());
    password = properties.stream().filter(p -> StringUtils.equals(p.getName(), "password"))
        .map(p -> p.getValue()).findFirst().orElse("");
    username = properties.stream().filter(p -> StringUtils.equals(p.getName(), "username"))
        .map(p -> p.getValue()).findFirst().orElse("");
    features = client.features();
    passwordChanged = false;
    var propMap = new HashMap<String,String>();
    for (ch.ivyteam.ivy.rest.client.RestClientProperty prop : client.properties()) {
        propMap.put(prop.key(), prop.value());
    }
    connectionProps = ClientProperties.clientProps(propMap);
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
