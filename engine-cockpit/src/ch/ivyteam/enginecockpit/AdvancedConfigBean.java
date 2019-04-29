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
import ch.ivyteam.ivy.system.UserInterfaceFormat;

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
    showDefaults = true;
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
    if (activeConfig.getValue().equals(activeConfig.getDefaultValue()))
    {
      deleteConfig();
      return;
    }
    if (UserInterfaceFormat.PASSWORD.name().equals(activeConfig.getUserInterfaceFormat()))
    {
      Configuration.set(activeConfig.getKey(), Configuration.encrpyt(activeConfig.getValue()));
    }
    else if (UserInterfaceFormat.NUMBER.name().equals(activeConfig.getUserInterfaceFormat()))
    {
      Configuration.set(activeConfig.getKey(), Long.valueOf(activeConfig.getValue()));
    }
    else if (UserInterfaceFormat.TRUE_FALSE.name().equals(activeConfig.getUserInterfaceFormat()) ||
            UserInterfaceFormat.YES_NO.name().equals(activeConfig.getUserInterfaceFormat()) ||
            UserInterfaceFormat.ON_OFF.name().equals(activeConfig.getUserInterfaceFormat()))
    {
      Configuration.set(activeConfig.getKey(), Boolean.valueOf(activeConfig.getValue()));
    }
    else
    {
      Configuration.set(activeConfig.getKey(), activeConfig.getValue());
    }
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
