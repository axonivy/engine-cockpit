package ch.ivyteam.enginecockpit.model;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ch.ivyteam.enginecockpit.util.Authenticator;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.environment.Ivy;

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
  
  public ElasticSearch(String serverUrl)
  {
    this.username = IConfiguration.get().getOrDefault("Elasticsearch.ExternalServer.UserName");
    this.password = IConfiguration.get().getOrDefault("Elasticsearch.ExternalServer.Password");
    this.serverUrl = serverUrl;
    initElasticSearchInfos();
  }
  
  private void initElasticSearchInfos()
  {
    try
    {
      executeRequestJsonResponse(serverUrl)
              .ifPresent(response -> {
                clusterName = response.getAsJsonObject().get("cluster_name").getAsString();
                version = response.getAsJsonObject().get("version").getAsJsonObject().get("number").getAsString();
              });
      executeRequestJsonResponse(serverUrl + ElasticSearchApi.HEALTH_URL)
              .ifPresent(response -> {
                health = SearchEngineHealth.getHealth(response.getAsJsonObject().get("status").getAsString());
              });
    }
    catch (Exception ex)
    {
      Ivy.log().info(ex.getMessage());
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
  
  public void evaluateAdditionalIndicesInformation(List<SearchEngineIndex> indices)
  {
    executeRequestJsonResponse(serverUrl + ElasticSearchApi.INDICIES_URL)
            .ifPresent(response -> response.getAsJsonArray().forEach(element -> {
              if (element.isJsonObject())
              {
                JsonObject jsonIndex = element.getAsJsonObject();
                getIndexFromList(indices, jsonIndex.get("index").getAsString())
                        .ifPresent(index -> {
                          index.setStatus(SearchEngineHealth.getHealth(jsonIndex.get("health").getAsString()));
                          index.setSize(jsonIndex.get("store.size").getAsString());
                        });
              }
            }));
  }
  
  public void evaluateAliasForIndices(List<SearchEngineIndex> indices)
  {
    executeRequestJsonResponse(serverUrl + ElasticSearchApi.ALIASES_URL)
            .ifPresent(response -> response.getAsJsonArray().forEach(element -> {
              if (element.isJsonObject())
              {
                JsonObject jsonIndex = element.getAsJsonObject();
                getAliasFromList(indices, jsonIndex.get("alias").getAsString())
                        .ifPresent(index -> index.setIndex(jsonIndex.get("index").getAsString()));
              }
            }));
    
  }

  private Optional<SearchEngineIndex> getIndexFromList(List<SearchEngineIndex> indices, String jsonElementIndex)
  {
    return indices.stream().filter(index -> StringUtils.equals(index.getIndex(), jsonElementIndex)).findFirst();
  }
  
  private Optional<SearchEngineIndex> getAliasFromList(List<SearchEngineIndex> indices, String jsonElementIndex)
  {
    return indices.stream().filter(index -> StringUtils.equals(index.getName(), jsonElementIndex)).findFirst();
  }
  
  private Optional<JsonElement> executeRequestJsonResponse(String url)
  {
    return executeRequest(url).map(response -> Optional.ofNullable(new Gson().fromJson(response, JsonElement.class)))
            .orElse(Optional.empty());
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
    GREEN("green", "check"),
    YELLOW("yellow", "check"),
    RED("red", "close"),
    UNKNOWN("unknown", "help_outline");
    
    private final String state;
    private final String icon;

    private SearchEngineHealth(String state, String icon)
    {
      this.state = state;
      this.icon = icon;
    }
    
    public String getState()
    {
      return state;
    }
    
    public String getIcon()
    {
      return icon;
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
