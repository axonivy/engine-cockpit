package ch.ivyteam.enginecockpit.services.search.index;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class SearchDocBean {

  private SearchIndexDoc doc;

  public void setDoc(SearchIndexDoc doc) {
    this.doc = doc;
  }

  public SearchIndexDoc getDoc() {
    return doc;
  }
}
