package ch.ivyteam.enginecockpit.services;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.net.ssl.SSLContext;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

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
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.jersey.client.JerseyClientBuilder;
import ch.ivyteam.ivy.ssl.restricted.SslClientSettings;
import ch.ivyteam.ivy.webservice.client.WebServiceClient.Builder;
import ch.ivyteam.ivy.webservice.client.WebServiceClients;

@ManagedBean
@ViewScoped
public class WebserviceDetailBean extends HelpServices implements IConnectionTestResult {

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
    app = IApplicationRepository.instance().findByName(appName).orElse(null);
    if (app == null) {
      ResponseHelper.notFound("Application '" + appName + "' not found");
      return;
    }
    webServiceClients = WebServiceClients.of(app);
    loadWebService();
    liveStats = new WebServiceMonitor(appName, webserviceId);
  }

  public String getViewUrl() {
    return webservice.getViewUrl(appName);
  }

  private void loadWebService() {
    var ws = webServiceClients.find(webserviceId);
    if (ws == null) {
      ResponseHelper.notFound("Web service '" + webserviceId + "' not found");
      return;
    }
    webservice = new Webservice(ws);
  }

  public Webservice getWebservice() {
    return webservice;
  }

  @Override
  public String getTitle() {
    return "Web Service '" + webservice.getName() + "'";
  }

  @Override
  public String getGuideText() {
    return "To edit your Web Service overwrite your app.yaml file. For example copy and paste the snippet below.";
  }

  @Override
  public String getYaml() {
    var valuesMap = new HashMap<String, String>();
    valuesMap.put("name", webservice.getName());
    valuesMap.put("endpoints", parseEndpointsToYaml(webservice.getPortTypeMap()));
    valuesMap.put("features", parseFeaturesToYaml(webservice.getFeatures()));
    valuesMap.put("properties", parsePropertiesToYaml(webservice.getProperties()));
    var templateString = readTemplateString("webservice.yaml");
    var strSubstitutor = new StrSubstitutor(valuesMap);
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
    testResult = (ConnectionTestResult) connectionTest.test(() -> testConnection());
  }

  private ConnectionTestResult testConnection() {
    javax.ws.rs.client.Client client = createClient();
    if (authSupportedForTesting()) {
      client.register(new Authenticator(webservice.getUsername(), webservice.getPassword()));
    }
    try {
      int status = client.target(activeEndpointUrl).request().post(Entity.json("")).getStatus();
      if (status == 401) {
        return new ConnectionTestResult("POST", status, TestResult.WARNING,
                "Authentication (only HttpBasic supported) was not successful");
      } else if (status == 404) {
        return new ConnectionTestResult("POST", status, TestResult.WARNING, "Service not found");
      } else {
        return new ConnectionTestResult("POST", status, TestResult.SUCCESS,
                "Successfully reached web service");
      }
    } catch (ProcessingException ex) {
      return new ConnectionTestResult("", 0, TestResult.ERROR,
              "The URL seems to be not correct or contains scripting context (can not be evaluated)\n"
                      + "An error occurred: " + ExceptionUtils.getStackTrace(ex));
    }
  }

  private Client createClient() {
    var client =  JerseyClientBuilder.create("Client")
            .sslContext(createSSLContext())
            .insecureSsl(SslClientSettings.instance().isInsecureSSLEnabled())
            .toClient();
    return client;
  }

  private SSLContext createSSLContext() {
    var sslConfig = new ch.ivyteam.ivy.ssl.restricted.SslConfig(false, "", SslClientSettings.instance());
    var ivySslContext = new ch.ivyteam.ivy.ssl.restricted.IvySslContext(sslConfig);
    return ivySslContext.getSSLContext();
  }

  private boolean authSupportedForTesting() {
    return StringUtils.equals(webservice.getAuthType(), "HttpBasic")
            || StringUtils.equals(webservice.getAuthType(), "HTTP_BASIC");
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
    var msg = new FacesMessage("Web Service configuration saved", "");
    FacesContext.getCurrentInstance().addMessage("wsConfigMsg", msg);
    loadWebService();
  }

  public void resetConfig() {
    connectionTest.stop();
    webServiceClients.remove(webservice.getName());
    var msg = new FacesMessage("Web Service configuration reset", "");
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
    var msg = new FacesMessage("EndPoint saved", "");
    FacesContext.getCurrentInstance().addMessage("wsConfigMsg", msg);
    loadWebService();
  }

  @Override
  public ConnectionTestResult getResult() {
    return testResult;
  }

  public WebServiceMonitor getLiveStats() {
    return liveStats;
  }

  private Builder wsBuilder() {
    return webServiceClients.find(webserviceId).toBuilder();
  }

}
