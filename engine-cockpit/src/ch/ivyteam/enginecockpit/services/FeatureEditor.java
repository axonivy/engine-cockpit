package ch.ivyteam.enginecockpit.services;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public interface FeatureEditor {
  
  List<String> getFeatures();
  
  String getFeature();
  
  void saveFeature();
  
  void setFeature(String key);
  
  default boolean isExistingFeature() {
    for (String feature : getFeatures()) {
      if (feature.equals(getFeature())) {
        var msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
            getFeature() + " couldn't be created",
            "Because this Feature already exists.");
        FacesContext.getCurrentInstance().addMessage("msg", msg);
        return true;
      }
    }
    return false;
  }
}
