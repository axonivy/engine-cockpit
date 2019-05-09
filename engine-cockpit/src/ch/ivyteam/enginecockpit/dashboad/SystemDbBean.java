package ch.ivyteam.enginecockpit.dashboad;

import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

import ch.ivyteam.enginecockpit.util.Configuration;
import ch.ivyteam.enginecockpit.util.SystemDbUtil;

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
    url = Configuration.getOrDefault(SystemDbUtil.URL);
    driver = Configuration.getOrDefault(SystemDbUtil.DRIVER);
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
