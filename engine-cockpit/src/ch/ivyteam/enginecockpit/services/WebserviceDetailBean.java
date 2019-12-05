package ch.ivyteam.enginecockpit.services;

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
import javax.ws.rs.client.Entity;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.Webservice;
import ch.ivyteam.enginecockpit.model.Webservice.PortType;
import ch.ivyteam.enginecockpit.services.ConnectionTestResult.IConnectionTestResult;
import ch.ivyteam.enginecockpit.services.ConnectionTestResult.TestResult;
import ch.ivyteam.enginecockpit.util.Authenticator;
import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.application.IApplicationInternal;

@ManagedBean
@ViewScoped
public class WebserviceDetailBean extends HelpServices implements IConnectionTestResult
{
  private Webservice webservice;
  private String webserviceId;
  private String wsConfigKey;
  
  private ManagerBean managerBean;
  private PortType activePortType;
  private String activeEndpointUrl;
  private ConnectionTestResult testResult;
  
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
    return UrlUtil.getCockpitEngineGuideUrl() + "services.html#web-service-detail";
  }
  
  public void setActiveEndpoint(String endpointUrl)
  {
    this.activeEndpointUrl = endpointUrl;
  }

  public String getActiveEndpoint()
  {
    return activeEndpointUrl;
  }
  
  public void testWsEndpointUrl()
  {
    Client client = ClientBuilder.newClient();
    if (authSupportedForTesting())
    {
      client.register(new Authenticator(webservice.getUsername(), webservice.getPassword()));
    }
    try
    {
      int status = client.target(activeEndpointUrl).request().post(Entity.json("")).getStatus();
      if (status == 401)
      {
        testResult = new ConnectionTestResult("POST", status, TestResult.WARNING, "Authentication (only HttpBasic supported) was not successful");
      }
      else if (status == 404)
      {
        testResult = new ConnectionTestResult("POST", status, TestResult.WARNING, "Service not found");
      }
      else
      {
        testResult = new ConnectionTestResult("POST", status, TestResult.SUCCESS, "Successfully reached web service");
      }
    }
    catch (ProcessingException ex)
    {
      testResult = new ConnectionTestResult("", 0, TestResult.ERROR, "The URL seems to be not correct or contains scripting context (can not be evaluated)\n"
              + "An error occurred: " + ExceptionUtils.getStackTrace(ex));
    }
  }

  private boolean authSupportedForTesting()
  {
    return StringUtils.equals(webservice.getAuthType(), "HttpBasic") || StringUtils.equals(webservice.getAuthType(), "HTTP_BASIC");
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
  
  @Override
  public ConnectionTestResult getResult()
  {
    return testResult;
  }
  
}
