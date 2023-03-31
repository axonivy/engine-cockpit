package ch.ivyteam.enginecockpit.services.search.index;

import org.apache.commons.lang3.StringUtils;

public class SearchIndexDoc {

  private final String id;
  private final String json;

  public SearchIndexDoc(String id, String json) {
    this.id = id;
    this.json = json;
  }

  public String getId() {
    return id;
  }

  public String getJson() {
    return json;
  }

  public String getJsonShort() {
    return StringUtils.abbreviate(json, 500);
  }
}
