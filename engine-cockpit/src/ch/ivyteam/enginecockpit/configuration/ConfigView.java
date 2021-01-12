package ch.ivyteam.enginecockpit.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.ContentFilter;
import ch.ivyteam.enginecockpit.model.ConfigProperty;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@SuppressWarnings("restriction")
public class ConfigView implements ContentFilter
{
  private static final String DEFINED_FILTER = "defined";
  private List<ConfigProperty> configs;
  private List<ConfigProperty> filteredConfigs;
  private String filter;
  private boolean showDefaults;
  private ConfigProperty activeConfig;
  private IConfiguration configuration;
  private List<SelectItem> contentFilters;
  private List<String> selectedContentFilters;
  private Function<ConfigProperty, ConfigProperty> propertyEnricher;

  public ConfigView()
  {
    this(IConfiguration.instance(), c -> c);
  }

  public ConfigView(IConfiguration configuration, Function<ConfigProperty, ConfigProperty> propertyEnricher)
  {
    this.configuration = configuration;
    this.propertyEnricher = propertyEnricher;
    reloadConfigs();
    showDefaults = true;
    loadContentFilters();
  }

  private void reloadConfigs()
  {
    configs = configuration.getProperties().stream()
            .filter(property -> !StringUtils.startsWith(property.getKey(), "Applications."))
            .filter(property -> !StringUtils.startsWith(property.getKey(), "SecuritySystems."))
            .map(property -> new ConfigProperty(property))
            .map(propertyEnricher)
            .collect(Collectors.toList());
  }

  public List<ConfigProperty> getConfigs()
  {
    return filterDefault(configs);
  }

  private List<ConfigProperty> filterDefault(List<ConfigProperty> properties)
  {
    if (showDefaults)
    {
      return properties;
    }
    else
    {
      return properties.stream().filter(c -> !c.isDefault()).collect(Collectors.toList());
    }
  }

  public List<ConfigProperty> getFilteredConfigs()
  {
    if (StringUtils.isNotBlank(filter))
    {
      return filterDefault(filteredConfigs);
    }
    return filteredConfigs;
  }

  public void setFilteredConfigs(List<ConfigProperty> filteredConfigs)
  {
    this.filteredConfigs = filteredConfigs;
  }
  
  public String getFilter()
  {
    return filter;
  }
  
  public void setFilter(String filter)
  {
    this.filter = filter;
  }
  
  public boolean isShowDefaults() 
  {
    return showDefaults;
  }
  
  public void setShowDefaults(boolean showDefaults)
  {
    this.showDefaults = showDefaults;
  }
  
  public void switchDefaults()
  {
    showDefaults = !showDefaults;
  }
  
  public void loadContentFilters()
  {
    contentFilters = new ArrayList<>();
    contentFilters.add(new SelectItem(DEFINED_FILTER, "Show only defined values"));
  }
  
  @Override
  public List<SelectItem> getContentFilters()
  {
    return contentFilters;
  }
  
  @Override
  public List<String> getSelectedContentFilters()
  {
    return selectedContentFilters;
  }
  
  @Override
  public void setSelectedContentFilters(List<String> selectedContentFilters)
  {
    this.selectedContentFilters = selectedContentFilters;
    showDefaults = !selectedContentFilters.contains(DEFINED_FILTER);
  }
  
  @Override
  public void resetSelectedContentFilters()
  {
    setSelectedContentFilters(Collections.emptyList());
  }
  
  @Override
  public String getContentFilterText()
  {
    if (showDefaults)
    {
      return "all values";
    }
    return "defined values";
  }
  
  public void setActiveConfig(String configKey)
  {
    this.activeConfig = configs.stream().filter(c -> StringUtils.equals(c.getKey(), configKey)).findFirst().orElse(new ConfigProperty());
  }
  
  public ConfigProperty getActiveConfig()
  {
    return activeConfig;
  }
  
  public void resetConfig()
  {
    configuration.remove(activeConfig.getKey());
    if (filteredConfigs != null)
    {
      filteredConfigs.remove(activeConfig);
    }
    reloadAndUiMessage("reset");
  }

  public void saveConfig()
  {
    configuration.set(activeConfig.getKey(), activeConfig.getValue());
    reloadAndUiMessage("changed");
  }
  
  public void createConfig()
  {
    configuration.set(activeConfig.getKey(), activeConfig.getValue());
    reloadAndUiMessage("created");
  }
  
  private void reloadAndUiMessage(String message)
  {
    reloadConfigs();
    FacesContext.getCurrentInstance().addMessage("msgs",
            new FacesMessage("'" + activeConfig.getKey() + "' " + message));
  }
}
