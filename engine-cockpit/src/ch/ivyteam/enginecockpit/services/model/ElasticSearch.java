package ch.ivyteam.enginecockpit.services.model;

import java.util.Optional;

import javax.ws.rs.client.ClientBuilder;

import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.elasticsearch.client.ElasticSearchInfo;

@SuppressWarnings("restriction")
public class ElasticSearch {
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

  public ElasticSearch(String serverUrl, ElasticSearchInfo info) {
    this.username = IConfiguration.instance().getOrDefault("Elasticsearch.ExternalServer.UserName");
    this.password = IConfiguration.instance().getOrDefault("Elasticsearch.ExternalServer.Password");
    this.serverUrl = serverUrl;
    if (info != null) {
      clusterName = info.clusterName();
      version = info.version();
      health = SearchEngineHealth.getHealth(info.health());
    }
  }

  public String getServerUrl() {
    return serverUrl;
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

  public Optional<String> executeRequest(String url) {
    var client = ClientBuilder.newClient();
    client.register(new Authenticator(username, password));
    try (var response = client.target(url).request().get()) {
      if (response.getStatus() == 200) {
        return Optional.ofNullable(response.readEntity(String.class));
      }
      return Optional.empty();
    }
  }

  public static enum SearchEngineHealth {
    GREEN("green", "check-circle-1", "Everything is ok"), YELLOW("yellow", "check-circle-1",
            "Everything is ok, "
                    + "if you run on a single node cluster, like the internal ivy ES, this is normal. "
                    + "On an external multi node cluster this can indicate some upcoming issues. Please check the ES logs."), RED(
                            "red", "remove-circle",
                            "There is a problem which needs your attention. Some data may be unavailable or functions are not working correctly."), UNKNOWN(
                                    "unknown", "question-circle", "Health state unknown");

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
