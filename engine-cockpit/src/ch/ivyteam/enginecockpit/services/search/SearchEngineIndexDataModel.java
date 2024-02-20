package ch.ivyteam.enginecockpit.services.search;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import ch.ivyteam.ivy.elasticsearch.IElasticsearchManager;
import ch.ivyteam.ivy.elasticsearch.index.IndexInfo;

public class SearchEngineIndexDataModel extends LazyDataModel<SearchEngineIndex> {

  public static final IElasticsearchManager searchEngine = IElasticsearchManager.instance();

  @Override
  public int count(Map<String, FilterMeta> filterBy) {
    return (int) searchEngine.indicesCount();
  }

  @Override
  public List<SearchEngineIndex> load(int first, int pageSize, Map<String, SortMeta> sortBy,
          Map<String, FilterMeta> filterBy) {
    return searchEngine.indices(first, pageSize).stream()
            .map(index -> toSearchEngineIndex(index))
            .collect(Collectors.toList());
  }

  public static SearchEngineIndex toSearchEngineIndex(IndexInfo index) {
    return new SearchEngineIndex(index, searchEngine.isReindexing(index.indexName()));
  }
}
