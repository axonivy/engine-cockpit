package ch.ivyteam.enginecockpit.services;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.ws.rs.ProcessingException;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.exception.ExceptionUtils;

import ch.ivyteam.di.restricted.DiCore;
import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.RestClient;
import ch.ivyteam.enginecockpit.services.ConnectionTestResult.IConnectionTestResult;
import ch.ivyteam.enginecockpit.services.ConnectionTestResult.TestResult;
import ch.ivyteam.enginecockpit.system.ConnectionTestWrapper;
import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.application.IApplicationInternal;
import ch.ivyteam.ivy.application.restricted.rest.IRestClient;
import ch.ivyteam.ivy.application.restricted.rest.RestClientDao;
import ch.ivyteam.ivy.rest.client.internal.ExternalRestWebServiceCall;
import ch.ivyteam.ivy.webservice.internal.execution.WebserviceExecutionManager;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class RestClientDetailBean extends HelpServices implements IConnectionTestResult
{
  private RestClient restClient;
  private String restClientName;
  
  private ManagerBean managerBean;
  private RestClientDao restClientDao;
  private String restConfigKey;
  private ConnectionTestResult testResult;
  
  private final ConnectionTestWrapper connectionTest;
  
  public RestClientDetailBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
    restClientDao = RestClientDao.forApp(managerBean.getSelectedIApplication());
    configuration = ((IApplicationInternal) managerBean.getSelectedIApplication()).getConfiguration();
    connectionTest = new ConnectionTestWrapper();
  }
  
  public String getRestClientName()
  {
    return restClientName;
  }
  
  public void setRestClientName(String restClientName)
  {
    this.restClientName = restClientName;
    reloadRestClient();
  }

  private void reloadRestClient()
  {
    this.restClient = createRestClient();
    restConfigKey = "RestClients." + restClientName;
  }

  private RestClient createRestClient()
  {
    IRestClient iRestClient = restClientDao.findByName(restClientName, managerBean.getSelectedIEnvironment());
    if (iRestClient == null)
    {
      iRestClient = restClientDao.findByName(restClientName);
    }
    return new RestClient(iRestClient);
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
  
  @Override
  public String getHelpUrl()
  {
    return UrlUtil.getCockpitEngineGuideUrl() + "services.html#rest-client-detail";
  }
  
  public void testRestConnection()
  {
    testResult = (ConnectionTestResult) connectionTest.test(() -> testConnection());
  }
  
  private ConnectionTestResult testConnection()
  {
    try
    {
      ExternalRestWebServiceCall restCall = DiCore.getGlobalInjector().getInstance(WebserviceExecutionManager.class)
              .getRestWebServiceApplicationContext(managerBean.getSelectedIApplication())
              .getRestWebService(restClient.getUniqueId()).createCall();
      int status = restCall.getWebTarget().request().head().getStatus();
      if (status >= 200 && status < 400)
      {
        return new ConnectionTestResult("HEAD", status, TestResult.SUCCESS, "Successfully sent test request to REST service");
      }
      else if (status == 401)
      {
        return new ConnectionTestResult("HEAD", status, TestResult.WARNING, "Authentication was not successful");
      }
      else 
      {
        return new ConnectionTestResult("HEAD", status, TestResult.ERROR, "Could not connect to REST service");
      }
    }
    catch (ProcessingException ex)
    {
      return new ConnectionTestResult("", 0, TestResult.ERROR, "Invalid Url (may contains script context)\n"
              + "An error occurred: " + ExceptionUtils.getStackTrace(ex));
    }
  }
  
  public void saveConfig()
  {
    connectionTest.stop();
    RestClient originConfig = createRestClient();
    setIfChanged(restConfigKey + ".Url", restClient.getUrl(), originConfig.getUrl());
    setIfChanged(restConfigKey + ".Properties.username", restClient.getUsername(), originConfig.getUsername());
    setIfPwChanged(restConfigKey + ".Properties.password", restClient);
    FacesContext.getCurrentInstance().addMessage("restConfigMsg", 
            new FacesMessage("Rest configuration saved", ""));
    reloadRestClient();
  }
  
  public void resetConfig()
  {
    connectionTest.stop();
    configuration.remove(restConfigKey);
    FacesContext.getCurrentInstance().addMessage("restConfigMsg", 
            new FacesMessage("Rest configuration reset", ""));
    reloadRestClient();
  }

  @Override
  public ConnectionTestResult getResult()
  {
    return testResult;
  }

}
