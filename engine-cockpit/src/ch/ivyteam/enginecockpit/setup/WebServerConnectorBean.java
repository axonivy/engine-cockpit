package ch.ivyteam.enginecockpit.setup;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

import ch.ivyteam.enginecockpit.setup.WizardBean.StepStatus;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class WebServerConnectorBean extends StepStatus {
  private static final String ENABLED = "Enabled";
  private static final String PORT = "Port";
  private static final String CONNECTOR_HTTP = "Connector.HTTP.";
  private static final String CONNECTOR_HTTPS = "Connector.HTTPS.";

  private boolean httpEnabled;
  private int httpPort;
  private boolean httpsEnabled;
  private int httpsPort;

  public WebServerConnectorBean() {
    httpEnabled = IConfiguration.instance().getOrDefault(CONNECTOR_HTTP + ENABLED, Boolean.class);
    httpPort = IConfiguration.instance().getOrDefault(CONNECTOR_HTTP + PORT, int.class);

    httpsEnabled = IConfiguration.instance().getOrDefault(CONNECTOR_HTTPS + ENABLED, Boolean.class);
    httpsPort = IConfiguration.instance().getOrDefault(CONNECTOR_HTTPS + PORT, int.class);
  }

  public boolean isHttpEnabled() {
    return httpEnabled;
  }

  public void setHttpEnabled(boolean httpEnabled) {
    this.httpEnabled = httpEnabled;
    setConfig(CONNECTOR_HTTP + ENABLED, httpEnabled);
  }

  public int getHttpPort() {
    return httpPort;
  }

  public void setHttpPort(int httpPort) {
    this.httpPort = httpPort;
    setConfig(CONNECTOR_HTTP + PORT, httpPort);
  }

  public boolean isHttpsEnabled() {
    return httpsEnabled;
  }

  public void setHttpsEnabled(boolean httpsEnabled) {
    this.httpsEnabled = httpsEnabled;
    setConfig(CONNECTOR_HTTPS + ENABLED, httpsEnabled);
  }

  public int getHttpsPort() {
    return httpsPort;
  }

  public void setHttpsPort(int httpsPort) {
    this.httpsPort = httpsPort;
    setConfig(CONNECTOR_HTTPS + PORT, httpsPort);
  }

  private void setConfig(String key, Object httpEnabled) {
    IConfiguration.instance().set(key, httpEnabled);
    FacesContext.getCurrentInstance().addMessage("",
        new FacesMessage(FacesMessage.SEVERITY_INFO, "'" + key + "' changed successfully", ""));
  }

  @Override
  public boolean isStepOk() {
    return isHttpEnabled() || isHttpsEnabled();
  }

  @Override
  public String getStepWarningMessage() {
    return "Enable at least the HTTP or HTTPS Connector";
  }
}
