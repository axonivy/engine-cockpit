package ch.ivyteam.enginecockpit.model;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.application.restricted.rest.IRestClient;
import ch.ivyteam.util.Property;

@SuppressWarnings("restriction")
public class RestClient
{
  private String name;
  private String url;
  private String description;
  private List<Property> properties;
  private List<String> features;
  private UUID uniqueId;

  public RestClient(IRestClient client)
  {
    name = client.getName();
    url = client.getUri();
    description = client.getDescription();
    uniqueId = client.getUniqueId();
    properties = client.getProperties().stream().map(p -> new Property(p.getName(), p.getValue())).collect(Collectors.toList());
    features = client.getFeatures().stream().map(f -> f.getClazz()).collect(Collectors.toList());
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
    return properties.stream().filter(p -> StringUtils.equals(p.getName(), "username")).map(Property::getValue).findFirst().orElse("");
  }
  
  public String getPassword()
  {
    return properties.stream().anyMatch(p -> StringUtils.equals(p.getName(), "password")) ? "*****" : "";
  }


  public List<Property> getProperties()
  {
    return properties;
  }
  
  public List<String> getFeatures()
  {
    return features;
  }
  
}
