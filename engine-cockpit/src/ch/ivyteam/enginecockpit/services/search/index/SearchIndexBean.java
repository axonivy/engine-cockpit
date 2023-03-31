package ch.ivyteam.enginecockpit.services.search.index;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.services.search.SearchEngineIndex;
import ch.ivyteam.enginecockpit.services.search.SearchEngineService;

@ManagedBean
@ViewScoped
public class SearchIndexBean {

  private String index;
  private SearchEngineIndex searchIndex;
  private SearchIndexDocDataModel model;
  private String filter;

  public void onload() {
    searchIndex = SearchEngineService.instance().getIndices()
            .filter(in -> in.getName().equals(index))
            .findAny()
            .orElse(null);
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
