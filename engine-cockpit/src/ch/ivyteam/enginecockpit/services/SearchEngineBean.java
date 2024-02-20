package ch.ivyteam.enginecockpit.services;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import ch.ivyteam.enginecockpit.services.model.ElasticSearch;
import ch.ivyteam.enginecockpit.services.model.ElasticSearch.SearchEngineHealth;
import ch.ivyteam.enginecockpit.services.model.SearchEngineIndex;
import ch.ivyteam.ivy.business.data.store.restricted.IBusinessDataManager;
import ch.ivyteam.ivy.business.data.store.search.restricted.elasticsearch.server.ServerConfig;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class SearchEngineBean {
  private IBusinessDataManager searchEngine = IBusinessDataManager.instance();
  private ServerConfig serverConfig = ServerConfig.instance();

  private ElasticSearch elasticSearch;

  private String filter;

  private SearchEngineIndex activeIndex;

  private String query;
  private String queryResult;

  private SearchIndexDataModel model;

  public SearchEngineBean() {
    elasticSearch = new ElasticSearch(serverConfig.getServerUrl(),
            searchEngine.getBusinessDataInfo());
    model = new SearchIndexDataModel();
  }

  public SearchIndexDataModel getModel() {
    return model;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public ElasticSearch getElasticSearch() {
    return elasticSearch;
  }

  public boolean getState() {
    return elasticSearch.getHealth() != SearchEngineHealth.UNKNOWN;
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
    if (activeIndex != null) {
      return elasticSearch.getServerUrl() + "/" + activeIndex.getName();
    }
    return elasticSearch.getServerUrl();
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
            .map(api -> StringUtils.removeStart(api, "/"))
            .filter(api -> StringUtils.startsWith(api, value))
            .distinct()
            .collect(Collectors.toList());
  }

  private List<String> getQueryApis() {
    if (activeIndex == null) {
      return Arrays.asList(ElasticSearch.ElasticSearchApi.ALIASES_URL,
              ElasticSearch.ElasticSearchApi.HEALTH_URL,
              ElasticSearch.ElasticSearchApi.INDICIES_URL);
    }
    return Arrays.asList(ElasticSearch.ElasticSearchIndexApi.MAPPING_URL);
  }

  public void runQuery() {
    try {
      elasticSearch.executeRequest(getQueryUrl() + "/" + getQuery())
              .ifPresent(result -> queryResult = tryToBeutifyQueryResult(result));

    } catch (Exception ex) {
      queryResult = ex.getMessage();
    }
  }

  private String tryToBeutifyQueryResult(String result) {
    try {
      var gson = new GsonBuilder().setPrettyPrinting().create();
      return gson.toJson(JsonParser.parseString(result));
    } catch (Exception e) {
      return result;
    }
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
