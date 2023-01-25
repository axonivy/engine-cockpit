package ch.ivyteam.enginecockpit.services.search;

import ch.ivyteam.enginecockpit.services.model.Elasticsearch.SearchEngineHealth;
import ch.ivyteam.ivy.elasticsearch.client.IndexName;
import ch.ivyteam.ivy.elasticsearch.index.IndexInfo;

public class SearchEngineIndex {

  private SearchEngineHealth status;
  private String size;
  private boolean reindexing;

  private IndexInfo info;

  public SearchEngineIndex(IndexInfo info, boolean reindexing) {
    this.info = info;
    this.status = SearchEngineHealth.getHealth(info.health());
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

  public SearchEngineHealth getStatus() {
    return status;
  }

  public void setStatus(SearchEngineHealth searchEngineHealth) {
    this.status = searchEngineHealth;
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
}
