package ch.ivyteam.enginecockpit.model;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

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
  private TreeNode portTypes = new DefaultTreeNode("PortTypes", null);
  
  public Webservice(IWebService webservice)
  {
    name = webservice.getName();
    description = webservice.getDescription();
    wsdlUrl = webservice.getWsdlUrl();
    properties = webservice.getProperties().stream().map(p -> new Property(p.getName(), p.getValue())).collect(Collectors.toList());
    features = webservice.getFeatures().stream().map(f -> f.getClazz()).collect(Collectors.toList());
    genId = webservice.getGenerationIdentifier();
    
    webservice.getPortTypes()
            .forEach(p -> {
              TreeNode portType = new DefaultTreeNode(p.getPortType(), portTypes);
              portType.setExpanded(true);
              webservice.getWebServiceEndpoints(p.getPortType()).forEach(e -> new DefaultTreeNode("- " + e.getEndpointAddress(), portType));
            });
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
  
  public String getAuthType()
  {
    return features.stream().filter(f -> StringUtils.contains(f, "AuthenticationFeature"))
            .map(f -> StringUtils.substringBetween(f, "cxf.feature.", "AuthenticationFeature"))
            .findFirst().orElseGet(() -> properties.stream()
                    .filter(p -> StringUtils.equals(p.getName(), "authType"))
                    .map(Property::getValue).findFirst().orElse(""));
  }
  
  public String getUsername()
  {
    return properties.stream().filter(p -> StringUtils.equals(p.getName(), "username")).map(Property::getValue).findFirst().orElse("");
  }
  
  public String getPassword()
  {
    return properties.stream().anyMatch(p -> StringUtils.equals(p.getName(), "password")) ? "*****" : "";
  }

  public List<String> getFeatures()
  {
    return features;
  }

  public List<Property> getProperties()
  {
    return properties;
  }
  
  public TreeNode getPortTypes()
  {
    return portTypes;
  }
  
}
