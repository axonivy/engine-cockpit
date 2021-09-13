package ch.ivyteam.enginecockpit.services.model;

import ch.ivyteam.enginecockpit.services.model.ElasticSearch.SearchEngineHealth;
import ch.ivyteam.ivy.business.data.store.restricted.IndexInfo;
import ch.ivyteam.ivy.business.data.store.search.internal.elasticsearch.IndexName;

@SuppressWarnings("restriction")
public class SearchEngineIndex {
  private IndexName indexName;
  private long countIndexed;
  private long countStored;
  private SearchEngineHealth status;
  private String size;
  private boolean reindexing;

  public SearchEngineIndex(IndexInfo info, boolean reindexing) {
    this.indexName = info.getIndexName();
    this.countIndexed = info.getIndexCount();
    this.countStored = info.getStoreCount();
    this.status = SearchEngineHealth.getHealth(info.getHealth());
    this.size = info.getSize();
    this.reindexing = reindexing;
  }

  public IndexName getIndexName() {
    return indexName;
  }

  public String getName() {
    return indexName.getName();
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
