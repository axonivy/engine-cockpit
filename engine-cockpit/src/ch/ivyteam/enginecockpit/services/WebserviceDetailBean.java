package ch.ivyteam.enginecockpit.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.Webservice;

@ManagedBean
@ViewScoped
public class WebserviceDetailBean extends HelpServices
{
  private Webservice webservice;
  private String webserviceId;
  
  private ManagerBean managerBean;
  
  public WebserviceDetailBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
  }
  
  public String getWebserviceId()
  {
    return webserviceId;
  }
  
  public void setWebserviceId(String webserviceId)
  {
    this.webserviceId = webserviceId;
    webservice = new Webservice(managerBean.getSelectedIEnvironment().findWebService(webserviceId));
  }
  
  public Webservice getWebservice()
  {
    return webservice;
  }

  @Override
  public String getTitle()
  {
    return "Edit Web Service '" + webservice.getName() + "'";
  }
  
  @Override
  public String getGuideText()
  {
    return "To edit your Web Service overwrite your app.yaml file";
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
  
  private String parseEndpointsToYaml(Map<String, List<String>> portTypes)
  {
    return portTypes.entrySet().stream().map(e -> e.getKey() + ": \n        - " + e.getValue().stream()
            .map(v -> "\"" + v + "\"")
            .collect(Collectors.joining("\n        - "))).collect(Collectors.joining("\n      "));
  }
    
}
