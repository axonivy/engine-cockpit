package ch.ivyteam.enginecockpit.services.search.index;

import jakarta.inject.Named;

import java.io.Serializable;

import jakarta.faces.view.ViewScoped;

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
