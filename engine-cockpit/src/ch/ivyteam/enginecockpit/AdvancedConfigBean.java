package ch.ivyteam.enginecockpit;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.model.ConfigProperty;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.environment.Ivy;

@ManagedBean
@ViewScoped
@SuppressWarnings("restriction")
public class AdvancedConfigBean
{
  private List<ConfigProperty> configs;
  private List<ConfigProperty> filteredConfigs;
  private ConfigProperty activeConfig;

  public AdvancedConfigBean()
  {
    configs = IConfiguration.get().getProperties().stream()
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
  
  public void setActiveConfig(ConfigProperty config)
  {
    Ivy.log().info(config.getKey());
    this.activeConfig = config;
  }
  
  public ConfigProperty getActiveConfig()
  {
    return activeConfig;
  }
  
  public void deleteConfig()
  {
    //TODO: delete config
    FacesContext.getCurrentInstance().addMessage("msgs",
            new FacesMessage("'" + activeConfig.getKey() + "' deleted successfully"));
  }
  
  public void saveConfig()
  {
    //TODO: save config
    FacesContext.getCurrentInstance().addMessage("msgs",
            new FacesMessage("'" + activeConfig.getKey() + "' changed successfully"));
  }
}
