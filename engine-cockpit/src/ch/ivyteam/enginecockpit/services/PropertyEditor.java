package ch.ivyteam.enginecockpit.services;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.commons.Property;

public interface PropertyEditor {
  
  List<Property> getProperties();
  
  Property getProperty();
  
  void saveProperty(boolean isNewProperty);

  default Property findProperty(String key) {
    if (key != null) {
      return getProperties().stream()
          .filter(property -> property.getName().equals(key))
          .findFirst()
          .orElse(new Property());
    }
    return new Property();
  }

  default boolean isExistingProperty() {
    for (Property property : getProperties()) {
      if (property.getName().equals(getProperty().getName())) {
        var msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
            "Property '" + getProperty().getName() + "' couldn't be created",
            "Because a Property with the name " + getProperty().getName() + " already exists.");
        FacesContext.getCurrentInstance().addMessage("msg", msg);
        return true;
      }
    }
    return false;
  }

  void setProperty(String key);
  
  default boolean isSensitive() {
    if (getProperty() == null || getProperty().getName() == null || getProperties() == null) {
      return false;
    }
    return getProperties().stream()
        .filter(property -> getProperty().getName().equals(property.getName()))
        .findFirst()
        .map(Property::isSensitive)
        .orElse(false);
  }
}
