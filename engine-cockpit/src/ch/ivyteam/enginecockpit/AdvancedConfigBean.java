package ch.ivyteam.enginecockpit;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.model.ConfigProperty;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@ManagedBean
@ViewScoped
@SuppressWarnings("restriction")
public class AdvancedConfigBean
{
  private List<ConfigProperty> configs;
  private List<ConfigProperty> filteredConfigs;

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
}
