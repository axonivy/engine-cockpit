package ch.ivyteam.enginecockpit.services.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ch.ivyteam.enginecockpit.commons.Feature;
import ch.ivyteam.enginecockpit.commons.Property;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.monitor.mbeans.ivy.RestClientMonitor;
import ch.ivyteam.enginecockpit.services.DetailView;
import ch.ivyteam.enginecockpit.services.FeatureEditor;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestWrapper;
import ch.ivyteam.enginecockpit.services.model.RestClientDto;
import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.application.app.Application;
import ch.ivyteam.ivy.application.app.ApplicationRepository;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.rest.client.RestClient.Builder;
import ch.ivyteam.ivy.rest.client.RestClients;
import ch.ivyteam.ivy.rest.client.internal.RestClientExecutionManager;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class RestClientDetailBean extends DetailView implements FeatureEditor, Serializable {

  private RestClientDto restClient;
  private String restClientKey;

  private String appName;
  private Application app;

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

  public String getKey() {
    return restClientKey;
  }

  public void setKey(String restClientKey) {
    this.restClientKey = restClientKey;
  }

  public void onload() {
    app = ApplicationRepository.instance().findReleasedByName(appName);
    if (app == null) {
      ResponseHelper.notFound(Ivy.cm().content("/common/NotFoundApplication").replace("application", appName).get());
      return;
    }

    restClients = RestClients.of(app);
    var client = findRestClient();
    if (client == null) {
      ResponseHelper.notFound(
          Ivy.cm().content("/restClientDetail/NotFoundRestClient").replace("restClient", restClientKey).get());
      return;
    }

    loadRestClient();
    reloadExternalRestClient();
    liveStats = new RestClientMonitor(app.name(), app.version(), restClient.getKey());
  }

  private void reloadExternalRestClient() {
    var restClientManager = RestClientExecutionManager.instance();
    var client = findRestClient();

    var applicationContext = restClientManager.getRestWebServiceApplicationContext(app);
    var restWebService = applicationContext.getRestWebService(client.key());

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
    return restClients.find(restClientKey);
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

  @Override
  public void removeProperty(String name) {
    saveRestClient(restBuilder().removeProperty(name));
    loadRestClient();
  }

  @Override
  public String getTitle() {
    return Ivy.cm().content("/restClientDetail/Title").replace("restClient", restClientKey).get();
  }

  @Override
  public String getGuideText() {
    return Ivy.cm().co("/restClientDetail/GuideText");
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
    var msg = new FacesMessage(Ivy.cm().co("/restClientConfiguration/RestConfigurationSavedMessage"), "");
    FacesContext.getCurrentInstance().addMessage("restConfigMsg", msg);
    loadRestClient();
  }

  public void resetConfig() {
    connectionTest.stop();
    restClients.remove(restClientKey);
    var msg = new FacesMessage(Ivy.cm().co("/restClientConfiguration/RestConfigurationResetMessage"), "");
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
    return restClients.find(restClientKey).toBuilder();
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
  public Feature getEditFeature() {
    return editFeature;
  }

  @Override
  public void setEditFeature(String clazz) {
    this.editFeatureOrigin = clazz;
    this.editFeature = findFeature(clazz);
  }

  @Override
  public void removeFeature(String clazz) {
    saveRestClient(restBuilder().removeFeature(clazz));
    loadRestClient();
  }

  @Override
  public void saveEditFeature(boolean isNewFeature) {
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
