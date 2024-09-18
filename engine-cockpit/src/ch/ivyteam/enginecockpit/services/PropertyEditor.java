package ch.ivyteam.enginecockpit.services;

import java.util.List;

import ch.ivyteam.enginecockpit.commons.Property;

public interface PropertyEditor {
  
  List<Property> getProperties();
  
  Property getProperty();
  
  void saveProperty();

  default Property findProperty(String key) {
    if (key != null) {
      return getProperties().stream()
          .filter(property -> property.getName().equals(key))
          .findFirst()
          .orElse(new Property());
    } 
    return new Property();
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
