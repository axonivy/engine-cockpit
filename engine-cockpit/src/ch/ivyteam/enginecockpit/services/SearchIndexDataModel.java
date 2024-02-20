package ch.ivyteam.enginecockpit.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import ch.ivyteam.enginecockpit.services.model.SearchEngineIndex;
import ch.ivyteam.ivy.business.data.store.restricted.IBusinessDataManager;

@SuppressWarnings("restriction")
public class SearchIndexDataModel extends LazyDataModel<SearchEngineIndex> {

  private IBusinessDataManager searchEngine = IBusinessDataManager.instance();

  @Override
  public int count(Map<String, FilterMeta> filterBy) {
    return (int) searchEngine.getBusinessDataIndicesCount();
  }

  @Override
  public List<SearchEngineIndex> load(int first, int pageSize, Map<String, SortMeta> sortBy,
          Map<String, FilterMeta> filterBy) {
    return searchEngine.getBusinessDataIndicesInfo(first, pageSize).stream()
            .map(index -> new SearchEngineIndex(index, searchEngine.isReindexing(index.getIndexName())))
            .collect(Collectors.toList());
  }
}
