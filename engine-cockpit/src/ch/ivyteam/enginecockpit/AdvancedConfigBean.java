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
  private ConfigProperty activeConfig;

  public AdvancedConfigBean()
  {
    reloadConfigs();
  }

  private void reloadConfigs()
  {
    configs = Configuration.getProperties().stream()
            .map(property -> new ConfigProperty(property))
            .collect(Collectors.toList());
  }

  public List<ConfigProperty> getConfigs()
  {
    return configs;
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
