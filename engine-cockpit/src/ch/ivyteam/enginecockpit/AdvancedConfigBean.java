package ch.ivyteam.enginecockpit;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.SelectEvent;

import ch.ivyteam.enginecockpit.model.ConfigProperty;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@ManagedBean
@ViewScoped
@SuppressWarnings("restriction")
public class AdvancedConfigBean
{
  private List<ConfigProperty> configs;
  private List<ConfigProperty> filteredConfigs;
  private ConfigProperty selectedConfig;

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

  public ConfigProperty getSelectedConfig()
  {
    return selectedConfig;
  }

  public void setSelectedConfig(ConfigProperty selectedConfig)
  {
    this.selectedConfig = selectedConfig;
  }

  public List<ConfigProperty> getFilteredConfigs()
  {
    return filteredConfigs;
  }

  public void setFilteredConfigs(List<ConfigProperty> filteredConfigs)
  {
    this.filteredConfigs = filteredConfigs;
  }

  public void onRowSelect(SelectEvent event)
  {
    FacesMessage msg = new FacesMessage("Config Selected", ((ConfigProperty) event.getObject()).getKey());
    FacesContext.getCurrentInstance().addMessage(null, msg);
  }
}
