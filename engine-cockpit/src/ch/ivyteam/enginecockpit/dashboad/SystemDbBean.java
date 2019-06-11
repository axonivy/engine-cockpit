package ch.ivyteam.enginecockpit.dashboad;

import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

import ch.ivyteam.enginecockpit.util.SystemDbUtil;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class SystemDbBean
{
  private String url;
  private String driver;
  
  public SystemDbBean()
  {
    initSystemDbConfigs();
  }
  
  private void initSystemDbConfigs()
  {
    url = IConfiguration.get().getOrDefault(SystemDbUtil.URL);
    driver = IConfiguration.get().getOrDefault(SystemDbUtil.DRIVER);
  }

  public String getUrl()
  {
    return url;
  }

  public String getDriver()
  {
    return driver;
  }
  
}
