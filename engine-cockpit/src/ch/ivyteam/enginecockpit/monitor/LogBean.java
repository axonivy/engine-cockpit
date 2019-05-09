package ch.ivyteam.enginecockpit.monitor;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.model.LogView;


@ManagedBean
@ViewScoped
public class LogBean
{
  private LogView ivyLog;
  private LogView consoleLog;
  private LogView configLog;
  
  public LogBean()
  {
    ivyLog = new LogView("ivy.log");
    consoleLog = new LogView("console.log");
    configLog = new LogView("config.log");
  }
  
  public LogView getIvyLog()
  {
    return ivyLog;
  }
  
  public LogView getConsoleLog()
  {
    return consoleLog;
  }
  
  public LogView getConfigLog()
  {
    return configLog;
  }
  
}