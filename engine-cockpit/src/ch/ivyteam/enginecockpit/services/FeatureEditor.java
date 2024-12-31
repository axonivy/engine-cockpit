package ch.ivyteam.enginecockpit.services;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.commons.Feature;

public interface FeatureEditor {

  List<Feature> getFeatures();

  Feature getFeature();

  void saveFeature(boolean isNewFeature);

  default Feature findFeature(String clazz) {
    return getFeatures().stream()
        .filter(feature -> feature.getClazz().equals(clazz))
        .findFirst()
        .orElse(new Feature());
  }

  void setFeature(String key);

  default boolean isExistingFeature() {
    for (Feature feature : getFeatures()) {
      if (feature.getClazz().equals(getFeature().getClazz())) {
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
