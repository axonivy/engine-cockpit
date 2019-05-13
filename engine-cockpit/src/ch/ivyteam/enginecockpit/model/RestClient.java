package ch.ivyteam.enginecockpit.model;

import java.util.List;
import java.util.stream.Collectors;

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
  
  public RestClient(IRestClient client)
  {
    name = client.getName();
    url = client.getUri();
    description = client.getDescription();
    properties = client.getProperties().stream().map(p -> new Property(p.getName(), p.getValue())).collect(Collectors.toList());
    features = client.getFeatures().stream().map(f -> f.getClazz()).collect(Collectors.toList());
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

  public List<Property> getProperties()
  {
    return properties;
  }
  
  public List<String> getFeatures()
  {
    return features;
  }
  
}
