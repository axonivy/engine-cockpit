package ch.ivyteam.enginecockpit.services.search;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.services.model.Elasticsearch;
import ch.ivyteam.enginecockpit.services.model.Elasticsearch.SearchEngineHealth;
import ch.ivyteam.ivy.elasticsearch.IElasticsearchManager;
import ch.ivyteam.ivy.elasticsearch.server.ServerConfig;

@ManagedBean
@ViewScoped
public class SearchEngineBean {

  private IElasticsearchManager searchEngine = IElasticsearchManager.instance();
  private Elasticsearch elasticSearch;
  private List<SearchEngineIndex> indices;
  private List<SearchEngineIndex> filteredIndices;
  private String filter;
  private SearchEngineIndex activeIndex;
  private String query;
  private String queryResult;

  public SearchEngineBean() {
    elasticSearch = new Elasticsearch(searchEngine.info(), searchEngine.watermark());
    indices = SearchEngineService.instance().getIndices().collect(Collectors.toList());
  }

  public List<SearchEngineIndex> getFilteredIndicies() {
    return filteredIndices;
  }

  public void setFilteredIndicies(List<SearchEngineIndex> filteredIndices) {
    this.filteredIndices = filteredIndices;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public Elasticsearch getElasticSearch() {
    return elasticSearch;
  }

  public boolean getState() {
    return elasticSearch.getHealth() != SearchEngineHealth.UNKNOWN;
  }

  public List<SearchEngineIndex> getIndices() {
    return indices;
  }

  public void setActiveIndex(SearchEngineIndex index) {
    this.activeIndex = index;
    this.query = "";
    this.queryResult = "";
  }

  public SearchEngineIndex getActiveIndex() {
    return activeIndex;
  }

  public String getQueryUrl() {
    var baseUrl = ServerConfig.instance().getServerUrl();
    return activeIndex == null ? baseUrl : baseUrl + "/" + activeIndex.getName();
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public String getQueryResult() {
    return queryResult;
  }

  public List<String> queryProposals(String value) {
    return getQueryApis().stream()
            .filter(api -> StringUtils.startsWith(api, value))
            .distinct()
            .collect(Collectors.toList());
  }

  private List<String> getQueryApis() {
    if (activeIndex == null) {
      return Elasticsearch.APIS.SEARCH;
    }
    return Elasticsearch.APIS.INDEX;
  }

  public void runQuery() {
    try {
      var path = "/";
      if (activeIndex != null) {
        path += activeIndex.getName() + "/";
      }
      path += query;
      elasticSearch
              .executeRequest(path)
              .ifPresent(result -> queryResult = result);
    } catch (Exception ex) {
      queryResult = ex.getMessage();
    }
  }

  public boolean renderIndexingCount(SearchEngineIndex index) {
    return searchEngine.isReindexing(index.getIndexName()) && index.getCountIndexing() > 0 && index.getCountIndexing() < index.getCountStored();
  }

  public void reindex() {
    searchEngine.reindex(activeIndex.getIndexName());
  }

  public boolean isReindexing() {
    return searchEngine.isReindexing();
  }

  public boolean isReindexing(SearchEngineIndex index) {
    return searchEngine.isReindexing(index.getIndexName());
  }
}
