package ch.ivyteam.enginecockpit.system;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.model.ConfigProperty;
import ch.ivyteam.ivy.configuration.restricted.ConfigValueFormat;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.request.RequestUriFactory;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class WebServerBean
{
  public static String FRONTEND_HOST_KEY = "Frontend.HostName";
  public static String FRONTEND_PORT_KEY = "Frontend.Port";
  public static String FRONTEND_PROTOCOL_KEY = "Frontend.Protocol";
  
  private ConfigProperty frontendHost;
  private ConfigProperty frontendPort;
  private ConfigProperty frontendProtocol;

  public WebServerBean()
  {
    var config = IConfiguration.get();
    frontendHost = new ConfigProperty(config.getProperty(FRONTEND_HOST_KEY).get());
    frontendPort = new ConfigProperty(config.getProperty(FRONTEND_PORT_KEY).get());
    frontendProtocol = new ConfigProperty(config.getProperty(FRONTEND_PROTOCOL_KEY).get());
  }

  public List<RequestData> getRequestData()
  {
    var request = getRequest();
    var requestData = new ArrayList<RequestData>();
    request.getHeaderNames().asIterator().forEachRemaining(header -> requestData.add(new RequestData(header, request.getHeader(header))));
    return requestData;
  }

  public String getExternalUrl()
  {
    return RequestUriFactory.createServerUri(getRequest()).toString();
  }
  
  public String getFrontendUrl()
  {
    return RequestUriFactory.createExternalServerUri().toString();
  }
  
  public String getEvaluatedUrl()
  {
    return getExternalUrl() + getRequest().getRequestURI();
  }
  
  public String getRemoteAddr()
  {
    return getRequest().getRemoteAddr();
  }

  public String getFrontendHost()
  {
    return frontendHost.getValue();
  }

  public void setFrontendHost(String frontendHost)
  {
    this.frontendHost.setValue(frontendHost);
  }

  public String getFrontendPort()
  {
    return frontendPort.getValue();
  }

  public void setFrontendPort(String frontendPort)
  {
    if (StringUtils.isBlank(frontendPort))
    {
      frontendPort = "0";
    }
    this.frontendPort.setValue(frontendPort);
  }

  public String getFrontendProtocol()
  {
    return frontendProtocol.getValue();
  }

  public void setFrontendProtocol(String frontendProtocol)
  {
    this.frontendProtocol.setValue(frontendProtocol);
  }
  
  public List<String> getFrontendProtocols()
  {
    return frontendProtocol.getEnumerationValues();
  }
  
  public void saveFrontend()
  {
    saveConfig(frontendHost);
    saveConfig(frontendPort);
    saveConfig(frontendProtocol);
    FacesContext.getCurrentInstance().addMessage("frontendSaveGrowl",
            new FacesMessage("Frontend configuration changes saved"));
  }
  
  public void saveConfig(ConfigProperty property)
  {
    var config = IConfiguration.get();
    if (property.getValue().equals(property.getDefaultValue()))
    {
      config.remove(property.getKey());
    }
    else 
    {
      if (ConfigValueFormat.NUMBER.name().equals(property.getConfigValueFormat()))
      {
        config.set(property.getKey(), Integer.valueOf(property.getValue()));
      }
      config.set(property.getKey(), property.getValue());
    }
  }
  
  private HttpServletRequest getRequest()
  {
    return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
  }
  
  public class RequestData
  {
    private String data;
    private String value;

    RequestData(String data, String value)
    {
      this.data = data;
      this.value = value;
    }
    
    public String getData()
    {
      return data;
    }
    
    public String getValue()
    {
      return value;
    }
  }
}
