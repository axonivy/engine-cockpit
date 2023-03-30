package ch.ivyteam.enginecockpit.services.search;

import ch.ivyteam.enginecockpit.services.model.Elasticsearch.SearchEngineHealth;
import ch.ivyteam.ivy.elasticsearch.client.IndexName;
import ch.ivyteam.ivy.elasticsearch.index.IndexInfo;

public class SearchEngineIndex {

  private SearchEngineHealth health;
  private IndexStatus status;
  private String statusAsString;
  private String size;
  private boolean reindexing;

  private IndexInfo info;

  public SearchEngineIndex(IndexInfo info, boolean reindexing) {
    this.info = info;
    this.health = SearchEngineHealth.getHealth(info.health());
    this.statusAsString = info.status();
    this.status = IndexStatus.getStatus(statusAsString);
    this.size = info.size();
    this.reindexing = reindexing;
  }

  public IndexName getIndexName() {
    return info.indexName();
  }

  public String getName() {
    return info.indexName().name();
  }

  public long getCountIndexed() {
    return info.indexCount();
  }

  public long getCountStored() {
    return info.storeCount();
  }

  public SearchEngineHealth getHealth() {
    return health;
  }

  public IndexStatus getStatus() {
    return status;
  }

  public String getSize() {
    return size;
  }

  public void setSize(String size) {
    this.size = size;
  }

  public boolean isReindexing() {
    return reindexing;
  }

  public void setReindexing(boolean running) {
    this.reindexing = running;
  }

  public String getStatusAsString() {
    return statusAsString;
  }

  public static enum IndexStatus {
    OPEN("open", "pi pi-lock-open state-active", "Everything is okay, the index is open."),
    CLOSED("closed", "pi pi-lock-closed state-inactive", "It seems like your machine is out of disk space. Please check your Elasticsearch Watermark settings."),
    UNKNOWN("unknown", "si si-question-circle", "Index status is unknown.");

    private final String state;
    private final String icon;
    private final String hint;

    private IndexStatus(String status, String icon, String hint) {
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
