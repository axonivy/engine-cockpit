package ch.ivyteam.enginecockpit;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.model.ConfigProperty;
import ch.ivyteam.enginecockpit.util.Configuration;

@ManagedBean
@ViewScoped
public class AdvancedConfigBean
{
  private List<ConfigProperty> configs;
  private List<ConfigProperty> filteredConfigs;
  private String filter;
  private boolean showDefaults;
  private ConfigProperty activeConfig;

  public AdvancedConfigBean()
  {
    reloadConfigs();
    showDefaults = false;
  }

  private void reloadConfigs()
  {
    configs = Configuration.getProperties().stream()
            .map(property -> new ConfigProperty(property))
            .collect(Collectors.toList());
  }

  public List<ConfigProperty> getConfigs()
  {
    if (showDefaults)
    {
      return configs;
    }
    else
    {
      return configs.stream().filter(c -> !c.isDefault()).collect(Collectors.toList());
    }
  }

  public List<ConfigProperty> getFilteredConfigs()
  {
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
  
  public void setActiveConfig(ConfigProperty config)
  {
    if (config == null)
    {
      this.activeConfig = new ConfigProperty();
    }
    else
    {
      this.activeConfig = config;
    }
  }
  
  public ConfigProperty getActiveConfig()
  {
    return activeConfig;
  }
  
  public void deleteConfig()
  {
    Configuration.remove(activeConfig.getKey());
    if (StringUtils.isNotBlank(filter))
    {
      filteredConfigs.remove(activeConfig);
    }
    reloadAndUiMessage("deleted");
  }

  public void saveConfig()
  {
    Configuration.set(activeConfig.getKey(), activeConfig.getValue());
    reloadAndUiMessage("changed");
  }
  
  public void createConfig()
  {
    Configuration.set(activeConfig.getKey(), activeConfig.getValue());
    reloadAndUiMessage("created");
  }
  
  private void reloadAndUiMessage(String message)
  {
    reloadConfigs();
    FacesContext.getCurrentInstance().addMessage("msgs",
            new FacesMessage("'" + activeConfig.getKey() + "' " + message));
  }
}
