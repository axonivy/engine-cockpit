package ch.ivyteam.enginecockpit;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import ch.ivyteam.di.restricted.DiCore;
import ch.ivyteam.enginecockpit.model.SearchEngineIndex;
import ch.ivyteam.enginecockpit.util.ElasticSearchUtil;
import ch.ivyteam.ivy.business.data.store.search.internal.elasticsearch.ElasticSearchServerManager;
import ch.ivyteam.ivy.business.data.store.search.internal.elasticsearch.server.ServerConfig;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class SearchEngineBean
{
  @Inject
  private ElasticSearchServerManager searchEngine;
  
  @Inject
  private ServerConfig serverConfig;
  
  private List<SearchEngineIndex> indices;
  private List<SearchEngineIndex> filteredIndices;
  private String filter;
  
  public SearchEngineBean()
  {
    DiCore.getGlobalInjector().injectMembers(this);
    indices = searchEngine.getBusinessDataIndices().stream()
            .map(index -> new SearchEngineIndex(index,
                    searchEngine.countIndexed(index),
                    searchEngine.countStored(index),
                    "unknown"))
            .collect(Collectors.toList());
  }
  
  public List<SearchEngineIndex> getFilteredIndicies()
  {
    return filteredIndices;
  }

  public void setFilteredIndicies(List<SearchEngineIndex> filteredIndices)
  {
    this.filteredIndices = filteredIndices;
  }
  
  public String getFilter()
  {
    return filter;
  }
  
  public void setFilter(String filter)
  {
    this.filter = filter;
  }
  
  public String getName()
  {
    return searchEngine.getName();
  }
  
  public String getUrl()
  {
    return serverConfig.getServerUrl();
  }
  
  public String getVersion()
  {
    return ElasticSearchUtil.getVersion(getUrl());
  }
  
  public boolean getState()
  {
    return searchEngine.getElasticsearchServer().testConnection() == null;
  }
  
  public List<SearchEngineIndex> getIndices()
  {
    return indices;
  }
  
  public void reindexAll()
  {
    indices.forEach(index -> searchEngine.reindex(index.getIndex()));
  }
}
