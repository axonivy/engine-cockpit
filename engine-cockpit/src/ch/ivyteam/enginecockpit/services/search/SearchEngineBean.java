package ch.ivyteam.enginecockpit.services.search;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import ch.ivyteam.enginecockpit.services.model.SearchEngine;
import ch.ivyteam.enginecockpit.services.model.SearchEngine.SearchEngineHealth;
import ch.ivyteam.ivy.searchengine.ISearchEngineManager;
import ch.ivyteam.ivy.searchengine.manager.impl.SearchEngineManager;
import ch.ivyteam.ivy.searchengine.server.ServerConfig;

@ManagedBean
@ViewScoped
public class SearchEngineBean {

  private final SearchEngineManager searchEngineManager = (SearchEngineManager) ISearchEngineManager.instance();
  private SearchEngine searchEngine;
  private Exception esConnectionException;
  private String filter;
  private SearchEngineIndex activeIndex;
  private String query;
  private String queryResult;
  private final SearchEngineIndexDataModel model;

  public SearchEngineBean() {
    if (!hasFailure()) {
      searchEngine = new SearchEngine(searchEngineManager.info(), searchEngineManager.watermark());
    }
    model = new SearchEngineIndexDataModel();
  }

  public boolean hasFailure() {
    return hasBundledServerFailure() || hasConnectionFailure();
  }

  private boolean hasBundledServerFailure() {
    return searchEngineManager.getBundledServerStartupFailure() != null;
  }

  private boolean hasConnectionFailure() {
    return getConnectionException() != null;
  }

  private Exception getConnectionException() {
    if (esConnectionException != null) {
      return esConnectionException;
    }
    try {
      searchEngineManager.info();
    } catch (Exception ex) {
      esConnectionException = ex;
      return ex;
    }
    return null;
  }

  public String getFailure() {
    if (hasBundledServerFailure()) {
      return ExceptionUtils.getRootCauseMessage(searchEngineManager.getBundledServerStartupFailure());
    }
    return ExceptionUtils.getRootCauseMessage(getConnectionException());
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public SearchEngine getSearchEngine() {
    return searchEngine;
  }

  public boolean getState() {
    return searchEngine.getHealth() != SearchEngineHealth.UNKNOWN;
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
      return SearchEngine.APIS.SEARCH;
    }
    return SearchEngine.APIS.INDEX;
  }

  public SearchEngineIndexDataModel getIndicesModel() {
    return model;
  }

  public void runQuery() {
    try {
      var path = "/";
      if (activeIndex != null) {
        path += activeIndex.getName() + "/";
      }
      path += query;
      searchEngine
          .executeRequest(path)
          .ifPresent(result -> queryResult = result);
    } catch (Exception ex) {
      queryResult = ex.getMessage();
    }
  }

  public boolean renderIndexingCount(SearchEngineIndex index) {
    return searchEngineManager.isReindexing(index.getIndexName()) && index.getCountIndexing() > 0 && index.getCountIndexing() < index.getCountStored();
  }

  public void reindex() {
    searchEngineManager.reindex(activeIndex.getIndexName());
  }

  public boolean isReindexing() {
    return searchEngineManager.isReindexing();
  }

  public boolean isReindexing(SearchEngineIndex index) {
    return searchEngineManager.isReindexing(index.getIndexName());
  }
}
