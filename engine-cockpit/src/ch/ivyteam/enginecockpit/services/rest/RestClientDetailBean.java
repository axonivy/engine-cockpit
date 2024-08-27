package ch.ivyteam.enginecockpit.services.rest;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.ws.rs.client.WebTarget;

import org.apache.commons.lang.text.StrSubstitutor;

import ch.ivyteam.enginecockpit.commons.Property;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.monitor.mbeans.ivy.RestClientMonitor;
import ch.ivyteam.enginecockpit.services.help.HelpServices;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult.IConnectionTestResult;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestWrapper;
import ch.ivyteam.enginecockpit.services.model.RestClientDto;
import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.rest.client.RestClients;
import ch.ivyteam.ivy.rest.client.RestClient.Builder;

@ManagedBean
@ViewScoped
public class RestClientDetailBean extends HelpServices implements IConnectionTestResult {

  private RestClientDto restClient;
  private String restClientName;

  private String appName;
  private IApplication app;

  private RestClients restClients;
  private ConnectionTestResult testResult;
  private RestClientMonitor liveStats;

  private final ConnectionTestWrapper connectionTest;
  private Property activeProperty;

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
    liveStats = new RestClientMonitor(app.getName(), restClient.getUniqueId().toString());
  }

  public String getViewUrl() {
    return restClient.getViewUrl(appName);
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

  public boolean isSensitive() {
    if (activeProperty == null || activeProperty.getName() == null || restClient.getProperties() == null) {
      return false;
    }
    return restClient.getProperties().stream()
             .filter(property -> activeProperty.getName().equals(property.getName()))
             .findFirst()
             .map(Property::isSensitive)
             .orElse(false);
  }

  public void setProperty(String key) {
    this.activeProperty = new Property();
      if (key != null) {
        this.activeProperty = restClient.getProperties().stream()
            .filter(property -> property.getName().equals(key))
            .findFirst()
            .orElse(new Property());
      }
    }

    public Property getProperty() {
      return activeProperty;
    }

    public void saveProperty() {
      saveRestClient(wsBuilder().property(activeProperty.getName(),activeProperty.getValue()));
      loadRestClient();
    }

    public void removeProperty(String name) {
      restClients.remove(restClients+ "." +"Properties"+ "." +name);
      loadRestClient();
    }

  @Override
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
    valuesMap.put("properties", parsePropertiesToYaml(restClient.getProperties()));
    var templateString = readTemplateString("restclient.yaml");
    var strSubstitutor = new StrSubstitutor(valuesMap);
    return strSubstitutor.replace(templateString);
  }

  @Override
  public String getHelpUrl() {
    return UrlUtil.getCockpitEngineGuideUrl() + "services.html#rest-client-detail";
  }

  public void testRestConnection() {
    var uiClient = new UiStateClient(findRestClient())
      .setUiState(restClient)
      .setTimeout(TimeUnit.SECONDS, 5)
      .setReadTimeout(TimeUnit.SECONDS, 5)
      .toClient();
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
  
  private Builder wsBuilder() {
    return restClients.find(restClientName).toBuilder();
  }

  private void saveRestClient(Builder builder) {
    restClients.set(builder.toRestClient());
  }
}
