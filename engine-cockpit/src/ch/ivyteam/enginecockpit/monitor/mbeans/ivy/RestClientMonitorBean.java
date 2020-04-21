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
public class RestClientMonitorBean
{
  private RestClient currentRestClient;
  private final Map<String, RestClient> restClients = new LinkedHashMap<>();
  private String applicationName;
  private String environment;
  private String restClientName;
  
  public RestClientMonitorBean()
  {
    try
    {
      Set<ObjectName> clients = ManagementFactory.getPlatformMBeanServer().queryNames(new ObjectName("ivy Engine:type=External REST Web Service,application=*,environment=*,name=*"), null);
      for (ObjectName client : clients)
      {
        var restClient = new RestClient(client);
        restClients.put(restClient.label(), restClient);
      }
      restClients.put(RestClient.NO_DATA.label(), RestClient.NO_DATA);
      currentRestClient = restClients.values().iterator().next();
    }
    catch(MalformedObjectNameException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  public Monitor getConnectionsMonitor()
  {
    return currentRestClient.connectionsMonitor();
  }

  public Monitor getCallsMonitor()
  {
    return currentRestClient.callsMonitor();
  } 

  public Monitor getExecutionTimeMonitor()
  {
    return currentRestClient.executionTimeMonitor();
  } 
  
  public String getRestClient()
  {
    return currentRestClient.label();
  }
  
  public void setRestClient(String restClient)
  {
    currentRestClient = restClients.get(restClient);
  }
  
  public void setRestClient()
  {
    currentRestClient = restClients.getOrDefault(
        RestClient.toLabel(applicationName, environment, restClientName), 
        RestClient.NO_DATA);
  }
  
  public Set<String> getRestClients()
  {
    return restClients.keySet();
  }
  
  public String getConfigurationLink()
  {
    if (currentRestClient == RestClient.NO_DATA)
    {
      return "restclients.xhtml";
    }
    else
    {
      return "restclientdetail.xhtml?applicationName="+currentRestClient.application()+"&environment="+currentRestClient.environment()+"&restClientName="+currentRestClient.name();
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
  
  public String getRestClientName()
  {
    return restClientName;
  }
  
  public void setApplicationName(String applicationName)
  {
    this.applicationName = applicationName;
    setRestClient();
  }
  
  public void setEnvironment(String environment)
  {
    this.environment = environment;
    setRestClient();
  }
  
  public void setRestClientName(String restClientName)
  {
    this.restClientName = restClientName;
    setRestClient();
  }
}
