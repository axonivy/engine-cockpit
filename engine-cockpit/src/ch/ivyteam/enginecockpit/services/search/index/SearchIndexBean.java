package ch.ivyteam.enginecockpit.services.search.index;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.services.search.SearchEngineIndex;
import ch.ivyteam.enginecockpit.services.search.SearchEngineIndexDataModel;
import ch.ivyteam.ivy.searchengine.ISearchEngineManager;

@ManagedBean
@ViewScoped
public class SearchIndexBean {

  private final ISearchEngineManager searchEngine = ISearchEngineManager.instance();

  private String index;
  private SearchEngineIndex searchIndex;
  private SearchIndexDocDataModel model;
  private String filter;

  public void onload() {
    searchIndex = SearchEngineIndexDataModel.toSearchEngineIndex(searchEngine.indexByName(index));
    if (searchIndex == null) {
      ResponseHelper.notFound("Search index '" + index + "' not found");
      return;
    }
    model = new SearchIndexDocDataModel(searchIndex);
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public SearchIndexDocDataModel getDataModel() {
    return model;
  }

  public SearchEngineIndex getSearchIndex() {
    return searchIndex;
  }

  public String getIndex() {
    return index;
  }

  public void setIndex(String index) {
    this.index = index;
  }
}
