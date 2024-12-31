package ch.ivyteam.enginecockpit.services.search.index;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import ch.ivyteam.enginecockpit.services.search.SearchEngineIndex;
import ch.ivyteam.enginecockpit.services.search.SearchEngineService;

public class SearchIndexDocDataModel extends LazyDataModel<SearchIndexDoc> {

  private final SearchEngineIndex index;

  public SearchIndexDocDataModel(SearchEngineIndex index) {
    this.index = index;
  }

  @Override
  public int count(Map<String, FilterMeta> filterBy) {
    var filter = filter(filterBy);
    return (int) SearchEngineService.instance().search(index, filter, 0, 0).getTotalHitCount();
  }

  @Override
  public List<SearchIndexDoc> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
    var filter = filter(filterBy);
    var result = SearchEngineService.instance().search(index, filter, first, pageSize);
    return result.getDocs()
        .map(doc -> new SearchIndexDoc(doc.getId(), doc.getJson()))
        .collect(Collectors.toList());
  }

  private String filter(Map<String, FilterMeta> filterBy) {
    FilterMeta filterMeta = filterBy.get("globalFilter");
    if (filterMeta != null) {
      return filterMeta.getFilterValue().toString();
    }
    return "";
  }
}
