package ch.ivyteam.enginecockpit.services.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.commons.Feature;
import ch.ivyteam.enginecockpit.commons.Property;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.monitor.mbeans.ivy.RestClientMonitor;
import ch.ivyteam.enginecockpit.services.FeatureEditor;
import ch.ivyteam.enginecockpit.services.PropertyEditor;
import ch.ivyteam.enginecockpit.services.help.HelpServices;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult.IConnectionTestResult;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestWrapper;
import ch.ivyteam.enginecockpit.services.model.RestClientDto;
import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.rest.client.RestClient.Builder;
import ch.ivyteam.ivy.rest.client.RestClients;
import ch.ivyteam.ivy.rest.client.internal.RestClientExecutionManager;

@ManagedBean
@ViewScoped
public class RestClientDetailBean extends HelpServices implements IConnectionTestResult, PropertyEditor, FeatureEditor {

  private RestClientDto restClient;
  private String restClientName;

  private String appName;
  private IApplication app;

  private RestClients restClients;
  private ConnectionTestResult testResult;
  private RestClientMonitor liveStats;

  private final ConnectionTestWrapper connectionTest;
  private Property activeProperty;
  private Feature editFeature;
  private List<ExecHistory> history;
  private String editFeatureOrigin;

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

  @SuppressWarnings("restriction")
  private void reloadExternalRestClient() {
    var restClientManager = RestClientExecutionManager.instance();
    var client = findRestClient();

    var applicationContext = restClientManager.getRestWebServiceApplicationContext(app);
    var restWebService = applicationContext.getRestWebService(client.uniqueId());

    history = new ArrayList<>(restWebService.getCallHistory().stream()
        .map(call -> new ExecHistory(
            call.getExecutionTimestamp(),
            (call.getExecutionTimeInMicroSeconds()) / 1000L,
            call.getRequestUrl(),
            call.getRequestMethod(),
            call.getProcessElementId()))
        .toList());
  }

  public String getViewUrl() {
    return restClient.getViewUrl(appName);
  }

  public List<ExecHistory> getExecutionHistory() {
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

  @Override
  public List<Property> getProperties() {
    return restClient.getProperties();
  }

  @Override
  public void saveProperty(boolean isNewProperty) {
    if (!isNewProperty || !isExistingProperty()) {
      saveRestClient(restBuilder().property(getProperty().getName(), getProperty().getValue()));
    }
    loadRestClient();
  }

  public void removeProperty(String name) {
    saveRestClient(restBuilder().removeProperty(name));
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

  @SuppressWarnings("deprecation")
  @Override
  public String getYaml() {
    var valuesMap = new HashMap<String, String>();
    valuesMap.put("name", restClient.getName());
    valuesMap.put("url", restClient.getUrl());
    valuesMap.put("features", parseFeaturesToYaml(restClient.getFeatures()));
    valuesMap.put("properties", parsePropertiesToYaml(getProperties()));
    var templateString = readTemplateString("restclient.yaml");
    var strSubstitutor = new org.apache.commons.lang3.text.StrSubstitutor(valuesMap);
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
    var runner = new RestTestRunner(app, uiClient);
    testResult = (ConnectionTestResult) connectionTest.test(runner::test);
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

  @Override
  public List<Feature> getFeatures() {
    return restClient.getFeatures();
  }

  @Override
  public Feature getFeature() {
    return editFeature;
  }

  @Override
  public void setFeature(String clazz) {
    this.editFeatureOrigin = clazz;
    this.editFeature = findFeature(clazz);
  }

  public void removeFeature(String name) {
    saveRestClient(restBuilder().removeFeature(name));
    loadRestClient();
  }

  @Override
  public void saveFeature(boolean isNewFeature) {
    if (editFeatureOrigin.equals(editFeature.getClazz())) {
      return;
    }

    loadRestClient();
    if (isExistingFeatureThrowMessage()) {
      return;
    }

    Builder restBuilder = restBuilder();

    if (isNewFeature) {
      restBuilder.feature(editFeature.getClazz());
    } else {
      restBuilder.removeFeature(editFeatureOrigin);
      restBuilder.feature(editFeature.getClazz());
    }
    saveRestClient(restBuilder);
    loadRestClient();

  }
}
