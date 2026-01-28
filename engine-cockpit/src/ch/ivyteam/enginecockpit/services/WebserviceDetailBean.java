package ch.ivyteam.enginecockpit.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;

import org.apache.commons.lang3.exception.ExceptionUtils;

import ch.ivyteam.enginecockpit.commons.Feature;
import ch.ivyteam.enginecockpit.commons.Property;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.monitor.mbeans.ivy.WebServiceMonitor;
import ch.ivyteam.enginecockpit.services.help.HelpServices;
import ch.ivyteam.enginecockpit.services.model.Authenticator;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult.IConnectionTestResult;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult.TestResult;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestWrapper;
import ch.ivyteam.enginecockpit.services.model.Webservice;
import ch.ivyteam.enginecockpit.services.model.Webservice.PortType;
import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.ReleaseState;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.ssl.restricted.SslConnectionTesterClient;
import ch.ivyteam.ivy.webservice.client.WebServiceClient.Builder;
import ch.ivyteam.ivy.webservice.client.WebServiceClients;
import ch.ivyteam.ivy.webservice.restricted.execution.IWebserviceExecutionManager;

@ManagedBean
@ViewScoped
public class WebserviceDetailBean extends HelpServices implements IConnectionTestResult, PropertyEditor, FeatureEditor {

  private Webservice webservice;
  private String webserviceId;

  private String appName;
  private IApplication app;
  private PortType activePortType;
  private String activeEndpointUrl;
  private ConnectionTestResult testResult;

  private final ConnectionTestWrapper connectionTest;
  private WebServiceMonitor liveStats;
  private WebServiceClients webServiceClients;
  private Property activeProperty;
  private Feature editFeature;
  private List<WebServiceExecHistory> history;
  private String editFeatureOrigin;

  public WebserviceDetailBean() {
    connectionTest = new ConnectionTestWrapper();
  }

  public void setApp(String appName) {
    this.appName = appName;
  }

  public String getApp() {
    return appName;
  }

  public String getId() {
    return webserviceId;
  }

  public void setId(String webserviceId) {
    this.webserviceId = webserviceId;
  }

  public void onload() {
    app = IApplicationRepository.instance().findByName(appName).stream()
        .filter(a -> a.getReleaseState() == ReleaseState.RELEASED)
        .findAny()
        .orElse(null);
    if (app == null) {
      ResponseHelper.notFound(Ivy.cm().content("/common/NotFoundApplication").replace("application", appName).get());
      return;
    }
    webServiceClients = WebServiceClients.of(app);
    loadWebService();
    reloadExternalWebservice();
    liveStats = new WebServiceMonitor(appName, app.getVersion(), webserviceId);
  }

  private void reloadExternalWebservice() {
    var webService = IWebserviceExecutionManager.instance().getSoapWebServiceApplicationContext(app).getSoapWebService(webserviceId);

    history = new ArrayList<>(webService.getCallHistory().stream()
        .map(call -> new WebServiceExecHistory(
            call.getEndpoint(),
            call.getTimestamp(),
            call.getExecutionTimeInMilliSeconds(),
            call.getOperation(),
            call.getPort()))
        .toList());
  }

  public List<WebServiceExecHistory> getExecutionHistory() {
    return history;
  }

  public String getViewUrl() {
    return webservice.getViewUrl(appName);
  }

  private void loadWebService() {
    var ws = webServiceClients.find(webserviceId);
    if (ws == null) {
      ResponseHelper.notFound(
          Ivy.cm().content("/webServiceDetail/NotFoundWebService").replace("webserviceId", webserviceId).get());
      return;
    }
    webservice = new Webservice(ws);
  }

  public Webservice getWebservice() {
    return webservice;
  }

  @Override
  public List<Property> getProperties() {
    return webservice.getProperties();
  }

  @Override
  public void saveProperty(boolean isNewProperty) {
    if (!isNewProperty || !isExistingProperty()) {
      var prop = getProperty();
      saveWebService(wsBuilder().property(prop.getName(), prop.getValue()));
    }
    loadWebService();
  }

  public void removeProperty(String name) {
    saveWebService(wsBuilder().removeProperty(name));
    loadWebService();
  }

  @Override
  public String getTitle() {
    return Ivy.cm().content("/webServiceDetail/Title").replace("webservice", webservice.getName()).get();
  }

  @Override
  public String getGuideText() {
    return Ivy.cm().co("/webServiceDetail/GuideText");
  }

  @SuppressWarnings("deprecation")
  @Override
  public String getYaml() {
    var valuesMap = new HashMap<String, String>();
    valuesMap.put("name", webservice.getName());
    valuesMap.put("endpoints", parseEndpointsToYaml(webservice.getPortTypeMap()));
    valuesMap.put("features", parseFeaturesToYaml(getFeatures()));
    valuesMap.put("properties", parsePropertiesToYaml(getProperties()));
    var templateString = readTemplateString("webservice.yaml");
    var strSubstitutor = new org.apache.commons.lang3.text.StrSubstitutor(valuesMap);
    return strSubstitutor.replace(templateString);
  }

  @Override
  public String getHelpUrl() {
    return UrlUtil.getCockpitEngineGuideUrl() + "services.html#web-service-detail";
  }

  public void setActiveEndpoint(String endpointUrl) {
    this.activeEndpointUrl = endpointUrl;
  }

  public String getActiveEndpoint() {
    return activeEndpointUrl;
  }

  public void testWsEndpointUrl() {
    testResult = (ConnectionTestResult) connectionTest.test(this::testConnection);
  }

  private ConnectionTestResult testConnection() {
    var client = SslConnectionTesterClient.createClient();

    if (authSupportedForTesting()) {
      client.register(new Authenticator(webservice.getUsername(), webservice.getPassword()));
    }
    try {
      int status = client.target(activeEndpointUrl).request().post(Entity.json("")).getStatus();
      if (status == 401) {
        return new ConnectionTestResult("POST", status, TestResult.WARNING,
            Ivy.cm().co("/webServiceEndpoints/TestConnectionAuthenticationMessage"));
      } else if (status == 404) {
        return new ConnectionTestResult("POST", status, TestResult.WARNING,
            Ivy.cm().co("/webServiceEndpoints/TestConnectionNotFoundMessage"));
      } else {
        return new ConnectionTestResult("POST", status, TestResult.SUCCESS,
            Ivy.cm().co("/webServiceEndpoints/TestConnectionSuccessMessage"));
      }
    } catch (ProcessingException ex) {
      return new ConnectionTestResult("", 0, TestResult.ERROR,
          Ivy.cm().content("/webServiceEndpoints/TestConnectionErrorMessage")
              .replace("exception", ExceptionUtils.getStackTrace(ex)).get());
    }
  }

  private boolean authSupportedForTesting() {
    return Objects.equals(webservice.getAuthType(), "HttpBasic")
        || Objects.equals(webservice.getAuthType(), "HTTP_BASIC");
  }

  private String parseEndpointsToYaml(Map<String, PortType> portTypes) {
    return portTypes.entrySet().stream()
        .map(e -> e.getKey() + ": \n        - " + e.getValue().getLinks().stream()
            .map(v -> "\"" + v + "\"")
            .collect(Collectors.joining("\n        - ")))
        .collect(Collectors.joining("\n      "));
  }

  public void saveConfig() {
    connectionTest.stop();
    var builder = wsBuilder().property("username", webservice.getUsername());
    if (webservice.passwordChanged()) {
      builder.property("password", webservice.getPassword());
    }
    webServiceClients.set(builder.toWebServiceClient());
    var msg = new FacesMessage(Ivy.cm().co("/webServiceConfiguration/WebServiceConfigurationSavedMessage"), "");
    FacesContext.getCurrentInstance().addMessage("wsConfigMsg", msg);
    loadWebService();
  }

  public void resetConfig() {
    connectionTest.stop();
    webServiceClients.remove(webservice.getName());
    var msg = new FacesMessage(Ivy.cm().co("/webServiceConfiguration/WebServiceConfigurationResetMessage"), "");
    FacesContext.getCurrentInstance().addMessage("wsConfigMsg", msg);
    loadWebService();
  }

  public void setActivePortType(String name) {
    activePortType = webservice.getPortTypeMap().get(name);
  }

  public PortType getActivePortType() {
    return activePortType;
  }

  public void savePortType() {
    var client = wsBuilder()
        .endpoints(activePortType.getName(), activePortType.getLinks())
        .toWebServiceClient();
    webServiceClients.set(client);
    var msg = new FacesMessage(Ivy.cm().co("/webServiceEndpoints/EndPointSavedMessage"), "");
    FacesContext.getCurrentInstance().addMessage("wsConfigMsg", msg);
    loadWebService();
  }

  @Override
  public ConnectionTestResult getResult() {
    return testResult;
  }

  public void reset() {
    testResult = null;
  }

  public WebServiceMonitor getLiveStats() {
    return liveStats;
  }

  private Builder wsBuilder() {
    return webServiceClients.find(webserviceId).toBuilder();
  }

  private void saveWebService(Builder builder) {
    webServiceClients.set(builder.toWebServiceClient());
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
    return webservice.getFeatures();
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
    saveWebService(wsBuilder().removeFeature(name));
    loadWebService();
  }

  @Override
  public void saveFeature(boolean isNewFeature) {
    if (editFeatureOrigin.equals(editFeature.getClazz())) {
      return;
    }

    loadWebService();
    if (isExistingFeatureThrowMessage()) {
      return;
    }
    Builder wsBuilder = wsBuilder();
    if (isNewFeature) {
      wsBuilder.feature(editFeature.getClazz());
    } else {
      wsBuilder.removeFeature(editFeatureOrigin);
      wsBuilder.feature(editFeature.getClazz());
    }
    saveWebService(wsBuilder);
    loadWebService();
  }

}
