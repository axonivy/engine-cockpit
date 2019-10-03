package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.enginecockpit.model.ElasticSearch.SearchEngineHealth;
import ch.ivyteam.ivy.business.data.store.search.internal.elasticsearch.IndexName;

@SuppressWarnings("restriction")
public class SearchEngineIndex
{
  private IndexName indexName;
  private String index;
  private String alias;
  private long countIndexed;
  private long countStored;
  private SearchEngineHealth status;
  private String size;
  private boolean reindexing;
  
  public SearchEngineIndex(IndexName index, long countIndexed, long countStored, String size, String health, boolean reindexing)
  {
    this.indexName = index;
    this.alias = index.getName();
    this.index = index.getName();
    this.countIndexed = countIndexed;
    this.countStored = countStored;
    this.status = SearchEngineHealth.getHealth(health);
    this.size = size;
    this.reindexing = reindexing;
  }
  
  public IndexName getIndexName()
  {
    return indexName;
  }
  
  public String getIndex()
  {
    return index;
  }
  
  public void setIndex(String index)
  {
    this.index = index;
  }

  public String getName()
  {
    return alias;
  }

  public long getCountIndexed()
  {
    return countIndexed;
  }

  public long getCountStored()
  {
    return countStored;
  }

  public SearchEngineHealth getStatus()
  {
    return status;
  }
  
  public void setStatus(SearchEngineHealth searchEngineHealth)
  {
    this.status = searchEngineHealth;
  }
  
  public String getSize()
  {
    return size;
  }
  
  public void setSize(String size)
  {
    this.size = size;
  }
  
  public boolean isReindexing()
  {
    return reindexing;
  }
  
  public void setReindexing(boolean running)
  {
    this.reindexing = running;
  }
  
}
