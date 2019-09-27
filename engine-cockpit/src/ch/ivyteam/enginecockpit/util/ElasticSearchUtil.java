package ch.ivyteam.enginecockpit.util;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ch.ivyteam.enginecockpit.model.SearchEngineIndex;

public class ElasticSearchUtil
{
  public static final String MAPPING_URL = "/_mapping";
  public static final String INDICIES_URL = "/_cat/indices?format=json";
  public static final String ALIASES_URL = "/_cat/aliases?format=json";
  public static final String HEALTH_URL = "/_cluster/health";
  
  public static String getClusterName(String serverUrl)
  {
    return executeRequest(serverUrl)
            .map(response -> response.getAsJsonObject().get("cluster_name").getAsString())
            .orElse("unknown");
  }
  
  public static String getVersion(String serverUrl)
  {
    return executeRequest(serverUrl)
            .map(response -> response.getAsJsonObject().get("version").getAsJsonObject().get("number").getAsString())
            .orElse("unknown");
  }
  
  public static String getHealth(String serverUrl)
  {
    return executeRequest(serverUrl + HEALTH_URL)
            .map(response -> response.getAsJsonObject().get("status").getAsString())
            .orElse("unknown");
  }
  
  public static void evaluateAdditionalIndicesInformation(String serverUrl, List<SearchEngineIndex> indices)
  {
    executeRequest(serverUrl + INDICIES_URL)
            .ifPresent(response -> response.getAsJsonArray().forEach(element -> {
              if (element.isJsonObject())
              {
                JsonObject jsonIndex = element.getAsJsonObject();
                getIndexFromList(indices, jsonIndex.get("index").getAsString())
                        .ifPresent(index -> {
                          index.setStatus(jsonIndex.get("health").getAsString());
                          index.setSize(jsonIndex.get("store.size").getAsString());
                        });
              }
            }));
  }
  
  public static void evaluateAliasForIndices(String serverUrl, List<SearchEngineIndex> indices)
  {
    executeRequest(serverUrl + ALIASES_URL)
            .ifPresent(response -> response.getAsJsonArray().forEach(element -> {
              if (element.isJsonObject())
              {
                JsonObject jsonIndex = element.getAsJsonObject();
                getAliasFromList(indices, jsonIndex.get("alias").getAsString())
                        .ifPresent(index -> index.setIndex(jsonIndex.get("index").getAsString()));
              }
            }));
    
  }

  private static Optional<SearchEngineIndex> getIndexFromList(List<SearchEngineIndex> indices, String jsonElementIndex)
  {
    return indices.stream().filter(index -> StringUtils.equals(index.getIndex(), jsonElementIndex)).findFirst();
  }
  
  private static Optional<SearchEngineIndex> getAliasFromList(List<SearchEngineIndex> indices, String jsonElementIndex)
  {
    return indices.stream().filter(index -> StringUtils.equals(index.getName(), jsonElementIndex)).findFirst();
  }
  
  private static Optional<JsonElement> executeRequest(String url)
  {
    Client client = ClientBuilder.newClient();
    try (Response response = client.target(url).request().get())
    {
      if (response.getStatus() == 200)
      {
        String json = response.readEntity(String.class);
        return Optional.ofNullable(new Gson().fromJson(json, JsonElement.class));
      }
      return Optional.empty();
    }
  }

}
