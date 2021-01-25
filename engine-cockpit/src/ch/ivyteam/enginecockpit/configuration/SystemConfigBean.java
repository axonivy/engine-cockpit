package ch.ivyteam.enginecockpit.configuration;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.ContentFilter;
import ch.ivyteam.enginecockpit.model.ConfigProperty;

@ManagedBean
@ViewScoped
public class SystemConfigBean
{
  private ConfigViewImpl configView;

  public SystemConfigBean()
  {
    configView = new ConfigViewImpl(List.of(
            ConfigViewImpl.defaultFilter(),
            new ContentFilter<ConfigProperty>("Security Systems", "Show Security Systems", 
                    p -> !StringUtils.startsWith(p.getKey(), "SecuritySystems."), true)));
  }

  public ConfigViewImpl getConfigView()
  {
    return configView;
  }
  
}
