package ch.ivyteam.enginecockpit.configuration;

import java.util.List;

import ch.ivyteam.enginecockpit.model.ConfigProperty;

public interface ConfigView
{

  List<ConfigProperty> getConfigs();
  List<ConfigProperty> getFilteredConfigs();
  void setFilteredConfigs(List<ConfigProperty> filteredConfigs);
  String getFilter();
  void setFilter(String filter);
  void setActiveConfig(String configKey);
  ConfigProperty getActiveConfig();
  void resetConfig();
  void saveConfig();

}
