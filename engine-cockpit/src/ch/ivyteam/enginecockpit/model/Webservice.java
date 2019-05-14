package ch.ivyteam.enginecockpit.model;

import java.util.List;
import java.util.stream.Collectors;

import ch.ivyteam.ivy.application.restricted.IWebService;
import ch.ivyteam.util.Property;

@SuppressWarnings("restriction")
public class Webservice
{
  private String name;
  private String genId;
  private String description;
  private String wsdlUrl;
  private List<String> features;
  private List<Property> properties;
  
  public Webservice(IWebService webservice)
  {
    name = webservice.getName();
    description = webservice.getDescription();
    wsdlUrl = webservice.getWsdlUrl();
    properties = webservice.getProperties().stream().map(p -> new Property(p.getName(), p.getValue())).collect(Collectors.toList());
    features = webservice.getFeatures().stream().map(f -> f.getClazz()).collect(Collectors.toList());
    genId = webservice.getGenerationIdentifier();
  }

  public String getName()
  {
    return name;
  }

  public String getGenId()
  {
    return genId;
  }
  
  public String getDescription()
  {
    return description;
  }

  public String getWsdlUrl()
  {
    return wsdlUrl;
  }

  public List<String> getFeatures()
  {
    return features;
  }

  public List<Property> getProperties()
  {
    return properties;
  }
  
}
