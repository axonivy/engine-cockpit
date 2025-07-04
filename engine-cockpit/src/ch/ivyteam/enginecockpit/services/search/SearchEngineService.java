package ch.ivyteam.enginecockpit.services.search;

import ch.ivyteam.ivy.searchengine.client.SearchEngineClient;
import ch.ivyteam.ivy.searchengine.client.SearchEngineClientSearcher.Result;

public class SearchEngineService {

  private static SearchEngineService INSTANCE = new SearchEngineService();

  public static SearchEngineService instance() {
    return INSTANCE;
  }

  public Result search(SearchEngineIndex index, String filter, int from, int size) {
    var searcher = SearchEngineClient.instance().search(index.getIndexName());
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
