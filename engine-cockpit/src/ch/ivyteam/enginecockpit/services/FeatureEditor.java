package ch.ivyteam.enginecockpit.services;

import java.util.List;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.commons.Feature;

public interface FeatureEditor {

  List<Feature> getFeatures();

  void setEditFeature(String key);

  Feature getEditFeature();

  void saveEditFeature(boolean isNewFeature);

  void removeFeature(String clazz);

  default Feature findFeature(String clazz) {
    return getFeatures().stream()
        .filter(feature -> feature.getClazz().equals(clazz))
        .findFirst()
        .orElse(new Feature());
  }

  default boolean isExistingFeatureThrowMessage() {
    for (Feature feature : getFeatures()) {
      if (feature.getClazz().equals(getEditFeature().getClazz())) {
        var msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
            getEditFeature() + " couldn't be created",
            "Because this Feature already exists.");
        FacesContext.getCurrentInstance().addMessage("msg", msg);
        return true;
      }
    }
    return false;
  }
}
