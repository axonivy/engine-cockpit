package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.ivy.business.data.store.search.internal.elasticsearch.IndexName;

@SuppressWarnings("restriction")
public class SearchEngineIndex
{
  private IndexName index;
  private String name;
  private long countIndexed;
  private long countStored;
  private String status; //TODO: enum?
  
  public SearchEngineIndex(IndexName index, long countIndexed, long countStored, String status)
  {
    this.index = index;
    this.name = index.getName();
    this.countIndexed = countIndexed;
    this.countStored = countStored;
    this.status = status;
  }
  
  public IndexName getIndex()
  {
    return index;
  }

  public String getName()
  {
    return name;
  }

  public long getCountIndexed()
  {
    return countIndexed;
  }

  public long getCountStored()
  {
    return countStored;
  }

  public String getStatus()
  {
    return status;
  }
  
  
}
