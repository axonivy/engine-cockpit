package ch.ivyteam.enginecockpit.model;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.application.restricted.rest.IRestClient;
import ch.ivyteam.util.Property;

@SuppressWarnings("restriction")
public class RestClient implements IService
{
  private String name;
  private String url;
  private String description;
  private List<Property> properties;
  private String password;
  private String username;
  private List<String> features;
  private UUID uniqueId;
  private boolean passwordChanged;

  public RestClient(IRestClient client)
  {
    name = client.getName();
    url = client.getUri();
    description = client.getDescription();
    uniqueId = client.getUniqueId();
    properties = client.getProperties().stream().map(p -> new Property(p.getName(), p.getValue())).collect(Collectors.toList());
    password = properties.stream().filter(p -> StringUtils.equals(p.getName(), "password")).findAny().map(p -> p.getValue()).orElse("");
    username = properties.stream().filter(p -> StringUtils.equals(p.getName(), "username")).findAny().map(p -> p.getValue()).orElse("");
    features = client.getFeatures().stream().map(f -> f.getClazz()).collect(Collectors.toList());
    passwordChanged = false;
  }
  
  public UUID getUniqueId()
  {
    return uniqueId;
  }

  public String getName()
  {
    return name;
  }

  public String getUrl()
  {
    return url;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public String getAuthType()
  {
    return features.stream().filter(f -> StringUtils.contains(f, "authentication"))
            .map(f -> StringUtils.substringBetween(f, "authentication.", "AuthenticationFeature")).findFirst().orElse("");
  }
  
  public String getUsername()
  {
    return username;
  }
  
  @Override
  public String getPassword()
  {
    return password;
  }

  @Override
  public boolean passwordChanged()
  {
    return passwordChanged;
  }

  public List<Property> getProperties()
  {
    return properties;
  }
  
  public List<String> getFeatures()
  {
    return features;
  }

  public void setUrl(String url)
  {
    this.url = url;
  }

  public void setPassword(String password)
  {
    if (StringUtils.isNotBlank(password))
    {
      this.password = password;
      passwordChanged = true;
    }
  }
  
  public void setUsername(String username)
  {
    this.username = username;
  }
  
}
