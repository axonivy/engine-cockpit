package ch.ivyteam.enginecockpit.services.model;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import ch.ivyteam.ivy.elasticsearch.client.ElasticSearchInfo;
import ch.ivyteam.ivy.elasticsearch.server.IElasticsearchServer;
import ch.ivyteam.ivy.elasticsearch.server.ServerConfig;

public class ElasticSearch {

  public interface APIS {
    List<String> SEARCH = List.of("/_cat/indices?format=json", "/_cat/aliases?format=json", "/_cluster/health");
    List<String> INDEX = List.of("/mapping");
  }

  private String clusterName = "unknown";
  private SearchEngineHealth health = SearchEngineHealth.UNKNOWN;
  private String version = "unknown";

  public ElasticSearch(ElasticSearchInfo info) {
    if (info != null) {
      clusterName = info.clusterName();
      version = info.version();
      health = SearchEngineHealth.getHealth(info.health());
    }
  }

  public String getServerUrl() {
    return ServerConfig.instance().getServerUrl();
  }

  public String getClusterName() {
    return clusterName;
  }

  public String getVersion() {
    return version;
  }

  public SearchEngineHealth getHealth() {
    return health;
  }

  public boolean isNotSupported() {
    return !version.startsWith("7.17");
  }

  public Optional<String> executeRequest(String path) {
    var webTarget = IElasticsearchServer.instance().getClient();
    try (var response = webTarget.path(path).request().get()) {
      var node = response.readEntity(JsonNode.class);
      if (node == null) {
        return Optional.empty();
      }
      return Optional.of(node.toPrettyString());
    }
  }

  public static enum SearchEngineHealth {

    GREEN("green", "check-circle-1", "Everything is ok"),
    YELLOW("yellow", "check-circle-1", "Everything is ok, if you run on a single node cluster, like the internal ivy ES, this is normal."
                                     + "On an external multi node cluster this can indicate some upcoming issues. Please check the ES logs."),
    RED("red", "remove-circle", "There is a problem which needs your attention. Some data may be unavailable or functions are not working correctly."),
    UNKNOWN("unknown", "question-circle", "Health state unknown");

    private final String state;
    private final String icon;
    private final String hint;

    private SearchEngineHealth(String state, String icon, String hint) {
      this.state = state;
      this.icon = icon;
      this.hint = hint;
    }

    public String getState() {
      return state;
    }

    public String getIcon() {
      return icon;
    }

    public String getHint() {
      return hint;
    }

    public static SearchEngineHealth getHealth(String health) {
      if (GREEN.state.equals(health)) {
        return GREEN;
      } else if (YELLOW.state.equals(health)) {
        return YELLOW;
      } else if (RED.state.equals(health)) {
        return RED;
      } else {
        return UNKNOWN;
      }
    }
  }
}
