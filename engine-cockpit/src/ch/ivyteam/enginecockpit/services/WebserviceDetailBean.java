package ch.ivyteam.enginecockpit.services;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;

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
    configuration.remove(wsConfigKey);
    FacesContext.getCurrentInstance().addMessage("wsConfigMsg", 
            new FacesMessage("Web Service configuration reseted", ""));
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
            new FacesMessage("EndPoint reseted", ""));
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
    
}
