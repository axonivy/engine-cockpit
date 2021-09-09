package ch.ivyteam.enginecockpit.configuration;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.PrimeFaces;

import ch.ivyteam.enginecockpit.model.ConfigProperty;
import ch.ivyteam.ivy.configuration.internal.Configuration;
import ch.ivyteam.ivy.configuration.restricted.ConfigValueFormat;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@SuppressWarnings("restriction")
public class ConfigView
{
  private List<ConfigProperty> configs;
  private List<ConfigProperty> filteredConfigs;
  private String filter;
  private boolean showDefaults;
  private ConfigProperty activeConfig;
  private IConfiguration configuration;
  private Function<ConfigProperty, ConfigProperty> propertyEnricher;

  public ConfigView()
  {
    this(IConfiguration.get(), c -> c);
  }

  public ConfigView(IConfiguration configuration, Function<ConfigProperty, ConfigProperty> propertyEnricher)
  {
    this.configuration = configuration;
    this.propertyEnricher = propertyEnricher;
    reloadConfigs();
    showDefaults = true;
  }

  private void reloadConfigs()
  {
    ((Configuration) configuration).blockUntilReloaded();
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
    if (activeConfig.getValue().equals(activeConfig.getDefaultValue()))
    {
      resetConfig();
      return;
    }
    else if (ConfigValueFormat.NUMBER.name().equals(activeConfig.getConfigValueFormat()))
    {
      configuration.set(activeConfig.getKey(), Long.valueOf(activeConfig.getValue()));
    }
    else if (ConfigValueFormat.BOOLEAN.name().equals(activeConfig.getConfigValueFormat()))
    {
      configuration.set(activeConfig.getKey(), Boolean.valueOf(activeConfig.getValue()));
    }
    else
    {
      configuration.set(activeConfig.getKey(), activeConfig.getValue());
    }
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
    PrimeFaces.current().executeScript("PF('configTable').filter();");
  }
  
}
