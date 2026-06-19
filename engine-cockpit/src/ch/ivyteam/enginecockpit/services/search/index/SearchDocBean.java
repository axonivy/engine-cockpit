package ch.ivyteam.enginecockpit.services.search.index;

import java.io.Serializable;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class SearchDocBean implements Serializable {

  private SearchIndexDoc doc;

  public void setDoc(SearchIndexDoc doc) {
    this.doc = doc;
  }

  public SearchIndexDoc getDoc() {
    return doc;
  }
}
