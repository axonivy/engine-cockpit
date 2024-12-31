package ch.ivyteam.enginecockpit.services.search;

import javax.ws.rs.core.UriBuilder;

import ch.ivyteam.enginecockpit.services.model.SearchEngine.SearchEngineHealth;
import ch.ivyteam.ivy.searchengine.client.IndexName;
import ch.ivyteam.ivy.searchengine.index.IndexInfo;

public class SearchEngineIndex {

  private final IndexInfo info;
  private final boolean reindexing;

  public SearchEngineIndex(IndexInfo info, boolean reindexing) {
    this.info = info;
    this.reindexing = reindexing;
  }

  public IndexName getIndexName() {
    return info.indexName();
  }

  public String getName() {
    return info.indexName().name();
  }

  public String getViewUrl() {
    return UriBuilder.fromPath("searchindex.xhtml")
        .queryParam("index", info.indexName().name())
        .build()
        .toString();
  }

  public long getCountIndexed() {
    return info.indexCount();
  }

  public long getCountIndexing() {
    return info.reindexingCount();
  }

  public double getPercentIndexed() {
    var percent = info.reindexingCount() * 100.0 / info.storeCount();
    return Math.round(percent * 10.0) / 10.0;
  }

  public long getCountStored() {
    return info.storeCount();
  }

  public SearchEngineHealth getHealth() {
    return SearchEngineHealth.getHealth(info.health());
  }

  public IndexStatus getStatus() {
    return IndexStatus.getStatus(info.status());
  }

  public String getSize() {
    return info.size();
  }

  public boolean isReindexing() {
    return reindexing;
  }

  public String getStatusAsString() {
    return info.status();
  }

  public enum IndexStatus {
    OPEN("open", "pi pi-lock-open state-active", "Everything is okay, the index is open."),
    CLOSED("closed", "pi pi-lock-closed state-inactive", "It seems like your machine is out of disk space. Please check your search engine watermark settings."),
    UNKNOWN("unknown", "si si-question-circle", "");

    private final String state;
    private final String icon;
    private final String hint;

    IndexStatus(String status, String icon, String hint) {
      this.state = status;
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

    public static IndexStatus getStatus(String status) {
      if (OPEN.getState().equals(status)) {
        return OPEN;
      } else if (CLOSED.getState().equals(status)) {
        return CLOSED;
      } else {
        return UNKNOWN;
      }
    }
  }
}
