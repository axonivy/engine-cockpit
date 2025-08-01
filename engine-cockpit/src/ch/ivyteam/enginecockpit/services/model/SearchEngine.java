package ch.ivyteam.enginecockpit.services.model;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import ch.ivyteam.ivy.searchengine.client.SearchEngineInfo;
import ch.ivyteam.ivy.searchengine.client.Watermark;
import ch.ivyteam.ivy.searchengine.server.ISearchEngineServer;
import ch.ivyteam.ivy.searchengine.server.ServerConfig;

public class SearchEngine {

  public interface APIS {
    List<String> SEARCH = List.of("_cat/indices?format=json", "_cat/aliases?format=json", "_cluster/health");
    List<String> INDEX = List.of("_mapping");
  }

  private final SearchEngineInfo info;
  private final Watermark watermark;

  public SearchEngine(SearchEngineInfo info, Watermark watermark) {
    this.info = info;
    this.watermark = watermark;
  }

  public String getServerUrl() {
    return ServerConfig.instance().getServerUrl();
  }

  public String getClusterName() {
    return info.clusterName();
  }

  public String getVersion() {
    return info.version();
  }

  public SearchEngineHealth getHealth() {
    return SearchEngineHealth.getHealth(info.health());
  }

  public boolean isNotSupported() {
    return !info.supported();
  }

  public Optional<String> executeRequest(String path) {
    var client = ISearchEngineServer.instance().getClient().client();
    try (var response = client.target(getServerUrl() + path).request().get()) {
      var node = response.readEntity(JsonNode.class);
      if (node == null) {
        return Optional.empty();
      }
      return Optional.of(node.toPrettyString());
    }
  }

  public Watermark getWatermark() {
    return watermark;
  }

  public enum SearchEngineHealth {

    GREEN("green", "check-circle-1", "Everything is ok"),
    YELLOW("yellow", "check-circle-1", "Everything is ok, if you run on a single node cluster, like the internal ivy ES, this is normal."
        + "On an external multi node cluster this can indicate some upcoming issues. Please check the ES logs."),
    RED("red", "remove-circle", "There is a problem which needs your attention. Some data may be unavailable or functions are not working correctly."),
    UNKNOWN("unknown", "question-circle", "Health state unknown");

    private final String state;
    private final String icon;
    private final String hint;

    SearchEngineHealth(String state, String icon, String hint) {
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
