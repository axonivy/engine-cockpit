package ch.ivyteam.enginecockpit.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.ivy.application.restricted.IWebService;
import ch.ivyteam.util.Property;

@SuppressWarnings("restriction")
public class Webservice implements IService
{
  private String name;
  private String genId;
  private String description;
  private String wsdlUrl;
  private List<String> features;
  private List<Property> properties;
  private String username;
  private String password;
  private boolean passwordChanged;
  private TreeNode portTypes = new DefaultTreeNode("PortTypes", null);
  private Map<String, PortType> portTypeMap = new HashMap<>();
  
  public Webservice(IWebService webservice)
  {
    name = webservice.getName();
    description = webservice.getDescription();
    wsdlUrl = webservice.getWsdlUrl();
    properties = webservice.getProperties().stream().map(p -> new Property(p.getName(), p.getValue())).collect(Collectors.toList());
    password = properties.stream().filter(p -> StringUtils.equals(p.getName(), "password")).map(p -> p.getValue()).findFirst().orElse("");
    username = properties.stream().filter(p -> StringUtils.equals(p.getName(), "username")).map(p -> p.getValue()).findFirst().orElse("");
    passwordChanged = false;
    features = webservice.getFeatures().stream().map(f -> f.getClazz()).collect(Collectors.toList());
    genId = webservice.getGenerationIdentifier();
    
    webservice.getPortTypes()
            .forEach(p -> {
              TreeNode portType = new DefaultTreeNode(new EndPoint("port", p.getPortType()), portTypes);
              portType.setExpanded(true);
              webservice.getWebServiceEndpoints(p.getPortType()).forEach(e -> new DefaultTreeNode(
                      new EndPoint("link", e.getEndpointAddress()), portType));
            });
    
    webservice.getPortTypes()
            .forEach(p -> portTypeMap.put(p.getPortType(), new PortType(p.getPortType(), 
                    webservice.getWebServiceEndpoints(p.getPortType()).stream()
                            .map(e -> e.getEndpointAddress()).collect(Collectors.toList()))));
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
    return username;
  }
  
  public void setUsername(String username)
  {
    this.username = username;
  }
  
  @Override
  public String getPassword()
  {
    return password;
  }
  
  public void setPassword(String password)
  {
    if (StringUtils.isNotBlank(password))
    {
      this.password = password;
      passwordChanged = true;
    }
  }
  
  @Override
  public boolean passwordChanged()
  {
    return passwordChanged;
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
  
  public Map<String, PortType> getPortTypeMap()
  {
    return portTypeMap;
  }
  
  public String getEndpoints()
  {
    return getPortTypeMap().values().stream().map(pt -> pt.getDefault()).collect(Collectors.joining(", "));
  }
  
  public static class EndPoint
  {
    private String type;
    private String name;

    public EndPoint(String type, String name)
    {
      this.type = type;
      this.name = name;
    }
    
    public String getType()
    {
      return type;
    }
    
    public String getName()
    {
      return name;
    }
  }
  
  public static class PortType
  {
    private String name;
    private String defaultLink = "";
    private String fallbacks = "";
    
    public PortType(String name, List<String> links)
    {
      this.name = name;
      if (!links.isEmpty())
      {
        this.defaultLink = links.get(0);
        this.fallbacks = links.stream().skip(1).collect(Collectors.joining("\n"));
      }
    }
    
    public String getName()
    {
      return name;
    }
    
    public List<String> getLinks()
    {
      List<String> links = new ArrayList<>();
      links.add(defaultLink);
      Arrays.stream(StringUtils.split(fallbacks, "\n"))
              .map(link -> StringUtils.trim(link))
              .forEach(link -> links.add(link));
      return links;
    }
    
    public String getDefault()
    {
      return defaultLink;
    }
    
    public void setDefault(String defaultLink)
    {
      this.defaultLink = defaultLink;
    }
    
    public String getFallbacks()
    {
      return fallbacks;
    }
    
    public void setFallbacks(String input)
    {
      this.fallbacks = input;
    }
  }
  
}
