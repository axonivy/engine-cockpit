package ch.ivyteam.enginecockpit.services.model;

import ch.ivyteam.enginecockpit.services.model.ElasticSearch.SearchEngineHealth;
import ch.ivyteam.ivy.business.data.store.restricted.BusinessDataIndex;
import ch.ivyteam.ivy.business.data.store.restricted.IndexInfo;

@SuppressWarnings("restriction")
public class SearchEngineIndex {

  private BusinessDataIndex indexName;
  private long countIndexed;
  private long countStored;
  private SearchEngineHealth status;
  private String size;
  private boolean reindexing;

  public SearchEngineIndex(IndexInfo info, boolean reindexing) {
    this.indexName = info.indexName();
    this.countIndexed = info.indexCount();
    this.countStored = info.storeCount();
    this.status = SearchEngineHealth.getHealth(info.health());
    this.size = info.size();
    this.reindexing = reindexing;
  }

  public BusinessDataIndex getIndexName() {
    return indexName;
  }

  public String getName() {
    return indexName.name();
  }

  public long getCountIndexed() {
    return countIndexed;
  }

  public long getCountStored() {
    return countStored;
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
