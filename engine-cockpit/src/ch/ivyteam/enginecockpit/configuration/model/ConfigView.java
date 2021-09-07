package ch.ivyteam.enginecockpit.configuration.model;

import java.util.List;

import org.primefaces.PrimeFaces;

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

  default void triggerTableFilter() {
    PrimeFaces.current().executeScript("PF('configTable').filter();");
  }

}
