package ch.ivyteam.enginecockpit.services.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.enginecockpit.commons.Feature;
import ch.ivyteam.enginecockpit.commons.Property;
import ch.ivyteam.ivy.webservice.client.WebServiceClient;

public class Webservice implements IService {
  private final String name;
  private final String genId;
  private final String description;
  private final String wsdlUrl;
  private final List<Feature> features;
  private final List<Property> properties;
  private String username;
  private String password;
  private boolean passwordChanged;
  private final TreeNode<EndPoint> portTypes = new DefaultTreeNode<>("PortTypes", null, null);
  private final Map<String, PortType> portTypeMap = new HashMap<>();

  public Webservice(WebServiceClient webservice) {
    name = webservice.name();
    description = webservice.description();
    wsdlUrl = webservice.wsdlUrl();
    var metas = webservice.metas();
    properties = webservice.properties().stream()
        .map(p -> new Property(p.key(), p.value(), metas.get(p.key()), p.isDefault()))
        .collect(Collectors.toList());
    password = properties.stream().filter(p -> StringUtils.equals(p.getName(), "password"))
        .map(Property::getValue).findFirst().orElse("");
    username = properties.stream().filter(p -> StringUtils.equals(p.getName(), "username"))
        .map(Property::getValue).findFirst().orElse("");
    passwordChanged = false;
    features = webservice.features().stream()
        .map(f -> new Feature(f.clazz(), f.isDefault()))
        .collect(Collectors.toList());
    genId = webservice.id();

    webservice.portTypes()
        .forEach(p -> {
          var portType = new DefaultTreeNode<>(new EndPoint("port", p), portTypes);
          portType.setExpanded(true);
          webservice.endpoints().get(p)
              .forEach(e -> new DefaultTreeNode<>(new EndPoint("link", e), portType));
        });
    webservice.portTypes().forEach(p -> portTypeMap.put(p, new PortType(p, webservice.endpoints().get(p))));
  }

  public String getName() {
    return name;
  }

  public String getGenId() {
    return genId;
  }

  public String getDescription() {
    return description;
  }

  public String getViewUrl(String app) {
    return "webservicedetail.xhtml?app=" + app + "&id=" + genId;
  }

  public String getWsdlUrl() {
    return wsdlUrl;
  }

  public String getAuthType() {
    return new WebServiceClientAuthTypeCalcuator(features).get();
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    if (StringUtils.isNotBlank(password)) {
      this.password = password;
      passwordChanged = true;
    }
  }

  @Override
  public boolean passwordChanged() {
    return passwordChanged;
  }

  public List<Feature> getFeatures() {
    return features;
  }

  public List<Property> getProperties() {
    return properties;
  }

  public TreeNode<EndPoint> getPortTypes() {
    return portTypes;
  }

  public Map<String, PortType> getPortTypeMap() {
    return portTypeMap;
  }

  public String getEndpoints() {
    return getPortTypeMap().values().stream().map(PortType::getDefault).collect(Collectors.joining(", "));
  }

  public static class EndPoint {
    private final String type;
    private final String name;

    public EndPoint(String type, String name) {
      this.type = type;
      this.name = name;
    }

    public String getType() {
      return type;
    }

    public String getName() {
      return name;
    }
  }

  public static class PortType {
    private final String name;
    private String defaultLink = "";
    private String fallbacks = "";

    public PortType(String name, List<String> links) {
      this.name = name;
      if (!links.isEmpty()) {
        this.defaultLink = links.get(0);
        this.fallbacks = links.stream().skip(1).collect(Collectors.joining("\n"));
      }
    }

    public String getName() {
      return name;
    }

    public List<String> getLinks() {
      var links = new ArrayList<String>();
      links.add(defaultLink);
      Arrays.stream(StringUtils.split(fallbacks, "\n"))
          .map(StringUtils::trim)
          .forEach(link -> links.add(link));
      return links;
    }

    public String getDefault() {
      return defaultLink;
    }

    public void setDefault(String defaultLink) {
      this.defaultLink = defaultLink;
    }

    public String getFallbacks() {
      return fallbacks;
    }

    public void setFallbacks(String input) {
      this.fallbacks = input;
    }
  }

}
