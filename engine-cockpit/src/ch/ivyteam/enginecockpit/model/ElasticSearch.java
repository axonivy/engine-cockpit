package ch.ivyteam.enginecockpit.model;

import java.util.Optional;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import ch.ivyteam.enginecockpit.util.Authenticator;
import ch.ivyteam.ivy.business.data.store.restricted.ElasticSearchInfo;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@SuppressWarnings("restriction")
public class ElasticSearch
{
  public interface ElasticSearchApi {
    String INDICIES_URL = "/_cat/indices?format=json";
    String ALIASES_URL = "/_cat/aliases?format=json";
    String HEALTH_URL = "/_cluster/health";
  }
  
  public interface ElasticSearchIndexApi {
    String MAPPING_URL = "/_mapping";
  }
 
  private final String username;
  private final String password;
  private final String serverUrl;
  private String clusterName = "unknown";
  private SearchEngineHealth health = SearchEngineHealth.UNKNOWN;
  private String version = "unknown";
  
  public ElasticSearch(String serverUrl, ElasticSearchInfo elasticSearchInfo)
  {
    this.username = IConfiguration.get().getOrDefault("Elasticsearch.ExternalServer.UserName");
    this.password = IConfiguration.get().getOrDefault("Elasticsearch.ExternalServer.Password");
    this.serverUrl = serverUrl;
    if (elasticSearchInfo != null)
    {
      clusterName = elasticSearchInfo.getClusterName();
      version = elasticSearchInfo.getVersion();
      health = SearchEngineHealth.getHealth(elasticSearchInfo.getHealth());
    }
  }
  
  public String getServerUrl()
  {
    return serverUrl;
  }

  public String getClusterName()
  {
    return clusterName;
  }
  
  public String getVersion()
  {
    return version;
  }
  
  public SearchEngineHealth getHealth()
  {
    return health;
  }
  
  public Optional<String> executeRequest(String url)
  {
    Client client = ClientBuilder.newClient();
    client.register(new Authenticator(username, password));
    try (Response response = client.target(url).request().get())
    {
      if (response.getStatus() == 200)
      {
        return Optional.ofNullable(response.readEntity(String.class));
      }
      return Optional.empty();
    }
  }
  
  public static enum SearchEngineHealth {
    GREEN("green", "check", "Everything is ok"),
    YELLOW("yellow", "check", "Everything is ok, all data is saved in the system database"),
    RED("red", "close", "There is a problem, so some data is unavailable"),
    UNKNOWN("unknown", "help_outline", "Health state unknown");
    
    private final String state;
    private final String icon;
    private final String hint;

    private SearchEngineHealth(String state, String icon, String hint)
    {
      this.state = state;
      this.icon = icon;
      this.hint = hint;
    }
    
    public String getState()
    {
      return state;
    }
    
    public String getIcon()
    {
      return icon;
    }
    
    public String getHint()
    {
      return hint;
    }
    
    public static SearchEngineHealth getHealth(String health)
    {
      if (GREEN.state.equals(health))
      {
        return GREEN;
      }
      else if (YELLOW.state.equals(health))
      {
        return YELLOW;
      }
      else if (RED.state.equals(health))
      {
        return RED;
      }
      else 
      {
        return UNKNOWN;
      }
    }
  }

}
