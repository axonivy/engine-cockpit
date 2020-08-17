package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import java.lang.management.ManagementFactory;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.Monitor;

@ManagedBean
@ViewScoped
public class WebServiceMonitorBean
{
  private WebService currentWebService;
  private final Map<String, WebService> webServices = new LinkedHashMap<>();
  private String applicationName;
  private String environment;
  private String webServiceId;
  
  public WebServiceMonitorBean()
  {
    try
    {
      var services = ManagementFactory.getPlatformMBeanServer().queryNames(new ObjectName("ivy Engine:type=External Web Service,application=*,environment=*,name=*"), null);
      for (var webService : services)
      {
        var ws = new WebService(webService);
        webServices.put(ws.label(), ws);
      }
      webServices.put(WebService.NO_DATA.label(), WebService.NO_DATA);
      currentWebService = webServices.values().iterator().next();
    }
    catch(MalformedObjectNameException ex)
    {
      throw new RuntimeException(ex);
    }
  }
  
  public Monitor getCallsMonitor()
  {
    return currentWebService.callsMonitor();
  } 

  public Monitor getExecutionTimeMonitor()
  {
    return currentWebService.executionTimeMonitor();
  } 
  
  public String getWebService()
  {
    return currentWebService.label();
  }
  
  public void setWebService(String webService)
  {
    currentWebService = webServices.get(webService);
  }
  
  public void setWebService()
  {
    currentWebService = webServices
        .values()
        .stream()
        .filter(this::isWebService)
        .findAny()
        .orElse(WebService.NO_DATA);
  }
  
  private boolean isWebService(WebService webService)
  {
    return webService.application().equals(applicationName) &&
           webService.environment().equals(environment) &&
           webService.id().equals(webServiceId);
  }
  
  public Set<String> getWebServices()
  {
    return webServices.keySet();
  }
  
  public String getConfigurationLink()
  {
    if (currentWebService == WebService.NO_DATA)
    {
      return "webservices.xhtml";
    }
    else
    {
      return "webservicedetail.xhtml?applicationName="+currentWebService.application()+"&environment="+currentWebService.environment()+"&webserviceId="+currentWebService.id();
    }
  }
  
  public String getApplicationName()
  {
    return applicationName;
  }
  
  public String getEnvironment()
  {
    return environment;
  }
  
  public String getWebServiceId()
  {
    return webServiceId;
  }
  
  public void setApplicationName(String applicationName)
  {
    this.applicationName = applicationName;
    setWebService();
  }
  
  public void setEnvironment(String environment)
  {
    this.environment = environment;
    setWebService();
  }
  
  public void setWebServiceId(String webServiceId)
  {
    this.webServiceId = webServiceId;
    setWebService();
  }
}
