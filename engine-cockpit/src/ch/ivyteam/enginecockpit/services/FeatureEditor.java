package ch.ivyteam.enginecockpit.services;

import java.util.List;

public interface FeatureEditor {
  
  List<String> getFeatures();
  
  String getFeature();
  
  void saveFeature();
  
  void setFeature(String key);
  
}
