package ch.ivyteam.enginecockpit.services;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.RestClient;
import ch.ivyteam.ivy.application.restricted.rest.IRestClient;
import ch.ivyteam.ivy.application.restricted.rest.RestClientDao;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class RestClientDetailBean extends HelpServices
{
  private RestClient restClient;
  private String restClientName;
  
  private ManagerBean managerBean;
  private RestClientDao restClientDao;
  
  public RestClientDetailBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
    restClientDao = RestClientDao.forApp(managerBean.getSelectedIApplication());
  }
  
  public String getRestClientName()
  {
    return restClientName;
  }
  
  public void setRestClientName(String restClientName)
  {
    this.restClientName = restClientName;
    IRestClient iRestClient = restClientDao.findByName(restClientName, managerBean.getSelectedIEnvironment());
    if (iRestClient == null)
    {
      iRestClient = restClientDao.findByName(restClientName);
    }
    restClient = new RestClient(iRestClient);
  }
  
  public RestClient getRestClient()
  {
    return restClient;
  }

  @Override
  public String getTitle()
  {
    return "Rest Client '" + restClientName + "'";
  }
  
  @Override
  public String getGuideText()
  {
    return "To edit your Rest Client overwrite your app.yaml file. For example copy and paste the snippet below.";
  }
  
  @Override
  public String getYaml()
  {
    Map<String, String> valuesMap = new HashMap<>();
    valuesMap.put("name", restClient.getName());
    valuesMap.put("url", restClient.getUrl());
    valuesMap.put("features", parseFeaturesToYaml(restClient.getFeatures()));
    valuesMap.put("properties", parsePropertiesToYaml(restClient.getProperties().stream()
            .filter(p -> !StringUtils.equals(p.getName(), "password"))
            .collect(Collectors.toList())));
    String templateString = readTemplateString("restclient.yaml");
    StrSubstitutor strSubstitutor = new StrSubstitutor(valuesMap);
    return strSubstitutor.replace(templateString);
  }

}
