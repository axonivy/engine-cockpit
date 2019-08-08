package ch.ivyteam.enginecockpit.services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.Webservice;
import ch.ivyteam.enginecockpit.model.Webservice.PortType;
import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.application.IApplicationInternal;

@ManagedBean
@ViewScoped
public class WebserviceDetailBean extends HelpServices
{
  private Webservice webservice;
  private String webserviceId;
  private String wsConfigKey;
  
  private ManagerBean managerBean;
  private PortType activePortType;
  
  public WebserviceDetailBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
    configuration = ((IApplicationInternal) managerBean.getSelectedIApplication()).getConfiguration();
  }
  
  public String getWebserviceId()
  {
    return webserviceId;
  }
  
  public void setWebserviceId(String webserviceId)
  {
    this.webserviceId = webserviceId;
    reloadWebservice();
  }
  
  private void reloadWebservice()
  {
    webservice = createWebService();
    wsConfigKey = "WebServiceClients." + webservice.getName();
  }

  private Webservice createWebService()
  {
    return new Webservice(managerBean.getSelectedIEnvironment().findWebService(webserviceId));
  }
  
  public Webservice getWebservice()
  {
    return webservice;
  }

  @Override
  public String getTitle()
  {
    return "Web Service '" + webservice.getName() + "'";
  }
  
  @Override
  public String getGuideText()
  {
    return "To edit your Web Service overwrite your app.yaml file. For example copy and paste the snippet below.";
  }
  
  @Override
  public String getYaml()
  {
    Map<String, String> valuesMap = new HashMap<>();
    valuesMap.put("name", webservice.getName());
    valuesMap.put("endpoints", parseEndpointsToYaml(webservice.getPortTypeMap()));
    valuesMap.put("features", parseFeaturesToYaml(webservice.getFeatures()));
    valuesMap.put("properties", parsePropertiesToYaml(webservice.getProperties().stream()
            .filter(p -> !StringUtils.equals(p.getName(), "password"))
            .collect(Collectors.toList())));
    String templateString = readTemplateString("webservice.yaml");
    StrSubstitutor strSubstitutor = new StrSubstitutor(valuesMap);
    return strSubstitutor.replace(templateString);
  }
  
  @Override
  public String getHelpUrl()
  {
    return UrlUtil.getCockpitEngineGuideUrl() + "#web-service-detail";
  }
  
  public void testWsEndpointConnection(String name)
  {
    testEndPoint(webservice.getPortTypeMap().get(name));
  }

  private void testEndPoint(PortType endpoint)
  {
    Client client = ClientBuilder.newClient();
    if (StringUtils.equals(webservice.getAuthType(), "HttpBasic"))
    {
      client.register(new Authenticator(webservice.getUsername(), webservice.getPassword()));
    }
    boolean invalidUrlFound = false;
    for (String url : endpoint.getLinks())
    {
      try
      {
        int status = client.target(url).request().post(Entity.json("")).getStatus();
        FacesContext.getCurrentInstance().addMessage("wsConfigMsg", 
                new FacesMessage(FacesMessage.SEVERITY_INFO, endpoint.getName(), ">> Status: " + status + " Url: " + url));
      }
      catch (ProcessingException ex)
      {
        invalidUrlFound = true;
      }
    }
    if (invalidUrlFound)
    {
      FacesContext.getCurrentInstance().addMessage("wsConfigMsg", 
              new FacesMessage(FacesMessage.SEVERITY_WARN, endpoint.getName(), 
                      "The some URLs seems to be not correct or they contain scripting context (can not be evaluated)"));
    }
  }
  
  private String parseEndpointsToYaml(Map<String, PortType> portTypes)
  {
    return portTypes.entrySet().stream().map(e -> e.getKey() + ": \n        - " + e.getValue().getLinks().stream()
            .map(v -> "\"" + v + "\"")
            .collect(Collectors.joining("\n        - "))).collect(Collectors.joining("\n      "));
  }
  
  public void saveConfig()
  {
    Webservice originConfig = createWebService();
    setIfChanged(wsConfigKey + ".Properties.username", webservice.getUsername(), originConfig.getUsername());
    setIfPwChanged(wsConfigKey + ".Properties.password", webservice);
    FacesContext.getCurrentInstance().addMessage("wsConfigMsg", 
            new FacesMessage("Web Service configuration saved", ""));
    reloadWebservice();
  }
  
  @SuppressWarnings("restriction")
  public void resetConfig()
  {
    configuration.remove(wsConfigKey + ".Properties");
    FacesContext.getCurrentInstance().addMessage("wsConfigMsg", 
            new FacesMessage("Web Service configuration reset", ""));
    reloadWebservice();
  }
  
  public void setActivePortType(String name)
  {
    activePortType = webservice.getPortTypeMap().get(name);
  }
  
  public PortType getActivePortType()
  {
    return activePortType;
  }
  
  @SuppressWarnings("restriction")
  public void resetPortType()
  {
    configuration.remove(wsConfigKey + ".Endpoints." + activePortType.getName());
    FacesContext.getCurrentInstance().addMessage("wsConfigMsg", 
            new FacesMessage("EndPoint reset", ""));
    reloadWebservice();
  }
  
  @SuppressWarnings("restriction")
  public void savePortType()
  {
    configuration.set(wsConfigKey + ".Endpoints." + activePortType.getName(), activePortType.getLinks());
    FacesContext.getCurrentInstance().addMessage("wsConfigMsg", 
            new FacesMessage("EndPoint saved", ""));
    reloadWebservice();
  }
  
  private class Authenticator implements ClientRequestFilter {

    private final String user;
    private final String password;

    public Authenticator(String user, String password) {
        this.user = user;
        this.password = password;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();
        final String basicAuthentication = getBasicAuthentication();
        headers.add("Authorization", basicAuthentication);

    }

    private String getBasicAuthentication() {
        String token = this.user + ":" + this.password;
        try {
            return "BASIC " + DatatypeConverter.printBase64Binary(token.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("Cannot encode with UTF-8", ex);
        }
    }
}

    
}
