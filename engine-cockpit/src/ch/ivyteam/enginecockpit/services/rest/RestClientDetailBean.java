package ch.ivyteam.enginecockpit.services.rest;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.ws.rs.client.WebTarget;

import org.apache.commons.lang.text.StrSubstitutor;

import ch.ivyteam.enginecockpit.commons.Property;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.monitor.mbeans.ivy.RestClientMonitor;
import ch.ivyteam.enginecockpit.services.PropertyEditor;
import ch.ivyteam.enginecockpit.services.help.HelpServices;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult.IConnectionTestResult;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestWrapper;
import ch.ivyteam.enginecockpit.services.model.ExecHistoryStatement;
import ch.ivyteam.enginecockpit.services.model.RestClientDto;
import ch.ivyteam.enginecockpit.util.DateUtil;
import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.db.IExternalDatabaseRuntimeConnection;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.rest.client.RestClients;
import ch.ivyteam.ivy.rest.client.RestClient.Builder;
import ch.ivyteam.ivy.rest.client.internal.RestClientExecutionManager;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class RestClientDetailBean extends HelpServices implements IConnectionTestResult, PropertyEditor {

  private RestClientDto restClient;
  private String restClientName;

  private String appName;
  private IApplication app;

  private RestClients restClients;
  private ConnectionTestResult testResult;
  private RestClientMonitor liveStats;

  private final ConnectionTestWrapper connectionTest;
<<<<<<< Upstream, based on 9834b4584a3d112ead90cda1cfcbdf65daac1231
  private Property activeProperty;
  private List<ExecStatement> history;
=======
  private List<ExecHistoryStatement> history;
>>>>>>> eef7ee7 XIVY-14796 move ExecHistoryStatement in own class

  public RestClientDetailBean() {
    connectionTest = new ConnectionTestWrapper();
  }

  public void setApp(String appName) {
    this.appName = appName;
  }

  public String getApp() {
    return appName;
  }

  public String getName() {
    return restClientName;
  }

  public void setName(String restClientName) {
    this.restClientName = restClientName;
  }

  public void onload() {
    app = IApplicationRepository.instance().findByName(appName).orElse(null);
    if (app == null) {
      ResponseHelper.notFound("Application '" + appName + "' not found");
      return;
    }

    restClients = RestClients.of(app);
    var client = findRestClient();
    if (client == null) {
      ResponseHelper.notFound("Rest client '" + restClientName + "' not found");
      return;
    }

    loadRestClient();
    reloadExternalRestClient();
    liveStats = new RestClientMonitor(app.getName(), restClient.getUniqueId().toString());
  }

  private void reloadExternalRestClient() {
      RestClientExecutionManager restClientManager = new RestClientExecutionManager();
      RestClients restClients = RestClients.of(app);
      RestClient restClient = restClients.find(restClientName);
      if (restClient == null) {
          ResponseHelper.notFound("Rest client '" + restClientName + "' not found");
          return;
      }
      
      var applicationContext = restClientManager.getRestWebServiceApplicationContext(app);
      var restWebService = applicationContext.getRestWebService(restClient.uniqueId());
      restWebService.setRecordRequestResponse(true);

      var callHistory = restWebService.getCallHistory();
      history = callHistory.stream()
          .map(entry -> new ExecHistoryStatement(
              entry.getExecutionTimestamp(),
              entry.getExecutionTimeInMicroSeconds(),
              entry.getProcessElementId()))
          .collect(Collectors.toList());
  }
  
    public static class Connection {
      private String lastUsed;
      private boolean inUse;

      public Connection(IExternalDatabaseRuntimeConnection conn) {
        lastUsed = DateUtil.formatDate(conn.getLastUsed());
        inUse = conn.isInUse();
      }

      public String getLastUsed() {
        return lastUsed;
      }

      public boolean isInUse() {
        return inUse;
      }
    }

  public String getViewUrl() {
    return restClient.getViewUrl(appName);
  }
  
  public List<ExecHistoryStatement> getExecutionHistory() {
      return history;
    }

  private void loadRestClient() {
    this.restClient = new RestClientDto(findRestClient());
  }

  private RestClient findRestClient() {
    return restClients.find(restClientName);
  }

  public RestClientDto getRestClient() {
    return restClient;
  }

  public List<Property> getProperties() {
    return restClient.getProperties();
  }

  public void saveProperty() {
    saveRestClient(restBuilder().property(getProperty().getName(), getProperty().getValue()));
    loadRestClient();
  }

  public void removeProperty(String name) {
    restClients.remove(restClient.getName()+ "." +"Properties"+ "." +name);
    loadRestClient();
  }

  public String getTitle() {
    return "Rest Client '" + restClientName + "'";
  }

  @Override
  public String getGuideText() {
    return "To edit your Rest Client overwrite your app.yaml file. For example copy and paste the snippet below.";
  }

  @Override
  public String getYaml() {
    var valuesMap = new HashMap<String, String>();
    valuesMap.put("name", restClient.getName());
    valuesMap.put("url", restClient.getUrl());
    valuesMap.put("features", parseFeaturesToYaml(restClient.getFeatures()));
    valuesMap.put("properties", parsePropertiesToYaml(getProperties()));
    var templateString = readTemplateString("restclient.yaml");
    var strSubstitutor = new StrSubstitutor(valuesMap);
    return strSubstitutor.replace(templateString);
  }

  @Override
  public String getHelpUrl() {
    return UrlUtil.getCockpitEngineGuideUrl() + "services.html#rest-client-detail";
  }

  public void testRestConnection() {
    var uiClient = new UiStateClient(findRestClient()).setUiState(restClient).setTimeout(TimeUnit.SECONDS, 5)
        .setReadTimeout(TimeUnit.SECONDS, 5).toClient();
    WebTarget target = RestTestRunner.createTarget(app, uiClient);
    testResult = (ConnectionTestResult) connectionTest.test(() -> RestTestRunner.testConnection(target));
  }

  public void saveConfig() {
    connectionTest.stop();
    var uiClient = new UiStateClient(findRestClient()).setUiState(restClient).toClient();
    restClients.set(uiClient);
    var msg = new FacesMessage("Rest configuration saved", "");
    FacesContext.getCurrentInstance().addMessage("restConfigMsg", msg);
    loadRestClient();
  }

  public void resetConfig() {
    connectionTest.stop();
    restClients.remove(restClientName);
    var msg = new FacesMessage("Rest configuration reset", "");
    FacesContext.getCurrentInstance().addMessage("restConfigMsg", msg);
    loadRestClient();
  }

  @Override
  public ConnectionTestResult getResult() {
    return testResult;
  }

  public void reset() {
    testResult = null;
  }

  public RestClientMonitor getLiveStats() {
    return liveStats;
  }
  
  private Builder restBuilder() {
    return restClients.find(restClientName).toBuilder();
  }

  private void saveRestClient(Builder builder) {
    restClients.set(builder.toRestClient());
  }

  @Override
  public Property getProperty() {
    return activeProperty;
  }

  @Override
  public void setProperty(String key) {
    this.activeProperty = findProperty(key);
  }
}
