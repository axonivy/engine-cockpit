package ch.ivyteam.enginecockpit.configuration;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class SystemConfigBean
{
  private ConfigView configView;

  public SystemConfigBean()
  {
    configView = new ConfigView();
  }

  public ConfigView getConfigView()
  {
    return configView;
  }
  
}
