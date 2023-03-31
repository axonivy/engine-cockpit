package ch.ivyteam.enginecockpit.services.search;

import java.util.stream.Stream;

import ch.ivyteam.ivy.elasticsearch.IElasticsearchManager;
import ch.ivyteam.ivy.elasticsearch.client.EsClient;
import ch.ivyteam.ivy.elasticsearch.client.EsClientSearcher.Result;

public class SearchEngineService {

  private static SearchEngineService INSTANCE = new SearchEngineService();

  private final IElasticsearchManager searchEngine = IElasticsearchManager.instance();

  public Stream<SearchEngineIndex> getIndices() {
    return searchEngine.indices().stream()
            .map(index -> new SearchEngineIndex(index, searchEngine.isReindexing(index.indexName())));
  }

  public static SearchEngineService instance() {
    return INSTANCE;
  }

  public Result search(SearchEngineIndex index, String filter, int from, int size) {
    var searcher = EsClient.instance().search(index.getIndexName());
    if (filter.isBlank()) {
      filter = "*";
    }
    return searcher.search("""
                           {
                             "from": ${from},
                             "size": ${size},
                             "query": {
                               "simple_query_string": {
                                 "query": "${query}"
                              }
                           }}
                           """
                            .replace("${from}", String.valueOf(from))
                            .replace("${size}", String.valueOf(size))
                            .replace("${query}", filter));
  }
}
