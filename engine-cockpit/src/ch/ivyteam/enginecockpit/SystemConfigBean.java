package ch.ivyteam.enginecockpit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.model.ConfigProperty;
import ch.ivyteam.enginecockpit.util.Configuration;
import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.configuration.restricted.ConfigValueFormat;
import ch.ivyteam.ivy.environment.Ivy;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class SystemConfigBean
{
  private List<ConfigProperty> configs;
  private List<ConfigProperty> filteredConfigs;
  private String filter;
  private boolean showDefaults;
  private ConfigProperty activeConfig;

  public SystemConfigBean()
  {
    reloadConfigs();
    showDefaults = true;
  }

  private void reloadConfigs()
  {
    configs = Configuration.getProperties().stream()
            .filter(property -> !StringUtils.startsWith(property.getKey(), "Applications."))
            .filter(property -> !StringUtils.startsWith(property.getKey(), "SecuritySystems."))
            .map(property -> new ConfigProperty(property))
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
    Configuration.remove(activeConfig.getKey());
    if (StringUtils.isNotBlank(filter))
    {
      filteredConfigs.remove(activeConfig);
    }
    reloadAndUiMessage("reseted");
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
      Configuration.set(activeConfig.getKey(), Long.valueOf(activeConfig.getValue()));
    }
    else if (ConfigValueFormat.BOOLEAN.name().equals(activeConfig.getConfigValueFormat()))
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
  
  public StreamedContent downloadFile()
  {
    File logFile = UrlUtil.getLogFile("config.log");
    Ivy.log().info(logFile.exists());
    if (logFile.exists())
    {
      try
      {
        Ivy.log().info(FileUtils.readFileToString(logFile));
        InputStream newInputStream = Files.newInputStream(logFile.toPath());
        return new DefaultStreamedContent(newInputStream, "text/plain", logFile.getName());
      }
      catch (IOException e)
      {
        Ivy.log().info(e.getMessage());
        FacesContext.getCurrentInstance().addMessage("msgs",
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to load file: " + logFile.getName()));
      }
    }
    else 
    {
      FacesContext.getCurrentInstance().addMessage("msgs",
              new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Log file " + logFile.getName() + " does not exists."));
    }
    return null;
  }
}
