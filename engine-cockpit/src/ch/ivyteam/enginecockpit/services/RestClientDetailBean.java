package ch.ivyteam.enginecockpit.services;

import java.util.HashMap;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.ws.rs.ProcessingException;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.exception.ExceptionUtils;

import ch.ivyteam.enginecockpit.monitor.mbeans.ivy.RestClientMonitor;
import ch.ivyteam.enginecockpit.services.help.HelpServices;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult.IConnectionTestResult;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult.TestResult;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestWrapper;
import ch.ivyteam.enginecockpit.services.model.RestClientDto;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.rest.client.RestClients;
import ch.ivyteam.ivy.webservice.internal.execution.WebserviceExecutionManager;
import ch.ivyteam.ivy.webservice.restricted.execution.IWebserviceExecutionManager;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class RestClientDetailBean extends HelpServices implements IConnectionTestResult
{
  private RestClientDto restClient;
  private String restClientName;

  private final ManagerBean managerBean;
  private final RestClients restClients;
  private ConnectionTestResult testResult;
  private RestClientMonitor liveStats;

  private final ConnectionTestWrapper connectionTest;

  public RestClientDetailBean()
  {
    var context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
    restClients = RestClients.of(managerBean.getSelectedIApplication(), managerBean.getSelectedEnvironment());
    connectionTest = new ConnectionTestWrapper();
  }

  public String getRestClientName()
  {
    return restClientName;
  }

  public void setRestClientName(String restClientName)
  {
    if (this.restClientName == null)
    {
      this.restClientName = restClientName;
      reloadRestClient();
      liveStats = new RestClientMonitor(managerBean.getSelectedApplicationName(),
              managerBean.getSelectedEnvironment(), restClient.getUniqueId().toString());
    }
  }

  private void reloadRestClient()
  {
    this.restClient = new RestClientDto(restClients.find(restClientName));
  }

  public RestClientDto getRestClient()
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
    var valuesMap = new HashMap<String, String>();
    valuesMap.put("name", restClient.getName());
    valuesMap.put("url", restClient.getUrl());
    valuesMap.put("features", parseFeaturesToYaml(restClient.getFeatures()));
    valuesMap.put("properties", parsePropertiesToYaml(restClient.getProperties().stream()
            .filter(p -> !StringUtils.equals(p.getName(), "password"))
            .collect(Collectors.toList())));
    var templateString = readTemplateString("restclient.yaml");
    var strSubstitutor = new StrSubstitutor(valuesMap);
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
      var executionManger = (WebserviceExecutionManager) IWebserviceExecutionManager.instance();
      var restCall = executionManger
              .getRestWebServiceApplicationContext(managerBean.getSelectedIApplication())
              .getRestWebService(restClient.getUniqueId()).createCall();
      var status = restCall.getWebTarget().request().head().getStatus();
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
    var builder = restClients.find(restClientName).toBuilder()
            .uri(restClient.getUrl());
    builder.property("username", restClient.getUsername());
    if (restClient.passwordChanged())
    {
      builder.property("password", restClient.getPassword());
    }
    restClients.set(builder.toRestClient());
    FacesContext.getCurrentInstance().addMessage("restConfigMsg",
            new FacesMessage("Rest configuration saved", ""));
    reloadRestClient();
  }

  public void resetConfig()
  {
    connectionTest.stop();
    restClients.remove(restClientName);
    FacesContext.getCurrentInstance().addMessage("restConfigMsg",
            new FacesMessage("Rest configuration reset", ""));
    reloadRestClient();
  }

  @Override
  public ConnectionTestResult getResult()
  {
    return testResult;
  }

  public RestClientMonitor getLiveStats()
  {
    return liveStats;
  }

}
