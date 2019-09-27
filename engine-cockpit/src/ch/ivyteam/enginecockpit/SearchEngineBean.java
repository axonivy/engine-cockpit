package ch.ivyteam.enginecockpit;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import ch.ivyteam.di.restricted.DiCore;
import ch.ivyteam.enginecockpit.model.ElasticSearch;
import ch.ivyteam.enginecockpit.model.SearchEngineIndex;
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
  
  private ElasticSearch elasticSearch;
  
  private List<SearchEngineIndex> indices;
  private List<SearchEngineIndex> filteredIndices;
  private String filter;
  
  private SearchEngineIndex activeIndex;

  public SearchEngineBean()
  {
    DiCore.getGlobalInjector().injectMembers(this);
    elasticSearch = new ElasticSearch(serverConfig.getServerUrl());
    
    indices = searchEngine.getBusinessDataIndices().stream()
            .map(index -> new SearchEngineIndex(index,
                    searchEngine.countIndexed(index),
                    searchEngine.countStored(index),
                    serverConfig.getServerUrl()))
            .collect(Collectors.toList());
    elasticSearch.evaluateAliasForIndices(indices);
    elasticSearch.evaluateAdditionalIndicesInformation(indices);
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
  
  public ElasticSearch getElasticSearch()
  {
    return elasticSearch;
  }
  
  public boolean getState()
  {
    return searchEngine.getElasticsearchServer().testConnection() == null;
  }
  
  public List<SearchEngineIndex> getIndices()
  {
    return indices;
  }
  
  public void setActiveIndex(SearchEngineIndex index)
  {
    this.activeIndex = index;
  }
  
  public SearchEngineIndex getActiveIndex()
  {
    return activeIndex;
  }
  
  public void reindex()
  {
    searchEngine.reindex(activeIndex.getIndexName());
  }
}
